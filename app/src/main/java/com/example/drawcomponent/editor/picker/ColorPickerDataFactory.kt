package com.example.drawcomponent.editor.picker

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.drawcomponent.R

object ColorPickerDataFactory {
    fun createColorPickerData(context: Context): List<ColorData> = listOf(
        ColorData(ContextCompat.getColor(context, R.color.black)),
        ColorData(ContextCompat.getColor(context, R.color.red_500)),
        ColorData(ContextCompat.getColor(context, R.color.orange_500)),
        ColorData(ContextCompat.getColor(context, R.color.yellow_500)),
        ColorData(ContextCompat.getColor(context, R.color.green_500)),
        ColorData(ContextCompat.getColor(context, R.color.blue_500))
    )
}