package com.wakemeuphere

import android.media.SoundPool
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Alarm : Serializable {
    lateinit var title: String
    var minDistance: Int = 0
    var definitiveDistance: Int = 0
    lateinit var alarmSound: SoundPool
    var position: LatLng

    constructor(
        position: LatLng
    ) {
        this.position = position
    }
}