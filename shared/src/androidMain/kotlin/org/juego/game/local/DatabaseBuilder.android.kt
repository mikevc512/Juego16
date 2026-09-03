package org.juego.game.local

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.google.firebase.FirebaseApp

object AndroidAppContext {
    lateinit var application: Context

    fun initialize(context: Context) {
        application = context.applicationContext
        FirebaseApp.initializeApp(application)
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = AndroidAppContext.application
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}

actual fun currentEpochMs(): Long = System.currentTimeMillis()

private const val DATABASE_NAME = "juego16.db"
