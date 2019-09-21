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
import com.google.android.gms.maps.model.LatLng
import com.wakemeuphere.R
import com.wakemeuphere.internal.AppMemoryManager
import com.wakemeuphere.internal.Utils
import com.wakemeuphere.internal.songs.SongManager
import java.lang.Exception

class AlarmFormFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: AlarmFormViewModel
    private lateinit var etTitle : EditText
    private lateinit var etDistance : TextView
    private lateinit var swActive: Switch
    private lateinit var etDistanceValue : SeekBar
    private lateinit var spMusic : Spinner

    private lateinit var player: MediaPlayer
    private lateinit var btnDoMusic: Button
    private lateinit var formView: View
    private var isMakerMoved : Boolean = false
    var listener: ((i: Int)->Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        formView = inflater.inflate(R.layout.form_fragment, container, false)

        //Get Labels
        val lbTitle = formView.findViewById<TextView>(R.id.alarm_title_label)
        val lbDistance = formView.findViewById<TextView>(R.id.alarm_distance_label)
        //var lbMusic = findViewById<TextView>(R.id.alarm_music_label)

        //Get Edit Texts
        etTitle = formView.findViewById(R.id.alarm_title)
        etDistance = formView.findViewById(R.id.alarm_distance)
        swActive = formView.findViewById(R.id.alarm_active)
        spMusic = formView.findViewById(R.id.alarm_music)
        btnDoMusic = formView.findViewById(R.id.alarm_button_music)
        etDistanceValue = formView.findViewById(R.id.alarm_distance_value)

        val adapter = ArrayAdapter(activity!!.baseContext, R.layout.support_simple_spinner_dropdown_item, SongManager.songs)
        spMusic.adapter = adapter
        spMusic.onItemSelectedListener = this

        //SetTexts
        etTitle.setText(AppMemoryManager.alarmSelected.title)
        swActive.isChecked = AppMemoryManager.alarmSelected.active
        etDistance.setText(AppMemoryManager.alarmSelected.distance.toString())
        val song = SongManager.getSongById(AppMemoryManager.alarmSelected.soundId)

        //etDistanceValue.max = R.integer.alarm_distance_max - R.integer.alarm_distance_min /
        etDistanceValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar == null) return
                val dist = Utils.distanceInRange(seekBar.progress)
                listener?.invoke(dist)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                activity?.baseContext?.
                val dist = Utils.distanceInRange(i)
                etDistance.text = "$dist"
                AppMemoryManager.alarmSelected.circle.radius = dist.toDouble()
                //listener?.invoke(dist)
            }

        })

//
        btnDoMusic.setOnClickListener { view -> onButtonDoMusic(view) }
        player = MediaPlayer()
        player.isLooping = false
        player.setOnCompletionListener { btnDoMusic.text = "Play" }
        setSong(song.uri)
        spMusic.setSelection(SongManager.songs.indexOf(song))

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
        if (!isMakerMoved) return
        AppMemoryManager.alarmSelected.marker.position = AppMemoryManager.alarmSelected.point
        AppMemoryManager.alarmSelected.circle.center = AppMemoryManager.alarmSelected.marker.position
    }

    private fun onButtonDoMusic(view: View) {
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
        if (isMakerMoved)
            AppMemoryManager.alarmSelected.point = AppMemoryManager.alarmSelected.marker.position
        AppMemoryManager.alarmSelected.title = etTitle.text.toString()
        AppMemoryManager.alarmSelected.distance = etDistance.text.toString().toInt()
        AppMemoryManager.alarmSelected.active = swActive.isChecked
        AppMemoryManager.alarmSelected.soundId = SongManager.songs[spMusic.selectedItemPosition].id//TODO create some verification
        AppMemoryManager.save()
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

    fun onMarkerMoved() {
        isMakerMoved = true
        AppMemoryManager.alarmSelected.circle.center = AppMemoryManager.alarmSelected.marker.position
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

}
