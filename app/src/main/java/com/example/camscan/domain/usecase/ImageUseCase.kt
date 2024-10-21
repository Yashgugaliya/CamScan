package com.example.camscan.domain.usecase

data class ImageUseCase(
  val updateImageTagUseCase: UpdateImageTagUseCase,
  val getImagesWithFace: GetImagesWithFace
)
