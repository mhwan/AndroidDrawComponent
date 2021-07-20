package com.example.drawcomponent.drawer

import com.example.drawcomponent.editor.picker.ColorPickerDataFactory
import com.example.drawcomponent.editor.DrawEditToolDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.drawcomponent.databinding.ActivityDrawBinding
import com.example.drawcomponent.util.toast
import com.example.drawcomponent.util.ImageUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DrawActivity : AppCompatActivity() {
    private val pickColorList by lazy { ColorPickerDataFactory.createColorPickerData(baseContext) }
    private lateinit var binding: ActivityDrawBinding
    private val viewModel: DrawViewModel by viewModels()
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawBinding.inflate(layoutInflater)

        intent.getStringExtra(EXTRA_DRAW_INPUT_BITMAP)?.let {
            if (ImageUtil.isRemotePath(it)) {
                ImageUtil.getBitmapFromRemotePath(applicationContext, it) { bitmap ->
                    viewModel.setDrawBackgroundImage(bitmap)
                }
            } else {
                viewModel.setDrawBackgroundImage(ImageUtil.getBitmapFromLocalPath(it))
            }
        }

        setContentView(binding.root)
        setButtonListener()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.brushType.collect {
                        changeBrushType(it)
                        setDrawViewMode()
                    }
                }
                launch {
                    viewModel.autoErase.collect {
                        setDrawViewMode()
                    }
                }
                launch {
                    viewModel.colorIdx.collect {
                        binding.drawView.brushColor = getPickedColor(it).backgroundColor
                    }
                }
                launch {
                    viewModel.brushSize.collect {
                        binding.drawView.brushSize = it.toFloat()
                    }
                }
                launch {
                    viewModel.backgroundImage.collect {
                        it?.let {
                            isEditMode = true
                            binding.drawView.setBackgroundImage(it)
                        }
                    }
                }
            }
        }
    }

    private fun setButtonListener() {
        binding.bottomToolbar.btnBrushMode.setOnClickListener {
            viewModel.setBrushType(BrushType.DRAW_BRUSH)
        }
        binding.bottomToolbar.btnEraseMode.setOnClickListener {
            viewModel.setBrushType(BrushType.ERASE_BRUSH)
        }
        binding.topToolbar.rootDraw.visibility = View.VISIBLE
        binding.topToolbar.btnUndo.setOnClickListener {
            if (!binding.drawView.undo()) toast("더이상 되돌릴 데이터가 없습니다.")
        }
        binding.topToolbar.btnRedo.setOnClickListener {
            if (!binding.drawView.redo()) toast("더이상 복구할 데이터가 없습니다.")
        }
        binding.topToolbar.btnRemoveAll.setOnClickListener {
            binding.drawView.clearAll()
        }
        binding.bottomToolbar.btnEditTool.setOnClickListener {
            DrawEditToolDialog.Builder()
                .setMinBrushSize(MIN_BRUSH_SIZE)
                .setMaxBrushSize(MAX_BRUSH_SIZE)
                .create()
                .show(supportFragmentManager, "")
        }
        binding.topToolbar.btnWrite.setOnClickListener {
            val resultBitmap = binding.drawView.convertToBitmap()
            if (resultBitmap != null) {
                setDrawResult(resultBitmap)
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
        }
    }

    private fun setDrawResult(bitmap: Bitmap) {
        val resultPath = ImageUtil.saveBitmapToLocalFile(this, bitmap)
        val resultCode = if (isEditMode) RESULT_EDIT_DRAW_OK else RESULT_NEW_DRAW_OK
        setResult(resultCode, Intent().apply {
            putExtra(EXTRA_DRAW_RESULT, resultPath)
        })
    }

    private fun setDrawViewMode() {
        binding.drawView.drawMode = viewModel.getDrawMode()
    }

    private fun changeBrushType(brushType: BrushType) {
        val isOnDrawMode = (brushType == BrushType.DRAW_BRUSH)
        val isOnEraseMode = !isOnDrawMode

        binding.bottomToolbar.btnBrushMode.isChecked = isOnDrawMode
        binding.bottomToolbar.btnEraseMode.isChecked = isOnEraseMode
    }

    private fun getPickedColor(colorIdx: Int) =
        if (isCorrectRange(colorIdx)) pickColorList[colorIdx] else pickColorList[0]

    private fun isCorrectRange(colorIdx: Int) = (pickColorList.indices).contains(colorIdx)

    companion object {
        const val EXTRA_DRAW_INPUT_BITMAP = "draw_image_input_bitmap"
        const val EXTRA_DRAW_RESULT = "draw_image_result"
        const val RESULT_NEW_DRAW_OK = 0x100
        const val RESULT_EDIT_DRAW_OK = 0x101
        const val MAX_BRUSH_SIZE = 60
        const val MIN_BRUSH_SIZE = 5
        const val DEFAULT_BRUSH_SIZE = 20
    }
}