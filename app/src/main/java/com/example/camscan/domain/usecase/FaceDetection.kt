package com.example.camscan.domain.usecase

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import javax.inject.Inject

class FaceDetection @Inject constructor(
  private val faceDetector: FaceDetector,
  private val context: Context,
) {
  operator fun invoke(imagePath: String): List<String> {
    val mpImage = BitmapImageBuilder(getBitmapFromUri(imagePath)).build()
    val result = faceDetector.detect(mpImage)
    val faceRects = mutableListOf<String>()
    result?.detections()?.forEach { detection ->
      val tag = ""
      val boundingBox: RectF = detection.boundingBox()
      val formattedRect =
        "${boundingBox.left.toInt()}:${boundingBox.top.toInt()}:${boundingBox.right.toInt()}:${boundingBox.bottom.toInt()}:$tag"
      faceRects.add(formattedRect)
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