package com.example.camscan.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.camscan.data.local.ImageDao
import com.example.camscan.data.local.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun provideImageDatabase(@ApplicationContext context: Context): ImageDatabase {
    return Room.databaseBuilder(
      context,
      ImageDatabase::class.java,
      "photo_database"
    ).build()
  }

  @Provides
  @Singleton
  fun provideImageShotDao(database: ImageDatabase): ImageDao {
    return database.imageDao()
  }

  @Provides
  @Singleton
  fun provideApplicationContext(application: Application): Context {
    return application.applicationContext
  }

}