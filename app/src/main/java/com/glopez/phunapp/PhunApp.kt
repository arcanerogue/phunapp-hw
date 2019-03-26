package com.glopez.phunapp

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import com.glopez.phunapp.data.EventRepository
import com.glopez.phunapp.data.db.EventDatabase

class PhunApp : Application() {
    private fun getDatabase(): EventDatabase {
        return EventDatabase.getDatabase(this)
    }

    fun getRepository(): EventRepository {
        return EventRepository.getInstance(this, getDatabase())
    }
}