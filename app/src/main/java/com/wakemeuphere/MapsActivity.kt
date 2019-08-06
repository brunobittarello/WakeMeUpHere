package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import android.content.Intent
import com.wakemeuphere.internal.Alarm
import com.wakemeuphere.internal.AppMemoryManager
import com.wakemeuphere.internal.AppMemoryManager.LoadMarkers
import com.wakemeuphere.internal.AppMemoryManager.alarms


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppMemoryManager.teste = "MAPS"

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        LoadMarkers()
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(AppMemoryManager.alarms[0].latitude, AppMemoryManager.alarms[0].longitude)))//TODO change to the current GPS position

        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(position: LatLng?) {
                Log.d("Map_Tag", "CLICK")
                if (position == null)
                    return

                mMap.addMarker(MarkerOptions().position(position).title("Novo ponto"))
            }
        })


        mMap.setOnMapLongClickListener(object : GoogleMap.OnMapLongClickListener{
            override fun onMapLongClick(point: LatLng) {

                mMap.addMarker(MarkerOptions().position(point));

            }
        })

        mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                //clicking on the marker should take the user to the alarm setup
//                val intent = Intent(this, SetupAlarmActivity::class.java);
                val alarm = Alarm()

                val intent = Intent(this@MapsActivity, AlarmForm::class.java);
                intent.putExtra("AlarmObject", alarm)
                startActivity(intent)
                Log.d("Marker_tag", "MARKER CLICKED")
                return true;
            }
        })


    }

    fun LoadMarkers() {
        AppMemoryManager.LoadMarkers()
        for (alarm in AppMemoryManager.alarms)
        {
            val point = LatLng(alarm.latitude, alarm.longitude)
            mMap.addMarker(MarkerOptions().position(point).title(alarm.title))
        }

    }
}
