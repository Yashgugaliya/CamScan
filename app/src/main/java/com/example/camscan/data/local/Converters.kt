package com.example.camscan.data.local

import androidx.room.TypeConverter

class Converters {
  @TypeConverter
  fun fromFaceRectanglesList(value: List<String>): String {
    return value.joinToString(",") // Convert the list to a comma-separated string
  }

  @TypeConverter
  fun toFaceRectanglesList(value: String): List<String> {
    return value.split(",") // Convert the string back to a list
  }
}