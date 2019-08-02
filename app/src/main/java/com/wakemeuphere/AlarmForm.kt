package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_alarm_form.*

class AlarmForm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_form)

        val alarm = intent.extras?.get("AlarmObject") as Alarm
        textView.text = alarm.position.toString()
        //getSerializableExtra("AlarmObject")
    }

    fun OnButtonCancelClicked(view: View) {
        onBackPressed()
    }
}
