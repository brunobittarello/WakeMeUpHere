package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

//https://www.viralandroid.com/2016/01/simple-android-user-contact-form-xml-ui-design.html
class AlarmForm : AppCompatActivity() {

    lateinit var etName : EditText
    lateinit var etDistance : EditText
    lateinit var etMusic : EditText
    lateinit var tvPosition: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_form)

        etName = findViewById(R.id.alarm_name)
        etDistance = findViewById(R.id.alarm_distance)
        etMusic = findViewById(R.id.alarm_music)
        tvPosition = findViewById(R.id.alarm_position)

        etName.setText("Teste")
        etDistance.setText("500")
        etMusic.setText("Lady Gaga - Comendo o cu do Matheus")
        tvPosition.setText("69.00 11.24")
    }

    fun OnButtonCancelClicked(view: View) {
        onBackPressed()
    }
}
