package com.glopez.phunapp.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.glopez.phunapp.R
import com.glopez.phunapp.utils.CallHelper
import com.glopez.phunapp.utils.ShareHelper
import com.glopez.phunapp.view.StarWarsUiEvent
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

    override fun onFeedEventClicked(event: StarWarsUiEvent) {
        val detailFragment = DetailFragment.newInstance(event.id)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, detailFragment, "eventDetail")
            .addToBackStack(null)
            .commit()
    }

    override fun onShareClicked(event: StarWarsUiEvent) {
        ShareHelper.createShareIntent(this, event.shareMessage)
    }

    override fun onNavigateBackFromDetail() {
        supportFragmentManager.apply {
            val detailFragment = this.findFragmentByTag("eventDetail")

            if (detailFragment != null) {
                beginTransaction()
                    .remove(detailFragment)
                    .commit()
            }
            popBackStack()
        }
    }

    override fun onDetailMenuItemClick(menuItem: MenuItem, phoneNumber: String, shareMessage: String) {
        when (menuItem.itemId) {
            R.id.detail_action_call -> {
                CallHelper.createCallIntent(this, phoneNumber)
            }
            R.id.detail_action_share -> {
                ShareHelper.createShareIntent(this, shareMessage)
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}



