package com.example.drawcomponent.drawer

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

data class PathPaint(val path: Path, val paint: Paint)

object PathPaintFactory {
    fun createPathPaint(color: Int, width: Float) =
        PathPaint(createPath(), createPaint(color, width))

    fun createPaint(color: Int, width: Float): Paint {
        return Paint().apply {
            strokeWidth = width
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.ROUND
            setColor(color)
        }
    }

    private fun createPath() = Path()
}

object PointerPaintFactory {
    fun createPointer(): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
            color = Color.GRAY
            alpha = 152
        }
    }
}