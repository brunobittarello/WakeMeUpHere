package com.wakemeuphere.internal

import kotlin.math.max
import kotlin.math.min

object Utils {
    const val alarm_distance_min: Int = 10
    const val alarm_distance_max: Int = 10000

    fun distanceInRange(dist: Int) : Int {
        return max(min(dist, alarm_distance_max), alarm_distance_min)
    }
}