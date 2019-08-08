package com.wakemeuphere.internal

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.Serializable

class Alarm : JSONObject, Serializable {

    var id: Int = 0
    var title: String = ""
    var minDistance: Int = 0
    var definitiveDistance: Int = 0
    var soundId: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor() {}
    constructor(json: String) : super(json) { }

    fun setLatLng (position: LatLng) {
        longitude = position.longitude
        latitude = position.latitude
    }
}