package com.skybound.space.data.local.db

import androidx.room.RoomDatabase

abstract class BaseDatabase : RoomDatabase() {
    // App layer subclasses add @Database annotation and concrete DAO getters
}
