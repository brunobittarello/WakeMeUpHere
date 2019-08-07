package com.wakemeuphere.internal

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Alarm : Serializable {

    var id: Int = 0
    var title: String = ""
    var minDistance: Int = 0
    var definitiveDistance: Int = 0
    var soundId: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    fun SetLatLng (position: LatLng) {
        longitude = position.longitude
        latitude = position.latitude
    }
}