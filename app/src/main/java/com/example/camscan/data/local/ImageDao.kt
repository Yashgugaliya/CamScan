package com.example.camscan.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.camscan.data.model.ImageEntity

@Dao
interface ImageDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertImage(image: ImageEntity)

  @Query("SELECT * FROM image WHERE isFace = 1 ORDER BY timestamp DESC")
  suspend fun getAllImages(): List<ImageEntity>

  @Query("SELECT * FROM image WHERE imagePath = :imagePath")
  suspend fun getImageByPath(imagePath: String): ImageEntity

  @Query("SELECT * FROM image WHERE id = :id")
  suspend fun getImageById(id: Long): ImageEntity

  @Update
  suspend fun updateImage(image: ImageEntity)

}
