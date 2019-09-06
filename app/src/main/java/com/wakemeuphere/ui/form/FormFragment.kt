package com.wakemeuphere.ui.form

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
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.Circle
import com.wakemeuphere.MapsActivity
import com.wakemeuphere.MyTextWatcher
import com.wakemeuphere.R
import com.wakemeuphere.internal.AppMemoryManager
import com.wakemeuphere.internal.songs.SongManager
import java.lang.Double
import java.lang.Exception

class FormFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = FormFragment()
    }

    private lateinit var viewModel: FormViewModel
    private lateinit var etTitle : EditText
    private lateinit var etDistance : TextView
    private lateinit var etDistanceValue : SeekBar
    private lateinit var spMusic : Spinner
    private lateinit var tvPosition: TextView

    private lateinit var player: MediaPlayer
    private lateinit var btnDoMusic: Button
    private lateinit var formView: View

    private lateinit var callback: OnCircleChangedListener

    fun setOnCircleChanged(callback: OnCircleChangedListener) {
        this.callback = callback
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnCircleChangedListener {
        fun circleValue(radius: kotlin.Double)
    }


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
        etDistance.setText(AppMemoryManager.alarmSelected.minDistance.toString())
        var song = SongManager.getSongById(AppMemoryManager.alarmSelected.soundId)

        etDistanceValue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(p0: SeekBar?) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                activity?.baseContext?.
                etDistance.text = "$i"
                callback.circleValue(i.toDouble())
                AppMemoryManager.alarmSelected.minDistance = i
            }

        })

//

        player = MediaPlayer()
        player.isLooping = false
        player.setOnCompletionListener { btnDoMusic.text = "Play" }
        setSong(song.uri)
        spMusic.setSelection(SongManager.songs.indexOf(song))

        tvPosition.text = AppMemoryManager.alarmSelected.latitude.toString() + " - " + AppMemoryManager.alarmSelected.longitude

        etTitle.addTextChangedListener(MyTextWatcher(lbTitle))
        etDistance.addTextChangedListener(MyTextWatcher(lbDistance))
        etDistanceValue.progress = AppMemoryManager.alarmSelected.minDistance
        //spMusic.addTextChangedListener(MyTextWatcher(lbMusic))

        return formView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FormViewModel::class.java)
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
        activity?.supportFragmentManager?.popBackStack()
//        onBackPressed()
    }

    fun onButtonSaveClicked(view: View) {

        AppMemoryManager.alarmSelected.title = etTitle.text.toString()
        AppMemoryManager.alarmSelected.minDistance = etDistance.text.toString().toInt()
        AppMemoryManager.alarmSelected.soundId = SongManager.songs[spMusic.selectedItemPosition].id//TODO create some verification
        AppMemoryManager.save()

//        onBackPressed()
    }

    fun onButtonDeleteClicked(view: View) {

        //https://medium.com/@suragch/making-an-alertdialog-in-android-2045381e2edb
        val builder = AlertDialog.Builder(activity!!.baseContext)


        builder.setTitle("Remove alert")//TODO use resource
        builder.setMessage("Are you want to remove this alert?")//TODO use resource

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES"){dialog, which ->
            AppMemoryManager.deleteSelectedAlarm()
            Toast.makeText(activity!!.baseContext, "Alarm deleted!", Toast.LENGTH_SHORT).show()
//            onBackPressed()
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
