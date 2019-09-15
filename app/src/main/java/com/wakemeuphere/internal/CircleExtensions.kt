package com.wakemeuphere.internal

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.Circle
import com.wakemeuphere.R

fun Circle.asNormal(resources: Resources) {
    this.strokeColor = ResourcesCompat.getColor(resources, R.color.strokeColorGreen, null)
    this.fillColor = ResourcesCompat.getColor(resources, R.color.fillColorGreen, null)
}

fun Circle.asNew(resources: Resources) {
    this.strokeColor = ResourcesCompat.getColor(resources, R.color.strokeColorNew, null)
    this.fillColor = ResourcesCompat.getColor(resources, R.color.fillColorNew, null)
}