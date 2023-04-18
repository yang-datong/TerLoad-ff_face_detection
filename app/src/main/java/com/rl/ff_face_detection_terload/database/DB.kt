package com.rl.ff_face_detection_terload.database

import android.content.Context
import androidx.room.Room

object DB {
    private var instance: UserDatabase? = null

    fun getInstance(context: Context): UserDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user"
            ).fallbackToDestructiveMigration()
                    .build().also { instance = it }
        }
    }
}