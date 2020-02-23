package com.glopez.phunapp.utils

import androidx.annotation.StringRes

interface StringsProvider {
    fun getString(@StringRes resourceId: Int): String
}