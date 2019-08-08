package com.wakemeuphere

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    val PERMISSIONS_CODE = 101
    //private val BACKGROUND_LOCATION_REQUEST_CODE = 1011
    //private val READ_STORAGE_REQUEST_CODE = 1012

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.shouldShowRequestPermissionRationale(this, "TEESTE")
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_CODE )
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_REQUEST_CODE )
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_REQUEST_CODE )
        setContentView(R.layout.activity_main)
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
                    val intent = Intent(this, MapsActivity::class.java);
                    startActivity(intent)
                    finish()
                }
            }
            else -> {
                Log.i("PERMISSÃO","codigo bizarro")
            }
        }
    }
}
