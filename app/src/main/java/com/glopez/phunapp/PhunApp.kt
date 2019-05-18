package com.glopez.phunapp

import android.app.Application
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.db.EventDatabase

class PhunApp : Application() {
    private fun getDatabase(): EventDatabase {
        return EventDatabase.getDatabase(this)
    }

    fun getRepository(): EventRepository {
        return EventRepository.getInstance(this, getDatabase())
    }
}