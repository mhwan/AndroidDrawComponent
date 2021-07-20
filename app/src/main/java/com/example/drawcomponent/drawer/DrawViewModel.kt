package com.example.drawcomponent.drawer

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DrawViewModel : ViewModel() {
    private val _brushTypeStateFlow = MutableStateFlow(BrushType.DRAW_BRUSH)
    private val _brushSizeStateFlow = MutableStateFlow(DrawActivity.DEFAULT_BRUSH_SIZE)
    private val _autoEraseStateFlow = MutableStateFlow(false)
    private val _colorIdxStateFlow = MutableStateFlow(0)
    private val _backgroundImageStateFlow = MutableStateFlow<Bitmap?>(null)

    val brushType : StateFlow<BrushType> get() = _brushTypeStateFlow
    val brushSize : StateFlow<Int> get() = _brushSizeStateFlow
    val autoErase : StateFlow<Boolean> get() = _autoEraseStateFlow
    val colorIdx : StateFlow<Int> get() = _colorIdxStateFlow
    val backgroundImage: StateFlow<Bitmap?> get() = _backgroundImageStateFlow

    fun setBrushType(brushType: BrushType) {
        _brushTypeStateFlow.value = brushType
    }

    fun setBrushSize(size: Int) {
        _brushSizeStateFlow.value = size
    }

    fun setColorIndex(idx: Int) {
        _colorIdxStateFlow.value = idx
    }

    fun setAutoErase(autoErase: Boolean) {
        _autoEraseStateFlow.value = autoErase
    }

    fun getDrawMode() =
        if (brushType.value == BrushType.DRAW_BRUSH) DrawMode.DRAW
        else {
            if (!autoErase.value) DrawMode.ERASE
            else DrawMode.AUTO_ERASE
        }

    fun setDrawBackgroundImage(bitmap: Bitmap?) {
        _backgroundImageStateFlow.value = bitmap
    }
}