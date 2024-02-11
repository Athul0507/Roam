package com.example.roam

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

object ToastUtils {
    fun showToast(context: Context, message: String, error: Int) {
        // Inflate custom layout for toast
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(if(error==0) R.layout.custom_toast_nice else R.layout.custom_toast_error , null)


        // Set message to the TextView in the custom layout
        val textView = layout.findViewById<TextView>(R.id.toastText)
        textView.text = message

        // Create and display the toast
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
