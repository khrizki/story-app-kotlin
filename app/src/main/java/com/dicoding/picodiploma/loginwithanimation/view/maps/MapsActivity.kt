package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.viewmodel.ViewModelProviderFactory
import com.dicoding.picodiploma.loginwithanimation.viewmodel.story.StoryViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel by viewModels<StoryViewModel> {
        ViewModelProviderFactory.getInstance(this)
    }

    companion object {
        private const val LOG_TAG = "MapViewActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadStoryLocations()
    }

    private fun loadStoryLocations() {
        lifecycleScope.launch {
            mapsViewModel.fetchMapsStories()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        addMarkersToMap()

        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
    }

    private fun addMarkersToMap() {
        mapsViewModel.storiesList.observe(this) { stories ->
            Log.d(LOG_TAG, "Story List: $stories")
            if (stories != null && stories.isNotEmpty()) {
                stories.forEach { story ->
                    val latLng = LatLng(story.lat!!.toDouble(), story.lon!!.toDouble())
                    Log.d(LOG_TAG, "Latitude: ${story.lat.toDouble()}, Longitude: ${story.lon}")

                    val marker = MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)

                    googleMap.addMarker(marker)
                }
            } else {
                Toast.makeText(this, "No location data available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
