package com.glopez.phunapp.view.detail

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.glopez.phunapp.R
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.deviceCanCall
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.ViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_event_detail.*
import timber.log.Timber
import java.lang.Exception

class DetailFragment : Fragment() {
    private lateinit var detailViewModel: DetailViewModel
    private var eventPhoneNumber: String = ""
    private var eventShareMessage: String = ""
    private lateinit var listener: DetailFragmentListener
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var eventDate: TextView
    private lateinit var eventTitle: TextView
    private lateinit var eventLocation: TextView
    private lateinit var eventDescription: TextView
    private lateinit var eventImageView: ImageView

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
        listener = activity as DetailFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater
            .inflate(R.layout.fragment_event_detail, container, false)
            .apply {
                toolbar = findViewById(R.id.fragment_detail_toolbar)
                appBarLayout = findViewById(R.id.app_bar)
                nestedScrollView = findViewById(R.id.nested_scroll_view_group)
                eventDate = findViewById(R.id.detail_event_date)
                eventTitle = findViewById(R.id.detail_event_title)
                eventLocation = findViewById(R.id.detail_event_location)
                eventDescription = findViewById(R.id.detail_event_description)
                eventImageView = findViewById(R.id.detail_event_image)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailViewModel = ViewModelProvider(this, ViewModelFactory)
            .get(DetailViewModel::class.java)

        val eventDetailId = arguments?.getSerializable(EVENT_ID) as Int
        detailViewModel.getEventDetail(eventDetailId)
        observeEventDetail()

        toolbar.apply {
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

    private fun observeEventDetail() {
        detailViewModel.eventDetail.observe(viewLifecycleOwner, Observer
        { resource ->
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
        appBarLayout.setExpanded(false)

        toolbar.apply {
            menu.findItem(R.id.detail_action_share).isVisible = false
            menu.findItem(R.id.detail_action_call).isVisible = false
        }

        nestedScrollView.visibility = View.GONE

        context?.run {
            Toast.makeText(this,"Unable to locate StarWarsEvent details.",
                Toast.LENGTH_LONG).show()
        }
        Timber.e(error)
    }

    private fun handleViewsOnSuccess(starWarsUiEvent: StarWarsUiEvent?) {
        if (starWarsUiEvent != null) {
            eventPhoneNumber = starWarsUiEvent.phone

            toolbar.menu.findItem(R.id.detail_action_call).apply {
                context?.run {
                    if (eventPhoneNumber.isBlank() || !deviceCanCall(this)) {
                        isVisible = false
                    }
                }
            }

            if (starWarsUiEvent.date.isBlank())
                eventDate.visibility = View.GONE
            else
                eventDate.text = starWarsUiEvent.date

            eventShareMessage = starWarsUiEvent.shareMessage
            eventTitle.text = starWarsUiEvent.title
            eventLocation.text = starWarsUiEvent.location2
            eventDescription.text = starWarsUiEvent.description

            Glide.with(this@DetailFragment)
                .load(starWarsUiEvent.imageUrl)
                .onlyRetrieveFromCache(true)
                .error(R.drawable.placeholder_nomoon)
                .centerCrop()
                .into(eventImageView)
        } else {
            handleViewsOnError(Exception("The database returned a null StarWarsEvent object."))
        }
    }

    interface DetailFragmentListener {
        fun onNavigateBackFromDetail()
        fun onDetailMenuItemClick(menuItem: MenuItem, phoneNumber: String, shareMessage: String)
    }
}