package com.glopez.phunapp.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.glopez.phunapp.R

class ShareHelper {
    companion object {
        fun createShareIntent(context: Context, message: String) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val packageManager = context.packageManager

            if (isIntentSafeToStart(packageManager, shareIntent)) {
                ContextCompat.startActivity(
                    context, Intent.createChooser(
                        shareIntent,
                        StringsResourceProvider.getString(R.string.share_intent_title)
                    ), null
                )
            } else {
                Toast.makeText(
                    context, context.getString(R.string.share_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}