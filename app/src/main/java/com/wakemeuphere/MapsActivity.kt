package com.wakemeuphere

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import androidx.fragment.app.FragmentActivity
import com.wakemeuphere.internal.Alarm
import com.wakemeuphere.internal.AppMemoryManager
import com.wakemeuphere.internal.AppMemoryManager.alarmSelected
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Circle
import com.wakemeuphere.internal.AlarmNotification
import com.wakemeuphere.internal.songs.SongManager
import com.wakemeuphere.ui.form.FormFragment
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private var isInitLocalSet: Boolean = false
    private var activeFragment: Int = 0


    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppMemoryManager.load(this)//Loads the memory
        SongManager.loadSongs(this)

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.alarm_map) as SupportMapFragment
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
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        loadMarkers()
        currentLocation()

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

//                val an = AlarmNotification()
//                val notif = an.createNotification(this@MapsActivity)
//                an.showNotification(this@MapsActivity, notif)

//                return true

                //Elvis operator https://en.wikipedia.org/wiki/Elvis_operator
                val alarm = AppMemoryManager.alarms.find { alarm -> alarm.marker == marker } ?: return true
                alarmSelected = alarm

                var formFragment = FormFragment()
                activeFragment = formFragment.id
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, formFragment)
                transaction.addToBackStack(null)
                transaction.commit()

//                val intent = Intent(this@MapsActivity, AlarmForm::class.java);
//                startActivity(intent)

                return true
            }
        })


    }


    fun removeActiveFragment(){
        if(this.activeFragment != null)
        {
            var fragment: Fragment? = supportFragmentManager.findFragmentById(this.activeFragment)
            val transaction = supportFragmentManager.beginTransaction()
            if (fragment != null) {
                transaction.remove(fragment)
                this.activeFragment = 0
            }
        }
    }

//    private fun setActiveFragment(fragment: Fragment){
//        this.activeFragment = fragment
//    }
//
//    private fun getActiveFragment(){
//        return this.activeFragment
//    }
//
//    private fun createActiveFragment(){
//
//    }

    //https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient
    //https://medium.com/@droidbyme/get-current-location-using-fusedlocationproviderclient-in-android-cb7ebf5ab88e
    @SuppressLint("MissingPermission")//TODO review this permission
    private fun currentLocation()
    {
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        val point = LatLng(location.latitude, location.longitude)
                        if (isInitLocalSet == false)
                        {
                            isInitLocalSet = true
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15f))
                            return
                        }
                    }
                }
            }
        }

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
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
        }
    }


}
