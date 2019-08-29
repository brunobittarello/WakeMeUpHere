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
import android.media.MediaPlayer
import android.net.Uri
import java.lang.Exception

//https://www.viralandroid.com/2016/01/simple-android-user-contact-form-xml-ui-design.html

//About Spinner
//https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin
class AlarmForm : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var etTitle : EditText
    private lateinit var etDistance : EditText
    private lateinit var spMusic : Spinner
    private lateinit var tvPosition: TextView

    private lateinit var player: MediaPlayer
    private lateinit var btnDoMusic: Button

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
        btnDoMusic = findViewById(R.id.alarm_button_music)

        var adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, SongManager.songs)
        spMusic.adapter = adapter
        spMusic.onItemSelectedListener = this

        //SetTexts
        etTitle.setText(AppMemoryManager.alarmSelected.title)
        etDistance.setText(AppMemoryManager.alarmSelected.minDistance.toString())
        var song = SongManager.getSongById(AppMemoryManager.alarmSelected.soundId)

        player = MediaPlayer()
        player.isLooping = false
        player.setOnCompletionListener { btnDoMusic.text = "Play" }
        setSong(song.uri)
        spMusic.setSelection(SongManager.songs.indexOf(song))

        tvPosition.text = AppMemoryManager.alarmSelected.latitude.toString() + " - " + AppMemoryManager.alarmSelected.longitude

        etTitle.addTextChangedListener(MyTextWatcher(lbTitle))
        etDistance.addTextChangedListener(MyTextWatcher(lbDistance))
        //spMusic.addTextChangedListener(MyTextWatcher(lbMusic))
    }

    private fun setSong(uri: Uri)
    {
        try {
            player.reset()
            player.setDataSource(this, uri)
            player.prepare()
        } catch (e: Exception) {
            Log.w("AlarmForm", "Error $e")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    fun onButtonDoMusic(view: View)
    {
        if (player.isPlaying)
        {
            btnDoMusic.text = "Play"
            player.pause()
        }
        else {
            btnDoMusic.text = "Stop"
            player.seekTo(0)
            player.start()
        }
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

    //OnItemSelectedListener
    override fun onItemSelected(p0: AdapterView<*>?, view: View?, index: Int, index2: Long) {
        btnDoMusic.text = "Play"
        setSong(SongManager.songs[spMusic.selectedItemPosition].uri)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { }

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