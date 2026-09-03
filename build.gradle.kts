plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}

//dependencies {
//    add("kspAndroid", libs.androidx.room3.compiler)
//    add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
//    add("kspIosX64", libs.androidx.room3.compiler)
//    add("kspIosArm64", libs.androidx.room3.compiler)
//    // Add any other platform target you use in your project, for example kspDesktop
//}