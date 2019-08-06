package com.wakemeuphere.internal

//https://alexdunn.org/2018/01/30/android-kotlin-basics-static-classes/
//https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e

object AppMemoryManager {

    var alarms: MutableList<Alarm> = mutableListOf()
    var teste: String = ""

    init {

    }

    fun LoadMarkers()
    {
        var debugAlarm = Alarm()
        debugAlarm.id = 0
        debugAlarm.title = "LoadTeste"
        debugAlarm.latitude = -34.0
        debugAlarm.longitude = 151.0

        alarms.add(debugAlarm)

        debugAlarm = Alarm()
        debugAlarm.id = 0
        debugAlarm.title = "LoadTeste2"
        debugAlarm.latitude = -30.0
        debugAlarm.longitude = 146.0

        alarms.add(debugAlarm)
    }
}