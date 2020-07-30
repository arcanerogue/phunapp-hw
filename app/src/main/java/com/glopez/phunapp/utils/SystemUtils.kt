package com.glopez.phunapp.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun deviceCanCall(context: Context): Boolean {
    val packageManager: PackageManager? = context.packageManager
    return packageManager != null && packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
}