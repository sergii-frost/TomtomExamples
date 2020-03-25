package com.bontouch.tomtomexamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomtom.online.sdk.common.location.LatLng
import com.tomtom.online.sdk.map.ChevronBuilder
import com.tomtom.online.sdk.map.Icon
import com.tomtom.online.sdk.map.MapFragment
import com.tomtom.online.sdk.map.TomtomMap
import com.tomtom.online.sdk.map.ktx.applyOnTomtomMap
import com.tomtom.online.sdk.map.model.MapModeType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var tomtomMap: TomtomMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
        initButtons()
    }

    private fun initMap() {
        (map_fragment as? MapFragment)?.applyOnTomtomMap {
            //Skip if map was already initialised
            if (tomtomMap == this) {
                return@applyOnTomtomMap
            }

            tomtomMap = this
            isMyLocationEnabled = true
        }
    }

    private fun initButtons() {
        start_navigation_button.setOnClickListener {
            startTracking()
        }

        stop_navigation_button.setOnClickListener {
            stopTracking()
        }
    }

    /**
     *
     * Implementation is based on:
     * - https://developer.tomtom.com/maps-sdk-android/follow-chevron-0
     *
     * What we want to achieve when tracking user location in driving mode:
     * - tweak zoom mode so that user can see road more closely
     * - tweak 3D mode and map tilt so that it feels more natural while navigation
     * - have chevron in the bottom center of the scree so that more map is visible for the user (as it is e.g. in TomTom Go app)
     *
     * Issues:
     * - on first attempt to start tracking newly added chevron, it seems that it has zero location coordinates
     * - thus it rapidly moves on the map from that guessed location to the current user location
     *   - In order to reproduce this issue, start the app, zoom out fully, tap on Start button
     *   - you will see that chevron is being added on the map, but it moves from lat lon 0,0 to your position
     *   - One more case to reproduce is below:
     *     - restart the app and zoom into user location
     *     - tap the Start button and observe that chevron is quickly moving on the map to the current position
     *
     * - we are unable to set zoom level programmatically while using drivingSettings.startTracking()
     * - we are unable to set position of chevron on the map, as it is always placed in the center of the screen
     */
    private fun startTracking() {
        val userLocation = tomtomMap?.userLocation ?: return
        val drivingSettings = tomtomMap?.drivingSettings ?: return
        if (drivingSettings.isTracking) return
        val chevron = if (drivingSettings.chevrons.isEmpty()) {
            val chevronIcon =
                Icon.Factory.fromResources(applicationContext, R.drawable.ic_navigation)
            drivingSettings.addChevron(
                ChevronBuilder.create(chevronIcon, chevronIcon).location(
                    LatLng(userLocation.latitude, userLocation.longitude)
                )
            )
        } else {
            drivingSettings.chevrons.first()
        }

        drivingSettings.startTracking(chevron)
        chevron.show()
        tomtomMap?.addLocationUpdateListener {
            chevron.setLocation(it)
            chevron.show()
        }
        setupMapForTracking()
    }

    private fun stopTracking() {
        if (tomtomMap?.drivingSettings?.isTracking == false) return
        tomtomMap?.apply {
            drivingSettings.stopTracking()
            drivingSettings.chevrons.firstOrNull()?.hide()
            defaultLocationSource.removeAllLocationUpdateListeners()
        }
    }

    private fun setupMapForTracking() {
        tomtomMap?.apply {
            uiSettings.mapModeType = MapModeType.MODE_3D
            zoomTo(19.0) // <-- this is ignored when tracking chevron
        }
    }
}
