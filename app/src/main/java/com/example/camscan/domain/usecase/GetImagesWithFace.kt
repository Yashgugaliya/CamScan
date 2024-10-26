package com.example.camscan.domain.usecase

import com.example.camscan.data.model.ImageEntity
import com.example.camscan.data.model.ScreenState
import com.example.camscan.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetImagesWithFace @Inject constructor(
  private val imageRepository: ImageRepository,
  private val faceDetection: FaceDetection,
) {
   operator fun invoke(): Flow<ScreenState<List<ImageEntity>>> = flow {
     emit(ScreenState.Loading)
     try {
       val galleryImages = imageRepository.fetchGalleryImages()
       val imagesWithFaces = galleryImages.mapNotNull { image ->
         val existingImage = imageRepository.getImageById(image.id)
         existingImage ?: processAndInsertImage(image)
       }.filter { it.isFace }
       emit(ScreenState.Success(imagesWithFaces))
     } catch (e: Exception) {
       emit(ScreenState.Error(e))
     }
   }


  private suspend fun processAndInsertImage(image: ImageEntity): ImageEntity? {
    return try {
      val faceRects = faceDetection(image.imagePath)
      if (faceRects.isNotEmpty()) {
        val processedImage =
          image.copy(faceRectangles = faceRects, isFace = true)
        imageRepository.insertImage(processedImage)
        processedImage
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }
}
