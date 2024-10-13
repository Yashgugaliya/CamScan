package com.example.camscan.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.camscan.R
import com.example.camscan.databinding.FragmentAddTagBinding
import com.example.camscan.presentation.util.LoaderView
import com.example.camscan.presentation.viewmodel.ImageViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTagBottomSheet : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentAddTagBinding
  private val viewModel: ImageViewModel by activityViewModels()
  private var tag: String = ""
  private var id: Long = 0
  private var index: Int = 0
  private lateinit var loaderView: LoaderView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    loaderView = LoaderView(requireContext())
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentAddTagBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments?.let {
      tag = it.getString(ARG_TAG, "")
      id = it.getLong(ARG_ID, 0)
      index = it.getInt(ARG_INDEX, 0)
    }

    binding.tvEdit.setText(tag)
    binding.tvSave.setOnClickListener {
      viewModel.updateImageTag(id, binding.tvEdit.text.toString(), index)
      dismiss()
    }
  }

  companion object {
    private const val ARG_TAG = "tag"
    private const val ARG_ID = "id"
    private const val ARG_INDEX = "index"

    @JvmStatic
    fun newInstance(id: Long, tag: String, index:Int): AddTagBottomSheet {
      val fragment = AddTagBottomSheet()
      val args = Bundle().apply {
        putString(ARG_TAG, tag)
        putLong(ARG_ID, id)
        putInt(ARG_INDEX, index)
      }
      fragment.arguments = args
      return fragment
    }
  }
}
