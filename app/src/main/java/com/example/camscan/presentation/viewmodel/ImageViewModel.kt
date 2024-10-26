package com.example.camscan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camscan.data.model.ImageEntity
import com.example.camscan.data.model.ScreenState
import com.example.camscan.domain.usecase.ImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
  private val imageUseCase: ImageUseCase
) : ViewModel() {

  private val _images = MutableStateFlow<ScreenState<List<ImageEntity>>?>(null)
  val images: StateFlow<ScreenState<List<ImageEntity>>?> get() = _images

  private val _imageUpdate = MutableStateFlow<ScreenState<ImageEntity>?>(null)
  val imageUpdate: StateFlow<ScreenState<ImageEntity>?> get() = _imageUpdate

  fun getAllImages() {
    viewModelScope.launch {
      try {
        imageUseCase.getImagesWithFace()
          .collect { screenState ->
            when (screenState) {
              is ScreenState.Success -> {
                _images.value =
                  ScreenState.Success(screenState.data) // Emit the updated list
              }

              is ScreenState.Error -> {
                _images.value = ScreenState.Error(screenState.exception)
              }

              is ScreenState.Loading -> {
                _images.value = ScreenState.Loading
              }
            }
          }
      } catch (e: Exception) {
        _images.value = ScreenState.Error(e)
      }
    }
  }

  fun updateImageTag(id: Long, tag: String, index: Int) {
    viewModelScope.launch {
      try {
        imageUseCase.updateImageTagUseCase(id, tag, index)
          .collect { screenState ->
            when (screenState) {
              is ScreenState.Success -> {
                _imageUpdate.value = ScreenState.Success(screenState.data)
              }

              is ScreenState.Error -> {
                _imageUpdate.value = ScreenState.Error(screenState.exception)
              }

              is ScreenState.Loading -> {
                _imageUpdate.value = ScreenState.Loading
              }
            }
          }
      } catch (e: Exception) {
        _imageUpdate.value = ScreenState.Error(e)
      }
    }
  }
}


