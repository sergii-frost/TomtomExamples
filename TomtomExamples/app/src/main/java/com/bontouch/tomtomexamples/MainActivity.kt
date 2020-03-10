package com.bontouch.tomtomexamples

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.tomtom.online.sdk.map.MapFragment
import com.tomtom.online.sdk.map.TomtomMap
import com.tomtom.online.sdk.map.ktx.applyOnTomtomMap
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var tomtomMap: TomtomMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs).also {
            initMap()
        }
    }

    private fun initMap() {
        (map_fragment as? MapFragment)?.applyOnTomtomMap {
            //Skip if map was already initialised
            if (tomtomMap == this) { return@applyOnTomtomMap }

            tomtomMap = this
            isMyLocationEnabled = true
        }
    }
}
