# WorkManager
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class androidx.work.impl.background.systemjob.SystemJobService { *; }
-keep class androidx.work.impl.background.systemalarm.SystemAlarmService { *; }
-keep class androidx.work.impl.background.systemalarm.ConstraintProxy$* { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker

# Firebase
-keep class com.google.firebase.** { *; }
-keep public class * extends com.google.firebase.components.ComponentRegistrar

# Startup
-keep class * extends androidx.startup.Initializer { *; }

# Room 3 (Multiplatform)
-keep class * extends androidx.room3.RoomDatabase
-keep class androidx.room3.RoomDatabase { *; }

# Room (Standard, used by WorkManager)
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.RoomDatabase { *; }
