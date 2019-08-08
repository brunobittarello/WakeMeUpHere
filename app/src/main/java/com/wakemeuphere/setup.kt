package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wakemeuphere.ui.setup.SetupFragment

class setup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SetupFragment.newInstance())
                .commitNow()
        }
    }

}
