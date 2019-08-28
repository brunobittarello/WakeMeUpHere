package com.wakemeuphere.ui.mundi

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wakemeuphere.AlarmForm
import com.wakemeuphere.R
import com.wakemeuphere.internal.Alarm
import com.wakemeuphere.internal.AlarmNotification
import com.wakemeuphere.internal.AppMemoryManager


class MundiFragment : Fragment() {

    companion object {
        fun newInstance() = MundiFragment()
    }

//    private lateinit var viewModel: FormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var rootView: View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment.getMapAsync(MyMaps())
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(FormViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

class MyMaps : OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    constructor(){
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadMarkers()

        mMap.setMyLocationEnabled(true)

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(AppMemoryManager.alarms[0].latitude, AppMemoryManager.alarms[0].longitude)))//TODO change to the current GPS position

        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(position: LatLng?) {
                Log.d("Map_Tag", "CLICK")
            }
        })


        mMap.setOnMapLongClickListener(object : GoogleMap.OnMapLongClickListener{
            override fun onMapLongClick(position: LatLng) {

                if (position == null)
                    return

                var marker = mMap.addMarker(MarkerOptions().position(position).title("Novo ponto"))

                var newAlarm = Alarm()
                newAlarm.setLatLng(position)
                newAlarm.marker = marker

                AppMemoryManager.addAlarm(newAlarm)

            }
        })

        mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {

                val an = AlarmNotification()
//                val notif = an.createNotification(this)
//                an.showNotification(this@MyMaps, notif)

                return true

                val alarm = AppMemoryManager.alarms.find { alarm -> alarm.marker == marker } ?: return true//Elvis operator https://en.wikipedia.org/wiki/Elvis_operator

                AppMemoryManager.alarmSelected = alarm
//                val intent = Intent(this, AlarmForm::class.java);
//                startActivity(intent)

                return true
            }
        })


    }

    private fun loadMarkers() {
        for (alarm in AppMemoryManager.alarms)
        {
            val point = LatLng(alarm.latitude, alarm.longitude)
            alarm.marker = mMap.addMarker(MarkerOptions().position(point).title(alarm.title))
            //draw circle
            val fillColor = Color.parseColor("#6b7ab8a6")
            val circle = mMap.addCircle(
                CircleOptions()
                    .center(point)
                    .radius(10000.0)
                    .strokeColor(Color.RED)
                    .fillColor(fillColor)
            )

            //adiciona zoom
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 20.0f))
        }
    }
}
