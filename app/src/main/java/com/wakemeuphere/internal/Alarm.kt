package com.wakemeuphere.internal

import com.beust.klaxon.Json
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class Alarm {

    var id: Int = 0
    var active: Boolean = true
    var title: String = ""
    var distance: Int = 0
        set(value) { field = Utils.distanceInRange(value) }
    var soundId: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    @Json(ignored = true)
    lateinit var marker: Marker
    @Json(ignored = true)
    lateinit var circle: Circle

    @Json(ignored = true)
    var point: LatLng
    set(value) {
        longitude = value.longitude
        latitude = value.latitude
    }
    get() = LatLng(latitude, longitude)
}