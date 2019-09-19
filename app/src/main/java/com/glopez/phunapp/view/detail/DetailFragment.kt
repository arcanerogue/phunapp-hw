package com.glopez.phunapp.view.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.R
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.createEventDateFormatString
import com.glopez.phunapp.model.createShareEventMessage
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.IntentCategory
import com.glopez.phunapp.utils.IntentFactory
import com.glopez.phunapp.utils.deviceCanCall
import com.glopez.phunapp.view.ViewModelFactory
import kotlinx.android.synthetic.main.activity_event_detail.app_bar
import kotlinx.android.synthetic.main.activity_event_detail.detail_event_date
import kotlinx.android.synthetic.main.activity_event_detail.detail_event_description
import kotlinx.android.synthetic.main.activity_event_detail.detail_event_image
import kotlinx.android.synthetic.main.activity_event_detail.detail_event_location
import kotlinx.android.synthetic.main.activity_event_detail.detail_event_title
import kotlinx.android.synthetic.main.activity_event_detail.nested_scroll_view_group
import kotlinx.android.synthetic.main.fragment_event_detail.*
import timber.log.Timber
import java.lang.Exception

class DetailFragment : Fragment() {
    private lateinit var detailViewModel: DetailViewModel
    private var eventPhoneNumber: String = ""
    private var eventShareMessage: String = ""
    private var canCall: Boolean = false
    private lateinit var activityContext: Context
    private lateinit var phunApp: PhunApp
    private lateinit var listener: DetailFragmentListener

    companion object {
        private const val EVENT_ID: String = "event_id"

        fun newInstance(eventDetailId: Int) : DetailFragment {
            val arguments = Bundle()
            arguments.putSerializable(EVENT_ID, eventDetailId)
            val detailFragment = DetailFragment()
            detailFragment.arguments = arguments
            return detailFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContext = activity as Context
        phunApp = activity?.application as PhunApp
        canCall = deviceCanCall(phunApp.packageManager)
        listener = activity as DetailFragmentListener

        detailViewModel = ViewModelProviders.of(this, ViewModelFactory
            .getInstance(phunApp))
            .get(DetailViewModel::class.java)

        val eventDetailId = arguments?.getSerializable(EVENT_ID) as Int
//        val eventDetailId = 15
        observeEventDetail(eventDetailId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_detail_toolbar.apply {
            inflateMenu(R.menu.detail_menu)
            setNavigationIcon(R.drawable.ic_back_icon)

            setNavigationOnClickListener {
//                activity?.supportFragmentManager?.apply {
//                    beginTransaction()?.remove(this@DetailFragment)?.commit()
//                    popBackStack()
//                }
                listener.onNavigateBackFromDetail()
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.detail_action_call -> {
                        IntentFactory.create(activityContext, IntentCategory.CALL, eventPhoneNumber)
                        true
                    }
                    R.id.detail_action_share -> {
                        IntentFactory.create(
                            activityContext,
                            IntentCategory.SHARE,
                            eventShareMessage
                        )
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun observeEventDetail(eventId: Int) {
        detailViewModel.getEventDetailResource(eventId).observe(this, Observer { event ->
            event?.let {
                when (event) {
                    is Resource.Error -> handleViewsOnError(event.error)
                    is Resource.Success -> handleViewsOnSuccess(event.data)
                }
            }
        })
    }

    /**
     * Displays an "empty" View when the database cannot locate an StarWarsEvent to display to the user
     */
    private fun handleViewsOnError(error: Throwable?) {
        app_bar.setExpanded(false)

        fragment_detail_toolbar.apply {
            menu.findItem(R.id.detail_action_share).isVisible = false
            menu.findItem(R.id.detail_action_call).isVisible = false
        }

        nested_scroll_view_group.visibility = View.GONE

        Toast.makeText(activityContext, "Unable to locate StarWarsEvent details.", Toast.LENGTH_LONG)
            .show()
        Timber.e(error)
    }

    private fun handleViewsOnSuccess(starWarsEvent: StarWarsEvent?) {
        if (starWarsEvent != null) {
            eventPhoneNumber = starWarsEvent.phone ?: ""

            fragment_detail_toolbar.menu.findItem(R.id.detail_action_call).apply {
                if (eventPhoneNumber.isEmpty() || !canCall) {
                    isVisible = false
                }
            }

            if (starWarsEvent.date != null)
                detail_event_date.text = starWarsEvent.createEventDateFormatString()
            else
                detail_event_date.visibility = View.GONE

            eventShareMessage = starWarsEvent.createShareEventMessage()
            detail_event_title.text = starWarsEvent.title
            detail_event_location.text = starWarsEvent.location2
            detail_event_description.text = starWarsEvent.description

            Glide.with(this@DetailFragment)
                .load(starWarsEvent.image)
                .onlyRetrieveFromCache(true)
                .error(R.drawable.placeholder_nomoon)
                .centerCrop()
                .into(detail_event_image)
        } else {
            handleViewsOnError(Exception("The database returned a null StarWarsEvent object."))
        }
    }

    interface DetailFragmentListener {
        fun onNavigateBackFromDetail()
//        fun onMenuItemClick(menuItem: MenuItem)
    }
}