package com.glopez.phunapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.glopez.phunapp.R

class CallHelper {
    companion object {
        fun createCallIntent(context: Context, phoneNumber: String) {
            if (phoneNumber.isEmpty()) {
                Toast.makeText(
                    context, StringsResourceProvider.getString(R.string.event_detail_no_number),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val packageManager = context.packageManager
                val dialerIntent = Intent(Intent.ACTION_DIAL)
                dialerIntent.data = Uri.parse("tel:$phoneNumber")
                if (isIntentSafeToStart(packageManager, dialerIntent)) {
                    ContextCompat.startActivity(context, dialerIntent, null)
                } else {
                    Toast.makeText(
                        context, StringsResourceProvider.getString(R.string.event_detail_unable_call),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}