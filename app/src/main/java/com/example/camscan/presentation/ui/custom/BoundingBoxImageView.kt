package com.example.camscan.presentation.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.camscan.presentation.util.BoxClick

class BoundingBoxImageView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
  private var boxClickListener: BoxClick? = null

  private val paint = Paint().apply {
    color = Color.RED // Box color
    strokeWidth = 5f
    style = Paint.Style.STROKE // To draw the outline
  }

  private val textPaint = Paint().apply {
    color = Color.WHITE
    textSize = 30f // Size of the text
    style = Paint.Style.FILL
  }

  private var faceRectangles: List<Rect> = emptyList()
  private var boundingBoxText = mutableListOf<String>()
  private var originalWidth: Int = 0
  private var originalHeight: Int = 0

  var faceString: List<String> = emptyList()
    set(value) {
      field = value
      faceRectangles = getRect(value)
      invalidate()
    }

  fun setOriginalDimensions(width: Int, height: Int) {
    originalWidth = width
    originalHeight = height
  }

  fun setOnBoxClickListener(listener: BoxClick) {
    boxClickListener = listener
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawFaceBoundingBoxes(canvas)
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
      val touchX = event.x
      val touchY = event.y
      val viewWidth = width
      val viewHeight = height
      var index = 0

      // Calculate scale ratios
      val scaleX = viewWidth.toFloat() / originalWidth
      val scaleY = viewHeight.toFloat() / originalHeight
      faceRectangles.forEach { rect ->
        index = faceRectangles.indexOf(rect)
        val scaledRect = Rect(
          (rect.left * scaleX).toInt(),
          (rect.top * scaleY).toInt(),
          (rect.right * scaleX).toInt(),
          (rect.bottom * scaleY).toInt()
        )

        if (scaledRect.contains(touchX.toInt(), touchY.toInt())) {
          boxClickListener?.invoke(index)
          Log.d("BoundingBoxImageView", "Box clicked: $boundingBoxText")
          return true
        }
      }
    }
    return super.onTouchEvent(event)
  }

  private fun drawFaceBoundingBoxes(canvas: Canvas) {
    val drawable = drawable ?: return // No image loaded
    val intrinsicWidth = drawable.intrinsicWidth
    val intrinsicHeight = drawable.intrinsicHeight
    if (intrinsicWidth == 0 || intrinsicHeight == 0 || faceRectangles.isEmpty()) {
      return // No dimensions or no rectangles
    }

    // Get current view size
    val viewWidth = width
    val viewHeight = height
    // Calculate scale ratios
    val scaleX = viewWidth.toFloat() / originalWidth
    val scaleY = viewHeight.toFloat() / originalHeight

    // Scale and draw each rectangle
    faceRectangles.forEachIndexed { index, rect ->
      val scaledRect = Rect(
        (rect.left * scaleX).toInt(),
        (rect.top * scaleY).toInt(),
        (rect.right * scaleX).toInt(),
        (rect.bottom * scaleY).toInt()
      )
      // Coerce to bounds
      scaledRect.left = scaledRect.left.coerceIn(0, intrinsicWidth)
      scaledRect.top = scaledRect.top.coerceIn(0, intrinsicHeight)
      scaledRect.right = scaledRect.right.coerceIn(0, intrinsicWidth)
      scaledRect.bottom = scaledRect.bottom.coerceIn(0, intrinsicHeight)
      canvas.drawRect(scaledRect, paint)

      val text =
        boundingBoxText[index] // Get the text corresponding to this rectangle

      val scaledLeft = (rect.left * scaleX).toInt()
      val scaledTop = (rect.top * scaleY).toInt()

      // Draw text slightly above the bounding box
      canvas.drawText(
        text,
        (scaledLeft).toFloat(),
        (scaledTop - 20).toFloat(), // Adjust vertical offset for text positioning
        textPaint
      )

    }
  }

  private fun getRect(lis: List<String>): List<Rect> {
    return lis.mapIndexed { index, rectString ->
      // Split the rectString into its components (left, top, right, bottom, text)
      val coordinates = rectString.split(":")

      // Convert strings to integers to create the Rect
      val left = coordinates[0].toInt()
      val top = coordinates[1].toInt()
      val right = coordinates[2].toInt()
      val bottom = coordinates[3].toInt()
      val text = coordinates[4]

      // Insert the bounding box text at the corresponding index
      if (index < boundingBoxText.size) {
        boundingBoxText[index] = text
      } else {
        boundingBoxText.add(text)
      }

      Rect(left, top, right, bottom)
    }
  }

}

