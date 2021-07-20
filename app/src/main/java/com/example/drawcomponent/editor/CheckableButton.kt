package com.example.drawcomponent.editor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.core.widget.ImageViewCompat
import com.example.drawcomponent.R
import com.example.drawcomponent.databinding.LayoutCheckableIconBinding

@Suppress("LeakingThis")
class CheckableButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), Checkable {
    private var checked = false
    private val binding: LayoutCheckableIconBinding =
        LayoutCheckableIconBinding.inflate(LayoutInflater.from(context), this)

    init {
        initIndicatorIcon()
        initTypedArray(attrs)
        isClickable = true
        isFocusable = true
        setBackgroundSelector(R.drawable.checkable_background_selector)
        setIconSelector(R.color.checkable_icon_selector)
    }

    private fun initTypedArray(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CheckableButton, 0, 0).use {
            isEnabled = it.getBoolean(R.styleable.CheckableButton_android_enabled, isEnabled)
            isChecked = it.getBoolean(R.styleable.CheckableButton_android_checked, false)
            binding.icon.setImageDrawable(it.getDrawable(R.styleable.CheckableButton_icon))
        }
    }

    private fun initIndicatorIcon() {
        binding.icon.layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(CENTER_IN_PARENT)
            }
    }

    private fun setBackgroundSelector(@DrawableRes id: Int) {
        background = ContextCompat.getDrawable(context, id)
    }

    private fun setIconSelector(@ColorRes id: Int) {
        ImageViewCompat.setImageTintList(binding.icon, AppCompatResources.getColorStateList(context, id))
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        children.forEach { it.isEnabled = enabled }
    }

    override fun isChecked() = checked

    override fun toggle() {
        isChecked = !checked
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            children.filter { it is Checkable }.forEach { (it as Checkable).isChecked = checked }
            refreshDrawableState()
        }
    }

    override fun performClick(): Boolean {
        if (isChecked) return false

        toggle()
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}