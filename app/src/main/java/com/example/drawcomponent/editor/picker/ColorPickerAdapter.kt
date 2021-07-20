package com.example.drawcomponent.editor.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcomponent.databinding.ItemColorPickerBinding

class ColorPickerAdapter(
    private val colorList: List<ColorData>,
    private var colorIdx: Int,
    private val changeColorListener: ChangeColorListener
) : RecyclerView.Adapter<ColorPickerAdapter.ColorPickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerViewHolder {
        val binding = ItemColorPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorPickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorPickerViewHolder, position: Int) {
        holder.bind(colorList[position], position)
    }

    override fun getItemCount() = colorList.size

    inner class ColorPickerViewHolder(private val binding: ItemColorPickerBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.colorPicker.setOnClickListener {
                colorIdx = adapterPosition
                changeColorListener.onChangeColorPickerIndex(colorIdx)
                notifyDataSetChanged()
            }
        }

        fun bind(data: ColorData, position: Int) {
            binding.colorPicker.apply {
                setBackgroundColor(data.backgroundColor)
                isChecked = (colorIdx == position)
            }
        }
    }
}