package com.glopez.phunapp.view.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.glopez.phunapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if(savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.constraint_layout, FeedFragment(), "eventList")
//                .addToBackStack(null)
//                .commit()
//        }
    }
}



