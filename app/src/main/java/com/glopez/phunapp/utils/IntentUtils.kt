package com.glopez.phunapp.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

fun isIntentSafeToStart(packageManager: PackageManager, intent: Intent): Boolean {
    val activities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
    return activities.isNotEmpty()
}