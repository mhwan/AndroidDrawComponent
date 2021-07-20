package com.example.drawcomponent.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    fun saveBitmapToLocalFile(context: Context, bitmap: Bitmap): String {
        val absolutePath = getFilePath(context)
        val file = File(absolutePath)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return absolutePath
    }

    private fun getFilePath(context: Context): String {
        val fileName = "draw_${System.currentTimeMillis()}.jpg"
        val dir = context.getExternalFilesDir(null)
        return if (dir == null) {
            fileName
        } else {
            "${dir.absolutePath}/$fileName"
        }
    }

    fun getBitmapFromLocalPath(path: String): Bitmap? {
        val file = File(path)
        return if (file.exists()) BitmapFactory.decodeFile(path)
        else null
    }

    fun getBitmapFromRemotePath(context: Context, path: String, onReady: (bitmap: Bitmap) -> Unit): Bitmap? {
        Glide.with(context)
            .asBitmap()
            .load(path)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    onReady(resource)
                }
            })
        return null
    }

    fun isRemotePath(path: String) = path.startsWith("https://")
}