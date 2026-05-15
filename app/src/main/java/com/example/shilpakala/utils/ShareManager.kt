package com.example.shilpakala.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class ShareManager @Inject constructor() {
    fun shareImage(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share ShilpaKala photo"))
    }
}
