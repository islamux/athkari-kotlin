package com.athkarix.app.util

import android.content.Context
import android.content.Intent

object ShareUtil {
    fun shareText(context: Context, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "مشاركة"))
    }

    fun shareApp(context: Context) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "تطبيق أذكاري - https://play.google.com/store/apps/details?id=${context.packageName}")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة التطبيق"))
    }
}
