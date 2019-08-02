package com.wakemeuphere

import android.media.SoundPool
import com.google.android.gms.maps.model.LatLng

class Alarme {
    var title: String = ""
    var minDistance: Int = 0
    var definitiveDistance: Int = 0
    var alarmSound: SoundPool? = null
    var position: LatLng? = null


}