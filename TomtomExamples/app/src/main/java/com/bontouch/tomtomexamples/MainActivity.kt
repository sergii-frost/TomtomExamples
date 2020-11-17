package com.bontouch.tomtomexamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomtom.online.sdk.map.*
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
     * Issues:
     * - chevron is blinking when location is being updated and chevron is to be shown again
     */
    private fun startTracking() {
        val userLocation = tomtomMap?.userLocation ?: return
        val drivingSettings = tomtomMap?.drivingSettings ?: return
        if (drivingSettings.isTracking) return
        val chevron = if (drivingSettings.chevrons.isEmpty()) {
            val chevronIcon =
                Icon.Factory.fromResources(applicationContext, R.drawable.ic_navigation)
            drivingSettings.addChevron(
                ChevronBuilder.create(chevronIcon, chevronIcon).initialPosition(ChevronPosition.Builder(userLocation).build())
            )
        } else {
            drivingSettings.chevrons.first()
        }

        drivingSettings.startTracking(chevron)
        chevron.show()
        tomtomMap?.addLocationUpdateListener {
            chevron.position = ChevronPosition.Builder(it).build()
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
            zoomTo(19.0)
        }
    }
}
