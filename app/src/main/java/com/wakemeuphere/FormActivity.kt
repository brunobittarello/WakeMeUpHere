package com.wakemeuphere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wakemeuphere.ui.form.FormFragment

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FormFragment.newInstance())
                .commitNow()
        }
    }

}
