package com.example.camscan.di

import android.app.Application
import android.content.Context
import com.example.camscan.data.local.ImageDao
import com.example.camscan.data.repository.ImageRepositoryImpl
import com.example.camscan.domain.repository.ImageRepository
import com.example.camscan.domain.usecase.FaceDetection
import com.example.camscan.domain.usecase.GetImagesWithFace
import com.example.camscan.domain.usecase.ImageUseCase
import com.example.camscan.domain.usecase.UpdateImageTagUseCase
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideApplicationContext(application: Application): Context {
    return application.applicationContext
  }

  @Provides
  @Singleton
  fun provideFaceDetector(context: Context): FaceDetector {
    val options = FaceDetector.FaceDetectorOptions.builder()
      .setBaseOptions(
        BaseOptions.builder()
          .setModelAssetPath("face_detection_short_range.tflite").build()
      )
      .setMinDetectionConfidence(0.6f)
      .setRunningMode(RunningMode.IMAGE)
      .build()

    return FaceDetector.createFromOptions(context, options)
  }

  @Provides
  @Singleton
  fun provideImageRepository(
    imageDao: ImageDao,
    context: Context
  ): ImageRepository {
    return ImageRepositoryImpl(imageDao, context)
  }


  @Provides
  @Singleton
  fun provideImageUseCase(
    imageRepository: ImageRepository,
    faceDetection : FaceDetection,
  ): ImageUseCase {
    return ImageUseCase(
      updateImageTagUseCase = UpdateImageTagUseCase(imageRepository),
      getImagesWithFace = GetImagesWithFace(imageRepository, faceDetection)
    )
  }
}