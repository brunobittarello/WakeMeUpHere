package com.wakemeuphere

import android.media.SoundPool
import com.google.android.gms.maps.model.LatLng

class Alarme {
    var title: String
    var minDistance: Int
    var definitiveDistance: Int
    var alarmSound: SoundPool
    var position: LatLng

    constructor(title: String, minDistance: Int, definitiveDistance: Int, alarmSound: SoundPool, position: LatLng) {
        this.title = title
        this.minDistance = minDistance
        this.definitiveDistance = definitiveDistance
        this.alarmSound = alarmSound
        this.position = position
    }
}