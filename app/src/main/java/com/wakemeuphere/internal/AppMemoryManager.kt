package com.wakemeuphere.internal

import android.util.Log
import com.beust.klaxon.Klaxon
import android.content.SharedPreferences
import android.content.Context


//About Singletons
//https://alexdunn.org/2018/01/30/android-kotlin-basics-static-classes/
//https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e

//About Storage
//https://developer.android.com/guide/topics/data/data-storage
object AppMemoryManager {

    val PREFS_PROFILE = "MyProfile"
    val PREFS_ALARM = "MyAlarms"

    private lateinit var preferences: SharedPreferences

    var alarms: MutableList<Alarm> = mutableListOf()

    init {
    }

    fun addAlarm(alarm: Alarm)
    {
        alarms.add(alarm)
        save()
    }

    private fun save()
    {
        Log.w("AppMemoryManager", "SAVE")
        var edit = preferences.edit()
        var jsonAlarm = Klaxon().toJsonString(alarms)
        edit.putString(PREFS_ALARM, jsonAlarm)
        edit.commit()
        Log.w("AppMemoryManager", "SAVED")
    }

    fun load(context: Context)
    {
        Log.w("AppMemoryManager", "LOAD")
        preferences = context.getSharedPreferences(PREFS_PROFILE, 0)
        var jsonAlarms = preferences.getString(PREFS_ALARM, "")


        if (jsonAlarms == null || jsonAlarms == "")
            loadDebugPoints()
        else
        {
            var parsedAlarms = Klaxon().parseArray<Alarm>(jsonAlarms)
            if (parsedAlarms != null)
                alarms.addAll(parsedAlarms)
            else
                loadDebugPoints()
        }

        Log.w("AppMemoryManager", "LOADED")
    }


    private fun loadDebugPoints()
    {
        Log.w("AppMemoryManager", "loadDebugPoints")

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
        save()
    }
}