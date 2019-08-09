package com.glopez.phunapp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.glopez.phunapp.R

fun createShareIntent(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }

    val packageManager = context.applicationContext
        .packageManager
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

fun createCallIntent(context: Context, phoneNumber: String) {
    if (phoneNumber.isEmpty()) {
        Toast.makeText(
            context, StringsResourceProvider.getString(R.string.event_detail_no_number),
            Toast.LENGTH_LONG
        ).show()
    } else {
        val packageManager = context.applicationContext
            .packageManager
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

fun isIntentSafeToStart(packageManager: PackageManager, intent: Intent): Boolean {
    val activities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
    return activities.isNotEmpty()
}