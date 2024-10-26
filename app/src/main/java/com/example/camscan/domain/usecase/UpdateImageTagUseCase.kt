package com.example.camscan.domain.usecase

import com.example.camscan.data.model.ImageEntity
import com.example.camscan.data.model.ScreenState
import com.example.camscan.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateImageTagUseCase @Inject constructor(
  private val imageRepository: ImageRepository
) {
  operator fun invoke(
    id: Long,
    tag: String,
    index: Int
  ): Flow<ScreenState<ImageEntity>> = flow {
    emit(ScreenState.Loading)

    val image = imageRepository.getImageById(id)
    if (image != null) {
      val updatedRectangles = image.faceRectangles.toMutableList().apply {
        this[index] = updateRectAtIndex(this[index], tag)
      }
      val updatedScreenshot = image.copy(faceRectangles = updatedRectangles)
      imageRepository.updateImage(updatedScreenshot)
      emit(ScreenState.Success(updatedScreenshot))
    } else {
      emit(ScreenState.Error(Exception("Image not found")))
    }
  }

  private fun updateRectAtIndex(rectString: String, tag: String): String {
    val rectArray = rectString.split(":").toMutableList()
    rectArray[4] = tag
    return rectArray.joinToString(":")
  }

}