package com.glopez.phunapp.view.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.glopez.phunapp.R
import com.glopez.phunapp.view.feed.FeedFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, FeedFragment(), "eventList")
                .commit()
        }
    }
}



