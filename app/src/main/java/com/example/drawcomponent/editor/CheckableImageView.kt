package com.example.drawcomponent.editor
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class CheckableImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attributeSet, defStyleAttr), Checkable {
    private var checked = false

    override fun isChecked() = checked

    override fun setChecked(isChecked: Boolean) {
        if (isChecked != checked) {
            checked = isChecked
            refreshDrawableState()
        }
    }

    override fun toggle() {
        isChecked = !checked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}