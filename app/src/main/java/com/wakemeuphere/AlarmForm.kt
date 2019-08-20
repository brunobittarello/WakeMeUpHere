package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.wakemeuphere.internal.AppMemoryManager
import androidx.appcompat.app.AlertDialog
import com.wakemeuphere.internal.songs.SongManager

//https://www.viralandroid.com/2016/01/simple-android-user-contact-form-xml-ui-design.html

//About Spinner
//https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin
class AlarmForm : AppCompatActivity() {

    private lateinit var etTitle : EditText
    private lateinit var etDistance : EditText
    private lateinit var spMusic : Spinner
    private lateinit var tvPosition: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_form)

        if (AppMemoryManager.alarmSelected == null)
        {
            onBackPressed()
            return
        }

        //Get Labels
        var lbTitle = findViewById<TextView>(R.id.alarm_title_label)
        var lbDistance = findViewById<TextView>(R.id.alarm_distance_label)
        //var lbMusic = findViewById<TextView>(R.id.alarm_music_label)

        //Get Edit Texts
        etTitle = findViewById(R.id.alarm_title)
        etDistance = findViewById(R.id.alarm_distance)
        spMusic = findViewById(R.id.alarm_music)
        tvPosition = findViewById(R.id.alarm_position)

        //val listItems = mutableListOf<Song>()
        //listItems.add("teste")
        //listItems.add("teste2")
        var adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, SongManager.songs)
        spMusic.adapter = adapter

        //SetTexts
        etTitle.setText(AppMemoryManager.alarmSelected.title)
        etDistance.setText(AppMemoryManager.alarmSelected.minDistance.toString())
        spMusic.setSelection(SongManager.songs.indexOf(SongManager.getSongById(AppMemoryManager.alarmSelected.soundId)))
        tvPosition.text = AppMemoryManager.alarmSelected.latitude.toString() + " - " + AppMemoryManager.alarmSelected.longitude

        etTitle.addTextChangedListener(MyTextWatcher(lbTitle))
        etDistance.addTextChangedListener(MyTextWatcher(lbDistance))
        //spMusic.addTextChangedListener(MyTextWatcher(lbMusic))
    }

    fun onButtonCancelClicked(view: View) {
        onBackPressed()
    }

    fun onButtonSaveClicked(view: View) {

        AppMemoryManager.alarmSelected.title = etTitle.text.toString()
        AppMemoryManager.alarmSelected.minDistance = etDistance.text.toString().toInt()
        AppMemoryManager.alarmSelected.soundId = SongManager.songs[spMusic.selectedItemPosition].id//TODO create some verification
        AppMemoryManager.save()

        onBackPressed()
    }

    fun onButtonDeleteClicked(view: View) {

        //https://medium.com/@suragch/making-an-alertdialog-in-android-2045381e2edb
        val builder = AlertDialog.Builder(this)


        builder.setTitle("Remove alert")//TODO use resource
        builder.setMessage("Are you want to remove this alert?")//TODO use resource

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES"){dialog, which ->
            AppMemoryManager.deleteSelectedAlarm()
            Toast.makeText(this, "Alarm deleted!", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        // Display a negative button on alert dialog
        builder.setNegativeButton("No"){dialog,which ->
            //Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}

class MyTextWatcher : TextWatcher
{
    private var label: TextView

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