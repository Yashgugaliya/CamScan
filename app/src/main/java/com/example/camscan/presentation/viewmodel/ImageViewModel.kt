package com.example.camscan.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camscan.data.model.ImageEntity
import com.example.camscan.data.model.ScreenState
import com.example.camscan.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val screenshotRepository: ImageRepository) :
  ViewModel() {

  private val _images = MutableLiveData<ScreenState<List<ImageEntity>>>()
  val images: LiveData<ScreenState<List<ImageEntity>>> get() = _images

  private val _imageUpdate = MutableLiveData<ScreenState<ImageEntity>>()
  val imageUpdate: LiveData<ScreenState<ImageEntity>> get() = _imageUpdate

  fun getAllImages() {
    viewModelScope.launch {
      _images.postValue(ScreenState.Loading)
      try {
        _images.postValue(screenshotRepository.getAllImages())
      } catch (e: Exception) {
        _images.postValue(ScreenState.Error(e))
      }
    }
  }

  fun getProceedImage() {
    viewModelScope.launch {
      _images.postValue(ScreenState.Loading)
      try {
        _images.postValue(screenshotRepository.getProcessedImage())
      } catch (e: Exception) {
        _images.postValue(ScreenState.Error(e))
      }
    }
  }

  fun updateImageTag(id: Long, tag: String, index: Int) {
    viewModelScope.launch {
      _imageUpdate.postValue(ScreenState.Loading)
      try {
        _imageUpdate.postValue(
          screenshotRepository.updateImageTag(
            id,
            tag,
            index
          )
        )
      } catch (e: Exception) {
        _imageUpdate.postValue(ScreenState.Error(e))
      }
    }
  }

}
