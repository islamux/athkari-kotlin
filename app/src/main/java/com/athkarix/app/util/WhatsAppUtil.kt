package com.athkarix.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Opens a WhatsApp chat via wa.me URI, falling back to the Play Store if WhatsApp is not installed. */
object WhatsAppUtil {

    private const val COUNTRY_CODE = "967"
    private const val PHONE_NUMBER = "772699924"

    fun openWhatsApp(context: Context) {
        try {
            val uri = Uri.parse("https://wa.me/$COUNTRY_CODE$PHONE_NUMBER")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                // fallback — do nothing
            }
        }
    }
}
