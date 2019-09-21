package com.wakemeuphere

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.GoogleMap.*
import com.wakemeuphere.internal.AppMemoryManager.alarmSelected
import com.google.android.gms.maps.model.CircleOptions
import com.wakemeuphere.internal.*
import com.wakemeuphere.internal.songs.SongManager
import com.wakemeuphere.ui.fragments.AlarmFormFragment

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener, OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener {

    private lateinit var mMap: GoogleMap
    private var isInitLocalSet: Boolean = false
    var newMarker: Marker? = null
    var alarmFormFragment: AlarmFormFragment? = null
    var btnView: View? = null
    var fragmentVisible: Boolean = false

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
        //Nothing
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

        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMarkerDragListener(this)
    }

    private fun openFormFragment(){
        supportFragmentManager.popBackStack()
        alarmFormFragment = AlarmFormFragment()
        alarmFormFragment?.listener = ::focusOnSelectedMarker
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, alarmFormFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()

        toggleBtnLayout(true)
    }

    //---- Region Maps Interfaces ----//
    override fun onMapClick(position: LatLng?) {
        Log.d("Map_Tag", "CLICK")
    }

    override fun onMapLongClick(position: LatLng) {
        newMarker = mMap.addMarker(MarkerOptions().position(position).title("Novo ponto"))!! as Marker

        val newAlarm = Alarm()
        newAlarm.point = position
        newAlarm.marker = newMarker as Marker

        AppMemoryManager.addAlarm(newAlarm)
        alarmSelected = newAlarm

        alarmSelected.circle = mMap.addCircle(
            CircleOptions().center(newMarker!!.position).radius(Utils.alarm_distance_min.toDouble())
        )
        alarmSelected.select(resources)

        openFormFragment()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (fragmentVisible) {
            alarmSelected.deselect(resources)
        }
        //val an = AlarmNotification()
        //val notif = an.createNotification(this@MapsActivity)
        //an.showNotification(this@MapsActivity, notif)

        //return true

        //Elvis operator https://en.wikipedia.org/wiki/Elvis_operator
        val alarm = AppMemoryManager.alarms.find { alarm -> alarm.marker == marker } ?: return true

        alarmSelected = alarm
        alarmSelected.select(resources)

        openFormFragment()
        focusOnSelectedMarker(alarmSelected.distance)

        return true
    }

    override fun onMarkerDragStart(marker: Marker?) {
        if (!isAValidMarkerDrag(marker)) return
    }

    override fun onMarkerDrag(marker: Marker?) {
        if (!isAValidMarkerDrag(marker)) return
        alarmFormFragment?.onMarkerMoved()
    }

    override fun onMarkerDragEnd(marker: Marker?) {
        if (!isAValidMarkerDrag(marker)) return
    }

    private fun isAValidMarkerDrag(marker: Marker?) : Boolean
    {
        return alarmFormFragment != null && marker == AppMemoryManager.alarmSelected.marker
    }
    //---- END Region Maps Interfaces ----//

    private fun focusOnSelectedMarker(distance: Int) {
        val bounds = Utils.boundsByDistance(alarmSelected.point, alarmSelected.distance)
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 45))
    }

    private fun toggleBtnLayout(isVisible:Boolean){
        btnView = findViewById<LinearLayout>(R.id.btnLayout)
        fragmentVisible = isVisible

        if(!isVisible){
            btnView!!.layoutParams.height = 0
            btnView!!.visibility = View.INVISIBLE
            return
        }

        btnView!!.layoutParams.height = 150
        btnView!!.visibility = View.VISIBLE
    }

    private fun removeActiveFragment(){
        toggleBtnLayout(false)
        supportFragmentManager.popBackStack()
    }

    fun onButtonCancelClicked(view: View) {
        alarmSelected.deselect(resources)
        newMarker?.remove()
        removeActiveFragment()
    }

    fun onButtonSaveClicked(view: View) {
        alarmFormFragment?.saveForm(btnView!!)
        alarmSelected.deselect(resources)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alarm saved!")
        builder.setMessage("Your alarm " + alarmSelected.title + " has been saved")
        builder.show()
        removeActiveFragment()
//        mMap.clear()
//        loadMarkers()
    }

    fun onButtonDeleteClicked(view: View) {
        Log.d("DELETE", "onButtonDeleteClicked")

        //https://medium.com/@suragch/making-an-alertdialog-in-android-2045381e2edb
        val builder = AlertDialog.Builder(this)


        builder.setTitle("Remove alert")//TODO use resource
        builder.setMessage("Do you really want to remove " + alarmSelected.title + " alarm?")//TODO use resource

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES"){ _, _ ->
            AppMemoryManager.deleteSelectedAlarm()
            alarmFormFragment?.deleteAlarm()
            removeActiveFragment()
            Toast.makeText(baseContext, "Alarm deleted!", Toast.LENGTH_SHORT).show()
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("No"){dialog,which ->
            //Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

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
            alarm.circle = mMap.addCircle(CircleOptions().center(point).radius(alarm.distance.toDouble()))
            alarm.deselect(resources)
            //draw circle
        }
    }
}
