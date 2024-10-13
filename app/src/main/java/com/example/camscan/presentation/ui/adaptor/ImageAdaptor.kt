package com.example.camscan.presentation.ui.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.camscan.data.model.ImageEntity
import com.example.camscan.databinding.ItemImageBinding
import com.example.camscan.presentation.util.BoxClick
import com.example.camscan.presentation.util.ImageClick
import com.example.camscan.presentation.util.load

class ImageAdaptor(private val listener: ImageClick) :
  ListAdapter<ImageEntity, RecyclerView.ViewHolder>(ImageComparator) {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): RecyclerView.ViewHolder =
    ImageViewHolder(
      ItemImageBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ), listener
    )

  override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder,
    position: Int
  ) {
    holder as ImageViewHolder
    getItem(position)?.let { holder.setData(it, position) }
  }
}

class ImageViewHolder(
  private val binding: ItemImageBinding,
  private val listener: ImageClick
) : RecyclerView.ViewHolder(binding.root) {
  fun setData(item: ImageEntity, po: Int) {
    with(binding) {
      image.setOnBoxClickListener(object : BoxClick {
        override fun invoke(pos: Int) {
          listener(item.id, item.faceRectangles[pos].split(":")[4], pos)
        }
      })
      image.load(item.imagePath)
      image.post {
        image.setOriginalDimensions(item.width, item.height)
        image.faceString = item.faceRectangles
      }
    }
  }
}

object ImageComparator : DiffUtil.ItemCallback<ImageEntity>() {
  override fun areItemsTheSame(
    oldItem: ImageEntity,
    newItem: ImageEntity
  ): Boolean {
    return oldItem.imagePath == newItem.imagePath
  }

  override fun areContentsTheSame(
    oldItem: ImageEntity,
    newItem: ImageEntity
  ): Boolean {
    return oldItem == newItem
  }

}