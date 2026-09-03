#!/usr/bin/env python3
"""Generate every app icon asset from the Play Store icon.

Source of truth is docs/ic_launcher-1024.png, the same artwork uploaded to the
Play Console, so the launcher icon on device matches the store listing.

Produces:
  androidApp/src/main/res/mipmap-*/ic_launcher.png             legacy square
  androidApp/src/main/res/mipmap-*/ic_launcher_round.png       legacy round
  androidApp/src/main/res/mipmap-*/ic_launcher_foreground.png  adaptive foreground
  androidApp/src/main/res/mipmap-*/ic_launcher_monochrome.png  themed icon layer
  iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png

Run from the repository root:  python3 tools/generate_app_icons.py
"""

from collections import Counter
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "docs" / "ic_launcher-1024.png"
ANDROID_RES = ROOT / "androidApp" / "src" / "main" / "res"
IOS_ICON = (ROOT / "iosApp" / "iosApp" / "Assets.xcassets" / "AppIcon.appiconset"
            / "app-icon-1024.png")

# Density buckets: (folder suffix, multiplier against mdpi)
DENSITIES = [("mdpi", 1), ("hdpi", 1.5), ("xhdpi", 2), ("xxhdpi", 3), ("xxxhdpi", 4)]

LEGACY_DP = 48   # classic launcher icon
ADAPTIVE_DP = 108  # adaptive icon layer canvas

# The artwork is cropped to the square around its smallest enclosing circle, so
# scaling that square to N% of a canvas guarantees a circular mask of the same
# diameter cannot clip anything. An adaptive layer is 108dp but only the middle
# 72dp survives every launcher mask.
ADAPTIVE_CONTENT = 72 / 108
ROUND_CONTENT = 0.94
LEGACY_CORNER_RADIUS = 0.18  # fraction of the icon width

# Anti-aliasing headroom: everything is composed at 4x and downsampled.
SUPERSAMPLE = 4


def load_source():
    """Return the artwork, its background colour and the artwork bounding box."""
    image = Image.open(SOURCE).convert("RGB")
    pixels = image.load()
    width, height = image.size

    # The flat fill carries a little compression noise, so take the most common
    # border colour rather than trusting a single corner pixel.
    border = Counter()
    for x in range(width):
        border[pixels[x, 0]] += 1
        border[pixels[x, height - 1]] += 1
    for y in range(height):
        border[pixels[0, y]] += 1
        border[pixels[width - 1, y]] += 1
    background = border.most_common(1)[0][0]

    min_x, min_y, max_x, max_y = width, height, -1, -1
    for y in range(height):
        for x in range(width):
            r, g, b = pixels[x, y]
            distance = (abs(r - background[0]) + abs(g - background[1])
                        + abs(b - background[2]))
            if distance > 30:
                min_x, max_x = min(min_x, x), max(max_x, x)
                min_y, max_y = min(min_y, y), max(max_y, y)

    return image, background, (min_x, min_y, max_x + 1, max_y + 1)


def enclosing_circle(image, background, box):
    """Smallest circle covering the artwork, as (centre_x, centre_y, radius).

    A bounding box is not enough: the wordmark reaches the bottom corners, so
    sizing by the box lets a round mask shave its edges. Iteratively nudging the
    centre toward the farthest pixel converges on the true enclosing circle.
    """
    pixels = image.convert("RGB").load()
    points = []
    for y in range(box[1], box[3]):
        for x in range(box[0], box[2]):
            r, g, b = pixels[x, y]
            distance = (abs(r - background[0]) + abs(g - background[1])
                        + abs(b - background[2]))
            if distance > 30:
                points.append((x, y))

    centre_x = (box[0] + box[2]) / 2
    centre_y = (box[1] + box[3]) / 2
    for step in range(200):
        far_x, far_y, far_sq = centre_x, centre_y, -1.0
        for x, y in points:
            squared = (x - centre_x) ** 2 + (y - centre_y) ** 2
            if squared > far_sq:
                far_x, far_y, far_sq = x, y, squared
        shift = 1.0 / (step + 2)
        centre_x += (far_x - centre_x) * shift
        centre_y += (far_y - centre_y) * shift

    radius = max(((x - centre_x) ** 2 + (y - centre_y) ** 2) ** 0.5 for x, y in points)
    return centre_x, centre_y, radius


def square_around_circle(artwork, box, circle):
    """Crop the artwork to the square bounding its enclosing circle."""
    centre_x, centre_y, radius = circle
    left = round(centre_x - radius) - box[0]
    top = round(centre_y - radius) - box[1]
    side = round(radius * 2)

    square = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    square.alpha_composite(artwork, (-left, -top))
    return square


def cut_out_artwork(image, background, box):
    """Crop to the artwork and make the flat background transparent.

    The grid's white cells differ enough from the cream background to stay fully
    opaque, so only the surrounding fill is removed.
    """
    cropped = image.crop(box).convert("RGB")
    pixels = cropped.load()
    width, height = cropped.size
    alpha = Image.new("L", cropped.size)
    alpha_pixels = alpha.load()

    for y in range(height):
        for x in range(width):
            r, g, b = pixels[x, y]
            distance = (abs(r - background[0]) + abs(g - background[1])
                        + abs(b - background[2]))
            alpha_pixels[x, y] = min(255, int(distance * 255 / 40))

    result = cropped.convert("RGBA")
    result.putalpha(alpha)
    return result


def darkness_silhouette(image, background, box):
    """Crop to the artwork as a black silhouette weighted by how dark it is.

    Used for the Android 13+ themed icon, where only the alpha channel matters.
    """
    cropped = image.crop(box).convert("RGB")
    pixels = cropped.load()
    width, height = cropped.size

    def luminance(rgb):
        return 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]

    background_luma = luminance(background)
    darkest = 45.0
    span = max(background_luma - darkest, 1.0)

    alpha = Image.new("L", cropped.size)
    alpha_pixels = alpha.load()
    for y in range(height):
        for x in range(width):
            value = (background_luma - luminance(pixels[x, y])) / span
            alpha_pixels[x, y] = max(0, min(255, int(value * 255)))

    silhouette = Image.new("RGBA", cropped.size, (0, 0, 0, 255))
    silhouette.putalpha(alpha)
    return silhouette


def centered(canvas_size, artwork, content_fraction):
    """Scale artwork to the given fraction of the canvas and centre it."""
    limit = canvas_size * content_fraction
    scale = min(limit / artwork.width, limit / artwork.height)
    resized = artwork.resize(
        (max(1, round(artwork.width * scale)), max(1, round(artwork.height * scale))),
        Image.Resampling.LANCZOS,
    )
    canvas = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    canvas.alpha_composite(
        resized,
        ((canvas_size - resized.width) // 2, (canvas_size - resized.height) // 2),
    )
    return canvas


def rounded_square(image, size, radius_fraction):
    """Resize the full artwork to a square with rounded corners."""
    big = size * SUPERSAMPLE
    resized = image.convert("RGBA").resize((big, big), Image.Resampling.LANCZOS)
    mask = Image.new("L", (big, big), 0)
    ImageDraw.Draw(mask).rounded_rectangle(
        (0, 0, big - 1, big - 1), radius=int(big * radius_fraction), fill=255
    )
    resized.putalpha(mask)
    return resized.resize((size, size), Image.Resampling.LANCZOS)


def circular(artwork, background, size, content_fraction):
    """Artwork centred on a filled circle of the background colour."""
    big = size * SUPERSAMPLE
    canvas = Image.new("RGBA", (big, big), (0, 0, 0, 0))
    circle = Image.new("RGBA", (big, big), background + (255,))
    mask = Image.new("L", (big, big), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, big - 1, big - 1), fill=255)
    canvas.paste(circle, (0, 0), mask)
    canvas.alpha_composite(centered(big, artwork, content_fraction))
    return canvas.resize((size, size), Image.Resampling.LANCZOS)


def write(image, path):
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path, "PNG")
    print(f"  {path.relative_to(ROOT)}  {image.size[0]}x{image.size[1]}")


def main():
    image, background, box = load_source()
    print(f"source     {SOURCE.relative_to(ROOT)}  {image.size[0]}x{image.size[1]}")
    print("background #%02X%02X%02X" % background)
    print(f"artwork    {box[2] - box[0]}x{box[3] - box[1]} at {box[:2]}\n")

    circle = enclosing_circle(image, background, box)
    print("enclosing circle centre (%.0f, %.0f) radius %.0f\n" % circle)

    artwork = square_around_circle(cut_out_artwork(image, background, box), box, circle)
    silhouette = square_around_circle(
        darkness_silhouette(image, background, box), box, circle
    )

    for suffix, multiplier in DENSITIES:
        folder = ANDROID_RES / f"mipmap-{suffix}"
        legacy = round(LEGACY_DP * multiplier)
        adaptive = round(ADAPTIVE_DP * multiplier)

        write(rounded_square(image, legacy, LEGACY_CORNER_RADIUS),
              folder / "ic_launcher.png")
        write(circular(artwork, background, legacy, ROUND_CONTENT),
              folder / "ic_launcher_round.png")
        write(centered(adaptive, artwork, ADAPTIVE_CONTENT),
              folder / "ic_launcher_foreground.png")
        write(centered(adaptive, silhouette, ADAPTIVE_CONTENT),
              folder / "ic_launcher_monochrome.png")

    # iOS applies its own mask and rejects alpha, so ship the flat square.
    write(image.convert("RGB").resize((1024, 1024), Image.Resampling.LANCZOS), IOS_ICON)

    # The adaptive background layer must be the same cream as the artwork fill,
    # so it is written here instead of being maintained by hand.
    colour_file = ANDROID_RES / "values" / "ic_launcher_background.xml"
    colour_file.write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        "<resources>\n"
        '    <color name="ic_launcher_background">#%02X%02X%02X</color>\n' % background
        + "</resources>\n"
    )
    print(f"  {colour_file.relative_to(ROOT)}  #%02X%02X%02X" % background)


if __name__ == "__main__":
    main()
