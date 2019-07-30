package com.example.secondemptytest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.secondemptytest.ui.framentmodal.FramentModalFragment

class framentModal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frament_modal_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FramentModalFragment.newInstance())
                .commitNow()
        }
    }

}
