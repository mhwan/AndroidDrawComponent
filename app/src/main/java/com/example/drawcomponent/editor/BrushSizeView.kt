package com.example.drawcomponent.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.example.drawcomponent.R
import com.example.drawcomponent.drawer.PathPaintFactory

class BrushSizeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {
    @ColorInt
    var brushColor: Int = ContextCompat.getColor(context, R.color.sub_title_text)
        set(value) {
            field = value
            invalidatePaint()
            invalidate()
        }

    var brushSize = 5.0f
        set(value) {
            field = value
            invalidatePaint()
            invalidate()
        }
    private val brushSamplePath = Path()
    private lateinit var brushPaint: Paint

    init {
        invalidatePaint()
        initPathLine()
    }

    private fun initPathLine() {
        val length = 50
        val startX: Float = ((width / 2) - (length / 2)).toFloat() + 30
        brushSamplePath.apply {
            reset()
            moveTo(startX, (brushSize) - ((brushSize) / 2))
            lineTo(startX + length, brushSize - ((brushSize) / 2))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (!brushSamplePath.isEmpty)
                it.drawPath(brushSamplePath, brushPaint)
        }
    }

    private fun invalidatePaint() {
        brushPaint = PathPaintFactory.createPaint(brushColor, brushSize)
    }
}