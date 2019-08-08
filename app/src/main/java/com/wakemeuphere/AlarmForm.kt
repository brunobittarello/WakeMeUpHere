package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

        //val alarm = intent.extras?.get("AlarmObject") as Alarm

        //Get Labels
        var lbTitle = findViewById<TextView>(R.id.alarm_title_label)
        var lbDistance = findViewById<TextView>(R.id.alarm_distance_label)
        var lbMusic = findViewById<TextView>(R.id.alarm_music_label)

        //Get Edit Texts
        etName = findViewById(R.id.alarm_title)
        etDistance = findViewById(R.id.alarm_distance)
        etMusic = findViewById(R.id.alarm_music)
        tvPosition = findViewById(R.id.alarm_position)

        //SetTexts
        etName.setText("Teste")
        etDistance.setText("500")
        etMusic.setText("Lady Gaga - Comendo o cu do Matheus")
        //tvPosition.text = alarm.position.toString()

        etName.addTextChangedListener(MyTextWatcher(lbTitle))
        etDistance.addTextChangedListener(MyTextWatcher(lbDistance))
        etMusic.addTextChangedListener(MyTextWatcher(lbMusic))
    }

    fun OnButtonCancelClicked(view: View) {
        onBackPressed()
    }
}

class MyTextWatcher : TextWatcher
{
    var label: TextView

    constructor(label: TextView)
    {
        this.label = label
    }

    override fun afterTextChanged(p0: Editable?) {
        Log.d("MyTextWatcher", "After")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("MyTextWatcher", "Before")
    }

    override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
        if (count == 0)
            label.visibility = View.INVISIBLE
        else
            label.visibility = View.VISIBLE
        Log.d("MyTextWatcher", "OnTextChanged")
    }

}