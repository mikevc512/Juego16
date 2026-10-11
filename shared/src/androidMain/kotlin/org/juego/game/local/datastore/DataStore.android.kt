package org.juego.game.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.juego.game.local.AndroidAppContext

actual fun getDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        AndroidAppContext.application.filesDir.resolve(dataStoreFileName).absolutePath
    },
)
