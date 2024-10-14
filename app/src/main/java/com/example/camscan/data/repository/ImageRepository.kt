package com.example.camscan.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.camscan.data.local.ImageDao
import com.example.camscan.data.model.ImageEntity
import com.example.camscan.data.model.ScreenState
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector.FaceDetectorOptions
import javax.inject.Inject

class ImageRepository @Inject constructor(
  private val imageDao: ImageDao,
  private val context: Context,
) {
  private val faceDetector: FaceDetector by lazy { initializeFaceDetector() }

  private fun initializeFaceDetector(): FaceDetector {
    val options = FaceDetectorOptions.builder()
      .setBaseOptions(
        BaseOptions.builder()
          .setModelAssetPath("face_detection_short_range.tflite").build()
      )
      .setMinDetectionConfidence(0.6f)
      .setRunningMode(RunningMode.IMAGE)
      .build()

    return FaceDetector.createFromOptions(context, options)
  }

  suspend fun getAllImages() = try {
    val galleryImages = fetchGalleryImages()
    val processedImages = mutableListOf<ImageEntity>()
    for (image in galleryImages) {
      val existingImage = imageDao.getImageByPath(image.imagePath)
      if (existingImage == null) {
        processAndInsertImage(image)
      } else {
        processedImages.add(existingImage)
      }
    }
    ScreenState.Success(imageDao.getAllImages())
  } catch (e: Exception) {
    ScreenState.Error(e)
  }

  suspend fun getProcessedImage() =
    try {
      ScreenState.Loading
      val result = imageDao.getAllImages()
      ScreenState.Success(result)
    } catch (e: Exception) {
      ScreenState.Error(exception = e)
    }

  suspend fun updateImageTag(id: Long, tag: String, index: Int) =
    try {
      ScreenState.Loading
      val image = imageDao.getImageById(id)
      image.let {
        val updatedRectangles = it.faceRectangles.toMutableList().apply {
          // Modify the 4th element of the rectangle
          this[index] = updateRectAtIndex(this[index], tag)
        }
        val updatedScreenshot = it.copy(faceRectangles = updatedRectangles)
        imageDao.updateImage(updatedScreenshot)
      }
      ScreenState.Success(imageDao.getImageById(id))
    } catch (e: Exception) {
      ScreenState.Error(exception = e)
    }

  private fun updateRectAtIndex(rectString: String, tag: String): String {
    val rectArray = rectString.split(":").toMutableList()
    rectArray[4] = tag
    return rectArray.joinToString(":")
  }

  private suspend fun processAndInsertImage(image: ImageEntity): ImageEntity {
    val faceRects = detectFaces(image.imagePath)

    if (faceRects.isNotEmpty()) {
      val processedImage = image.copy(faceRectangles = faceRects, isFace = true)
      imageDao.insertImage(processedImage)
      return processedImage
    } else {
      imageDao.insertImage(image)
    }
    return image
  }

  private fun fetchGalleryImages(): List<ImageEntity> {
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

  private fun detectFaces(imagePath: String): List<String> {

    val mpImage = BitmapImageBuilder(getBitmapFromUri(imagePath)).build()
    val result = faceDetector.detect(mpImage)
    val faceRects = mutableListOf<String>()

    result.detections()
    result?.detections()?.forEach { detection ->
      val tag = ""
      val boundingBox: RectF = detection.boundingBox()
      val formattedRect =
        "${boundingBox.left.toInt()}:${boundingBox.top.toInt()}:${boundingBox.right.toInt()}:${boundingBox.bottom.toInt()}:$tag"
      faceRects.add(formattedRect)
    }

    if (faceRects.isEmpty()) {
      Log.d("FaceDetection", "No faces detected.")
    }
    return faceRects
  }

  private fun getImageUri(imagePath: String): Uri? {
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = "${MediaStore.Images.Media.DATA} = ?"
    val selectionArgs = arrayOf(imagePath)

    context.contentResolver.query(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      projection,
      selection,
      selectionArgs,
      null
    )?.use { cursor ->
      if (cursor.moveToFirst()) {
        val id =
          cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
        return ContentUris.withAppendedId(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
          id
        )
      }
    }
    return null
  }

  private fun getBitmapFromUri(imagePath: String): Bitmap? {
    return getImageUri(imagePath)?.let {
      context.contentResolver.openInputStream(it)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
      }
    }
  }

}