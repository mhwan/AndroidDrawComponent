package com.example.drawcomponent.util

import android.widget.Toast
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.toast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, duration).show()
}