package com.glopez.phunapp.utils

import android.support.annotation.StringRes

interface StringsProvider {
    fun getString(@StringRes resourceId: Int): String
}