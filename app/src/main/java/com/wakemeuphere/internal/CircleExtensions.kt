package com.wakemeuphere.internal

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.Circle
import com.wakemeuphere.R

fun Circle.asActive(resources: Resources) {
    this.strokeColor = ResourcesCompat.getColor(resources, R.color.strokeColorActive, null)
    this.fillColor = ResourcesCompat.getColor(resources, R.color.fillColorActive, null)
}

fun Circle.asSelected(resources: Resources) {
    this.strokeColor = ResourcesCompat.getColor(resources, R.color.strokeColorSelected, null)
    this.fillColor = ResourcesCompat.getColor(resources, R.color.fillColorSelected, null)
}

fun Circle.asInactive(resources: Resources) {
    this.strokeColor = ResourcesCompat.getColor(resources, R.color.strokeColorInactive, null)
    this.fillColor = ResourcesCompat.getColor(resources, R.color.fillColorInactive, null)
}