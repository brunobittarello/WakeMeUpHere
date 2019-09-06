package com.wakemeuphere

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
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


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, FormFragment.OnCircleChangedListener {
    override fun circleValue(radius: Double) {
        this.newCircle?.radius = radius
    }

    private lateinit var mMap: GoogleMap
    private var isInitLocalSet: Boolean = false
    private var activeFragment: Int = 0
    var newCircle: Circle? = null

    private val fillColorGreen = Color.parseColor("#6b7ab8a6")
    private val strokeColorGreen = Color.parseColor("#5c8579")
    private val fillColorNew = Color.parseColor("#6b4a66f2")
    private val strokeColorNew= Color.parseColor("#3e53bf")


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

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is FormFragment) {
            fragment.setOnCircleChanged(this)
        }
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
                alarmSelected = newAlarm

                newCircle = mMap.addCircle(
                    CircleOptions()
                        .center(marker.position)
                        .radius(0.0)
                        .strokeColor(strokeColorNew)
                        .fillColor(fillColorNew)
                )

                openFormFragment()

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

                openFormFragment()

//                val intent = Intent(this@MapsActivity, AlarmForm::class.java);
//                startActivity(intent)

                return true
            }
        })


    }

    fun openFormFragment(){
        var formFragment = FormFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, formFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        toggleBtnLayout(true)
    }

    fun toggleBtnLayout(isVisible:Boolean){
        var btnView = findViewById<LinearLayout>(R.id.btnLayout)

        if(!isVisible){
            btnView.layoutParams.height = 0
            btnView.visibility = View.INVISIBLE
            return
        }

        btnView.layoutParams.height = 150
        btnView.visibility = View.VISIBLE
    }

    fun removeActiveFragment(){
        toggleBtnLayout(false)
        supportFragmentManager.popBackStack()
    }

    fun onButtonCancelClicked(view: View) {
        removeActiveFragment()
    }

    fun onButtonDeleteClicked(view: View) {

        //https://medium.com/@suragch/making-an-alertdialog-in-android-2045381e2edb
        val builder = AlertDialog.Builder(baseContext)


        builder.setTitle("Remove alert")//TODO use resource
        builder.setMessage("Are you want to remove this alert?")//TODO use resource

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES"){dialog, which ->
            AppMemoryManager.deleteSelectedAlarm()
            Toast.makeText(baseContext, "Alarm deleted!", Toast.LENGTH_SHORT).show()
//            onBackPressed()
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("No"){dialog,which ->
            //Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
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

            val circle = mMap.addCircle(
                CircleOptions()
                    .center(point)
                    .radius(alarm.minDistance.toDouble())
                    .strokeColor(this.strokeColorGreen)
                    .fillColor(this.fillColorGreen)
            )
        }
    }


}
