package com.glopez.phunapp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.glopez.phunapp.R

object Utils {

    private fun isIntentSafeToStart(context: Context, intent: Intent): Boolean {
        val activities: List<ResolveInfo> = context.applicationContext
            .packageManager.queryIntentActivities(intent, 0)
        return activities.isNotEmpty()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun deviceCanCall(context: Context): Boolean {
        return context.applicationContext
            .packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }


    fun createShareIntent(context: Context, message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        if (isIntentSafeToStart(context, shareIntent)) {
            ContextCompat.startActivity(
                context, Intent.createChooser(
                    shareIntent,
                    context.getString(R.string.share_intent_title)
                ), null
            )
        } else {
            Toast.makeText(context, context.getString(R.string.share_failed),
                Toast.LENGTH_LONG).show()
        }
    }

    fun createCallIntent(context: Context, phoneNumber: String) {
        if (phoneNumber.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.event_detail_no_number),
                Toast.LENGTH_LONG).show()
        } else {
            val dialerIntent = Intent(Intent.ACTION_DIAL)
            dialerIntent.data = Uri.parse("tel:$phoneNumber")
            if (isIntentSafeToStart(context, dialerIntent)) {
                ContextCompat.startActivity(context, dialerIntent, null)
            } else {
                Toast.makeText(context, context.getString(R.string.event_detail_unable_call),
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}