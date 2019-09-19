package com.glopez.phunapp.view.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.glopez.phunapp.R
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.createShareEventMessage
import com.glopez.phunapp.utils.IntentCategory
import com.glopez.phunapp.utils.IntentFactory
import com.glopez.phunapp.view.detail.DetailFragment
import com.glopez.phunapp.view.feed.FeedFragment

class MainActivity : AppCompatActivity(), FeedFragment.FeedFragmentListener,
    DetailFragment.DetailFragmentListener{

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

    override fun onFeedEventClicked(event: StarWarsEvent) {
        val detailFragment = DetailFragment.newInstance(event.id)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, detailFragment, "eventDetail")
            .addToBackStack(null)
            .commit()
    }

    override fun onShareClicked(event: StarWarsEvent) {
        val shareMessage = event.createShareEventMessage()
        IntentFactory.create(this, IntentCategory.SHARE, shareMessage)
    }

    override fun onNavigateBackFromDetail() {
        supportFragmentManager.apply {
            val detailFragment = this.findFragmentByTag("eventDetail")

            beginTransaction()
            .remove(detailFragment)
            .commit()

            popBackStack()
        }
    }

//    override fun onMenuItemClick(menuItem: MenuItem) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}



