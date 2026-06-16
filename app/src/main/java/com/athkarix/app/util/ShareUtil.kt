package com.athkarix.app.util

import android.content.Context
import android.content.Intent

/** System share-sheet helpers for sharing athkar text or the app itself. */
object ShareUtil {
    fun shareText(context: Context, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, context.getString(com.athkarix.app.R.string.share_title)))
    }

    fun shareApp(context: Context) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, context.getString(com.athkarix.app.R.string.app_name) + " - https://play.google.com/store/apps/details?id=${context.packageName}")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, context.getString(com.athkarix.app.R.string.share_app_title)))
    }
}
