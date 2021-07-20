package com.example.drawcomponent.editor
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlin.math.min

class CircleView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {
    private val circlePaint: Paint = createCirclePaint()
    private var w = 0.0f
    private var h = 0.0f

    @ColorInt
    var circleColor = ContextCompat.getColor(context, android.R.color.black)
        private set

    private fun createCirclePaint() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    fun setCircleColor(@ColorInt color: Int) {
        circleColor = color
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w.toFloat()
        this.h = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        circlePaint.color = circleColor

        val radius = min(w, h) / 2
        canvas.drawCircle(w / 2, h / 2, radius, circlePaint)
    }
}