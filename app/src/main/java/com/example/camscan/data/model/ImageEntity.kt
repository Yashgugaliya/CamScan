package com.example.camscan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.camscan.data.local.Converters

@Entity(tableName = "image")
data class ImageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val imagePath: String,
  val timestamp: Long,
  val width: Int = 0,
  val height: Int = 0,
  @TypeConverters(Converters::class)
  val faceRectangles: List<String> = emptyList(),
  val isFace: Boolean = false
)
