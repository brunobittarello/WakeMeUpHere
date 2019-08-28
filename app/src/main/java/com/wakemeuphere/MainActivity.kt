package com.wakemeuphere

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wakemeuphere.internal.Alarm
import com.wakemeuphere.internal.AlarmNotification
import com.wakemeuphere.internal.AppMemoryManager
import com.wakemeuphere.internal.songs.SongManager


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    val PERMISSIONS_CODE = 101
    //private val BACKGROUND_LOCATION_REQUEST_CODE = 1011
    //private val READ_STORAGE_REQUEST_CODE = 1012

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getString(R.string.CANAL_BRASIL)
        ActivityCompat.shouldShowRequestPermissionRationale(this, "TEESTE")
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_CODE )
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_REQUEST_CODE )
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_REQUEST_CODE )
        setContentView(R.layout.activity_main)

        AppMemoryManager.load(this)//Loads the memory
        SongManager.loadSongs(this)

        //supportFragmentManager.beginTransaction().add(Frag , "tag").commit()
        val result = supportFragmentManager.findFragmentById(R.id.fragmap)
        if (result == null)
        {
            Log.e("MATHEUS", "NULO")
            return
        }


        val mapFragment = result.childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }





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

                val an = AlarmNotification()
                val notif = an.createNotification(this@MainActivity)
                an.showNotification(this@MainActivity, notif)

                return true

                val alarm = AppMemoryManager.alarms.find { alarm -> alarm.marker == marker } ?: return true//Elvis operator https://en.wikipedia.org/wiki/Elvis_operator

                AppMemoryManager.alarmSelected = alarm
                val intent = Intent(this@MainActivity, AlarmForm::class.java);
                startActivity(intent)

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


    private var isInitLocalSet: Boolean = false
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













    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_CODE -> {
                
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED

                for(i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]

                if(perms[Manifest.permission.ACCESS_FINE_LOCATION] != PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] != PackageManager.PERMISSION_GRANTED)
                    finish()
                else{
//                    val intent = Intent(this, MapsActivity::class.java);
//                    startActivity(intent)
//                    finish()
                }
            }
            else -> {
                Log.i("PERMISSÃO","codigo bizarro")
            }
        }
    }
}
