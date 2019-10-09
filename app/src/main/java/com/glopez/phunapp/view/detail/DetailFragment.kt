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
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.deviceCanCall
import com.glopez.phunapp.view.StarWarsUiEvent
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
                listener.onNavigateBackFromDetail()
            }
            setOnMenuItemClickListener {
                listener.onDetailMenuItemClick(it, eventPhoneNumber, eventShareMessage)
                true
            }
        }
    }

    private fun observeEventDetail(eventId: Int) {
        detailViewModel.getEventDetailResource(eventId).observe(this, Observer { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Error -> handleViewsOnError(resource.error)
                    is Resource.Success -> handleViewsOnSuccess(resource.data)
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

    private fun handleViewsOnSuccess(starWarsUiEvent: StarWarsUiEvent?) {
        if (starWarsUiEvent != null) {
            eventPhoneNumber = starWarsUiEvent.phone

            fragment_detail_toolbar.menu.findItem(R.id.detail_action_call).apply {
                if (eventPhoneNumber.isBlank() || !canCall) {
                    isVisible = false
                }
            }

            if (starWarsUiEvent.date.isBlank())
                detail_event_date.visibility = View.GONE
            else
                detail_event_date.text = starWarsUiEvent.date

            eventShareMessage = starWarsUiEvent.shareMessage
            detail_event_title.text = starWarsUiEvent.title
            detail_event_location.text = starWarsUiEvent.location2
            detail_event_description.text = starWarsUiEvent.description

            Glide.with(this@DetailFragment)
                .load(starWarsUiEvent.imageUrl)
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
        fun onDetailMenuItemClick(menuItem: MenuItem, phoneNumber: String, shareMessage: String)
    }
}