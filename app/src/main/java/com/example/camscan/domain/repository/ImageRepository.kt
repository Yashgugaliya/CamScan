package com.example.camscan.domain.repository

import com.example.camscan.data.model.ImageEntity
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
  suspend fun getAllImages(): Flow<List<ImageEntity>>
  suspend fun updateImage(imageEntity: ImageEntity): ImageEntity
  suspend fun getImageById(id: Long): ImageEntity?
  suspend fun fetchGalleryImages(): List<ImageEntity>
  suspend fun insertImage(image: ImageEntity)
}