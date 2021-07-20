package com.example.drawcomponent.editor

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.drawcomponent.databinding.DialogDrawEditToolBinding
import com.example.drawcomponent.drawer.BrushType
import com.example.drawcomponent.drawer.DrawViewModel
import com.example.drawcomponent.editor.picker.ChangeColorListener
import com.example.drawcomponent.editor.picker.ColorPickerAdapter
import com.example.drawcomponent.editor.picker.ColorPickerDataFactory


class DrawEditToolDialog : DialogFragment(), ChangeColorListener {
    private val viewModel: DrawViewModel by viewModels(::requireActivity)
    private var _binding: DialogDrawEditToolBinding? = null
    private val binding get() = _binding!!
    private val colorPickerAdapter by lazy {
        context?.let {
            ColorPickerAdapter(
                ColorPickerDataFactory.createColorPickerData(it),
                getColorPickerIdx(),
                this
            )
        }
    }

    private var brushSizeStep = 2
    private var minBrushSize = 0
    private var maxBrushSize = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDrawEditToolBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        binding.btnClose.setOnClickListener { dismiss() }
        setMode()
        setBrushSizeController()
        setBrushSizeView()
        setSizeButton()

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val window: Window? = dialog?.window
        window?.apply {
            val params = attributes
            params.dimAmount = 0.1f
            params.gravity = Gravity.BOTTOM
            attributes = params
        }
    }

    private fun setMode() {
        when (viewModel.brushType.value) {
            BrushType.DRAW_BRUSH -> {
                binding.tvToolMode.text = "색상 선택"
                binding.rvColorPicker.visibility = View.VISIBLE
                binding.rootEraseMode.visibility = View.GONE
                setColorPickerAdapter()
            }
            BrushType.ERASE_BRUSH -> {
                binding.tvToolMode.text = "지우개 모드"
                binding.rvColorPicker.visibility = View.GONE
                binding.rootEraseMode.visibility = View.VISIBLE
                setAutoEraseSwitch()
            }
        }
    }

    private fun setBrushSizeView() {
        val brushSize = getBrushSize()
        binding.tvBrushSize.text = "$brushSize"
        binding.brushView.brushSize = brushSize.toFloat()
    }

    private fun setColorPickerAdapter() {
        context?.let {
            binding.rvColorPicker.adapter = colorPickerAdapter
        }
    }

    private fun setAutoEraseSwitch(){
        binding.switchAutoErase.isChecked = viewModel.autoErase.value
        binding.switchAutoErase.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoErase(isChecked)
        }
    }

    private fun setSizeButton() {
        binding.btnDown.setOnClickListener {
            binding.seekbar.let {
                if (it.progress > 0) {
                    it.progress -= 1
                }
            }
        }
        binding.btnUp.setOnClickListener {
            binding.seekbar.let {
                if (it.progress < it.max) {
                    it.progress += 1
                }
            }
        }
    }

    private fun setBrushSizeController() {
        binding.seekbar.apply {
            max = (maxBrushSize - minBrushSize) / brushSizeStep
            progress = (getBrushSize() - minBrushSize) / brushSizeStep
            setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    viewModel.setBrushSize(minBrushSize + (progress * brushSizeStep))
                    setBrushSizeView()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun getBrushSize(): Int = viewModel.brushSize.value

    private fun getColorPickerIdx() = viewModel.colorIdx.value

    override fun onChangeColorPickerIndex(colorIdx: Int) {
        viewModel.setColorIndex(colorIdx)
    }

    class Builder {
        private val dialog = DrawEditToolDialog()

        fun setMinBrushSize(size: Int): Builder {
            dialog.minBrushSize = size
            return this
        }

        fun setMaxBrushSize(size: Int): Builder {
            dialog.maxBrushSize = size
            return this
        }

        fun create(): DrawEditToolDialog = dialog
    }
}