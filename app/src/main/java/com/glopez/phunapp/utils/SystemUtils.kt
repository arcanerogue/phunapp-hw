package com.glopez.phunapp.utils

import android.content.pm.PackageManager
import android.net.ConnectivityManager

fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun deviceCanCall(packageManager: PackageManager): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
}