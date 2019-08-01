package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AlarmForm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_form)
    }

    fun OnButtonCancelClicked(view: View) {
        onBackPressed()
    }
}
