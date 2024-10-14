package com.example.camscan.presentation.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
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
    color = Color.GREEN
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
      val scale = minOf(scaleX, scaleY)
      val horizontalOffset = getHorizontalOffset(viewWidth, scale)
      val verticalOffset = getVerticalOffset(viewHeight, scale)

      faceRectangles.forEach { rect ->
        index = faceRectangles.indexOf(rect)
        val scaledRect = Rect(
          (rect.left * scale + horizontalOffset).toInt(),
          (rect.top * scale + verticalOffset).toInt(),
          (rect.right * scale + horizontalOffset).toInt(),
          (rect.bottom * scale + verticalOffset).toInt()
        )

        if (scaledRect.contains(touchX.toInt(), touchY.toInt())) {
          boxClickListener?.invoke(index)
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
    val scale = minOf(scaleX, scaleY)
    val horizontalOffset = getHorizontalOffset(viewWidth, scale)
    val verticalOffset = getVerticalOffset(viewHeight, scale)

    // Scale and draw each rectangle
    faceRectangles.forEachIndexed { index, rect ->
      val scaledRect = Rect(
        (rect.left * scale + horizontalOffset).toInt(),
        (rect.top * scale + verticalOffset).toInt(),
        (rect.right * scale + horizontalOffset).toInt(),
        (rect.bottom * scale + verticalOffset).toInt()
      )
      // Coerce to bounds
      scaledRect.left = scaledRect.left.coerceIn(0, viewWidth)
      scaledRect.top = scaledRect.top.coerceIn(0, viewHeight)
      scaledRect.right = scaledRect.right.coerceIn(0, viewWidth)
      scaledRect.bottom = scaledRect.bottom.coerceIn(0, viewHeight)

      canvas.drawRect(scaledRect, paint)
      val text = boundingBoxText[index]
      val scaledLeft = (rect.left * scaleX).toInt()
      val scaledTop = (rect.top * scaleY).toInt()

      // Draw text slightly above the bounding box
      canvas.drawText(
        text,
        (scaledLeft).toFloat(),
        (scaledTop - 20).toFloat(),
        textPaint
      )
    }
  }

  private fun getRect(lis: List<String>): List<Rect> {
    return lis.mapIndexed { index, rectString ->
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

  private fun getVerticalOffset(viewHeight: Int, scale: Float): Int {
    val scaledHeight = (originalHeight * scale).toInt()
    return if (scaledHeight < viewHeight) {
      (viewHeight - scaledHeight) / 2
    } else {
      0
    }
  }

  private fun getHorizontalOffset(viewWidth: Int, scale: Float): Int {
    val scaledWidth = (originalWidth * scale).toInt()
    return if (scaledWidth < viewWidth) {
      (viewWidth - scaledWidth) / 2
    } else {
      0
    }
  }
}

