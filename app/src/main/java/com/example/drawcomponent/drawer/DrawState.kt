package com.example.drawcomponent.drawer

sealed class DrawState {
    data class Show(val mode: DrawMode, val pathPaint: PathPaint) : DrawState()
    data class Hide(val orgIdx: Int, val pathPaint: PathPaint) : DrawState()
}
