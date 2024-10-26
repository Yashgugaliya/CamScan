package com.example.camscan.data.repository

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.example.camscan.data.local.ImageDao
import com.example.camscan.data.model.ImageEntity
import com.example.camscan.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
  private val imageDao: ImageDao,
  private val context: Context,
) : ImageRepository {
  override suspend fun getAllImages(): Flow<List<ImageEntity>> {
    return flowOf(imageDao.getAllImages())
  }

  override suspend fun updateImage(imageEntity: ImageEntity): ImageEntity {
    imageDao.updateImage(imageEntity)
    return imageDao.getImageById(imageEntity.id)
  }

  override suspend fun getImageById(id: Long): ImageEntity {
    return imageDao.getImageById(id)
  }

  override suspend fun fetchGalleryImages(): List<ImageEntity> {
    val cameraImages = mutableListOf<ImageEntity>()
    val projection = arrayOf(
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.DATA,
      MediaStore.Images.Media.DATE_TAKEN
    )

    val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
    val selectionArgs = arrayOf("%/DCIM/Camera/%")
    val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

    val cursor: Cursor? = context.contentResolver.query(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      projection,
      selection,
      selectionArgs,
      sortOrder
    )

    cursor?.use {
      val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
      val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
      val dateTakenColumn =
        it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

      while (it.moveToNext()) {
        val id = it.getLong(idColumn)
        val path = it.getString(pathColumn)
        val dateTaken = it.getLong(dateTakenColumn)

        val options = BitmapFactory.Options().apply {
          inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(path, options)
        val width = options.outWidth
        val height = options.outHeight

        // Create a new ImageEntity but without face rectangles (yet)
        cameraImages.add(
          ImageEntity(
            id = id,
            imagePath = path,
            timestamp = dateTaken,
            width = width,
            height = height,
            faceRectangles = emptyList()
          )
        )
      }
    }
    return cameraImages
  }

  override suspend fun insertImage(image: ImageEntity) {
    imageDao.insertImage(image)
  }

}