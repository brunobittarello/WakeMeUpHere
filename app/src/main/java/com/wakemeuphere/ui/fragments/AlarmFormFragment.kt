package com.wakemeuphere.ui.fragments

import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.wakemeuphere.R
import com.wakemeuphere.internal.AppMemoryManager
import com.wakemeuphere.internal.Utils
import com.wakemeuphere.internal.songs.SongManager
import java.lang.Exception

class AlarmFormFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = AlarmFormFragment()
    }

    private lateinit var viewModel: AlarmFormViewModel
    private lateinit var etTitle : EditText
    private lateinit var etDistance : TextView
    private lateinit var etDistanceValue : SeekBar
    private lateinit var spMusic : Spinner
    private lateinit var tvPosition: TextView

    private lateinit var player: MediaPlayer
    private lateinit var btnDoMusic: Button
    private lateinit var formView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        formView = inflater.inflate(R.layout.form_fragment, container, false)

        if (AppMemoryManager.alarmSelected == null)
        {
//            onBackPressed()
//            return
        }

        //Get Labels
        var lbTitle = formView.findViewById<TextView>(R.id.alarm_title_label)
        var lbDistance = formView.findViewById<TextView>(R.id.alarm_distance_label)
        //var lbMusic = findViewById<TextView>(R.id.alarm_music_label)

        //Get Edit Texts
        etTitle = formView.findViewById(R.id.alarm_title)
        etDistance = formView.findViewById(R.id.alarm_distance)
        spMusic = formView.findViewById(R.id.alarm_music)
        tvPosition = formView.findViewById(R.id.alarm_position)
        btnDoMusic = formView.findViewById(R.id.alarm_button_music)
        etDistanceValue = formView.findViewById(R.id.alarm_distance_value)

        var adapter = ArrayAdapter(activity!!.baseContext, R.layout.support_simple_spinner_dropdown_item, SongManager.songs)
        spMusic.adapter = adapter
        spMusic.onItemSelectedListener = this

        //SetTexts
        etTitle.setText(AppMemoryManager.alarmSelected.title)
        etDistance.setText(AppMemoryManager.alarmSelected.distance.toString())
        var song = SongManager.getSongById(AppMemoryManager.alarmSelected.soundId)

        //etDistanceValue.max = R.integer.alarm_distance_max - R.integer.alarm_distance_min /
        etDistanceValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(p0: SeekBar?) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                activity?.baseContext?.
                val dist = Utils.distanceInRange(i)
                etDistance.text = "$dist"
                AppMemoryManager.alarmSelected.circle.radius = dist.toDouble()
            }

        })

//
        btnDoMusic.setOnClickListener { view -> onButtonDoMusic(view) }
        player = MediaPlayer()
        player.isLooping = false
        player.setOnCompletionListener { btnDoMusic.text = "Play" }
        setSong(song.uri)
        spMusic.setSelection(SongManager.songs.indexOf(song))

        tvPosition.text = AppMemoryManager.alarmSelected.latitude.toString() + " - " + AppMemoryManager.alarmSelected.longitude

        etTitle.addTextChangedListener(MyTextWatcher(lbTitle))
        etDistance.addTextChangedListener(MyTextWatcher(lbDistance))
        etDistanceValue.progress = AppMemoryManager.alarmSelected.distance
        //spMusic.addTextChangedListener(MyTextWatcher(lbMusic))

        return formView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AlarmFormViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun setSong(uri: Uri)
    {
        try {
            player.reset()
            player.setDataSource(activity!!.baseContext, uri)
            player.prepare()
        } catch (e: Exception) {
            Log.w("AlarmForm", "Error $e")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    fun onButtonDoMusic(view: View) {
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

    fun saveForm(view: View) {

        AppMemoryManager.alarmSelected.title = etTitle.text.toString()
        AppMemoryManager.alarmSelected.distance = etDistance.text.toString().toInt()
        AppMemoryManager.alarmSelected.soundId = SongManager.songs[spMusic.selectedItemPosition].id//TODO create some verification
        AppMemoryManager.save()

//        onBackPressed()
    }

    fun deleteAlarm() {
        AppMemoryManager.deleteSelectedAlarm()
    }

    //REGION OnItemSelectedListener
    override fun onItemSelected(p0: AdapterView<*>?, view: View?, index: Int, index2: Long) {
        btnDoMusic.text = "Play"
        setSong(SongManager.songs[spMusic.selectedItemPosition].uri)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { }
    //END REGION OnItemSelectedListener



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

}
