package com.example.drawcomponent.editor.picker

import com.example.drawcomponent.editor.CircleView
import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.drawcomponent.R


class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr) {
    var isChecked = true
        set(value) {
            field = value
            when (field) {
                true -> checkImage.visibility = VISIBLE
                false -> checkImage.visibility = GONE
            }
        }
    private lateinit var checkImage: AppCompatImageView
    private lateinit var circleView: CircleView

    init {
        initView()
    }

    private fun initView() {
        circleView = CircleView(context).also {
            addView(it, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                addRule(CENTER_IN_PARENT)
            })
        }

        checkImage = AppCompatImageView(context).also {
            addView(it, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(CENTER_IN_PARENT)
            })
            it.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_check_16
                )
            )
        }
    }

    override fun setBackgroundColor(color: Int) {
        circleView.setCircleColor(color)
        invalidate()
    }
}