package com.glopez.phunapp.utils

import android.content.res.Resources

object StringsResourceProvider : StringsProvider {
    private lateinit var resources: Resources

    fun init(resources: Resources) {
        this.resources = resources
    }

    override fun getString(resourceId: Int): String {
        return resources.getString(resourceId)
    }
}