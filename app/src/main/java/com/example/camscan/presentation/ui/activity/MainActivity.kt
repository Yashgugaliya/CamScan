package com.example.camscan.presentation.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.camscan.data.model.ScreenState
import com.example.camscan.databinding.ActivityMainBinding
import com.example.camscan.presentation.ui.adaptor.ImageAdaptor
import com.example.camscan.presentation.ui.fragment.AddTagBottomSheet
import com.example.camscan.presentation.util.LoaderView
import com.example.camscan.presentation.util.gone
import com.example.camscan.presentation.util.visible
import com.example.camscan.presentation.viewmodel.ImageViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
  private val viewModel: ImageViewModel by viewModels()
  private val imageAdapter = ImageAdaptor(::imageClick)
  private val loaderView by lazy { LoaderView(this) }

  private val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
      if (isGranted) {
        viewModel.getAllImages()
      } else {
        if (shouldShowPermissionRationale()) {
          showRationaleSnackBar()
        } else {
          requestStoragePermission()
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    setUpRecyclerView()
    setUpCheckPermission()
    setUpObserver()
  }

  private fun setUpRecyclerView() {
    binding.recyclerView.apply {
      layoutManager = GridLayoutManager(this@MainActivity, 2)
      adapter = imageAdapter
    }
  }

  private fun setUpCheckPermission() {
    if (checkPermission()) {
      viewModel.getAllImages()
    } else
      requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
  }

  private fun setUpObserver() {
    viewModel.images.observe(this) { state ->
      when (state) {
        is ScreenState.Loading -> {
          loaderView.showLoading()
        }

        is ScreenState.Success -> {
          loaderView.hideLoading()
          if (state.data.isNotEmpty()) {
            binding.recyclerView.visible()
            binding.emptyView.gone()
            binding.errorView.gone()
            imageAdapter.submitList(state.data)
          } else {
            binding.emptyView.visible()
            binding.recyclerView.gone()
            binding.errorView.gone()
          }
        }

        is ScreenState.Error -> {
          loaderView.hideLoading()
          binding.recyclerView.gone()
          binding.errorView.visible()
          binding.emptyView.gone()
        }
      }
    }

    viewModel.imageUpdate.observe(this) { state ->
      when (state) {
        is ScreenState.Loading -> {
          // Show loading state
        }

        is ScreenState.Success -> {
          // Show success state
          val list = imageAdapter.currentList.toMutableList()
          val index = list.indexOfFirst { it.id == state.data.id }
          list[index] = state.data
          Log.d("AddTagBottomSheet", "Updated Image: $list")
          imageAdapter.submitList(list)
        }

        is ScreenState.Error -> {
          // Show error state
          Log.d("AddTagBottomSheet", "Error: ${state.exception}")
        }
      }
    }
  }

  private fun checkPermission() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_MEDIA_IMAGES
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
    }

  // Method to request storage permission based on Android version
  private fun requestStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
      requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
  }

  // Show Snackbar with rationale for permission request
  private fun showRationaleSnackBar() {
    Snackbar.make(
      binding.root,
      "We need access to your images to detect faces and add tags.",
      Snackbar.LENGTH_INDEFINITE
    ).setAction("Grant") {
      openAppSettings() // Re-request permission
    }.show()
  }

  // Check if we should show rationale for permission
  private fun shouldShowPermissionRationale(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
      shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
  }

  // Open the app's settings page
  private fun openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = android.net.Uri.parse("package:$packageName")
    startActivity(intent)
  }

  private fun imageClick(id: Long, tag: String, index: Int) {
    AddTagBottomSheet.newInstance(id, tag, index)
      .show(supportFragmentManager, "AddTagBottomSheet")
  }
}