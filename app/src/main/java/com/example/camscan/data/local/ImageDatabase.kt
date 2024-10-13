package com.example.camscan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.camscan.data.model.ImageEntity

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ImageDatabase : RoomDatabase() {
  abstract fun imageDao(): ImageDao
}
