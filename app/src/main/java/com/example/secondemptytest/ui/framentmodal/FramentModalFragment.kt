package com.example.secondemptytest.ui.framentmodal

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.secondemptytest.R

class FramentModalFragment : Fragment() {

    companion object {
        fun newInstance() = FramentModalFragment()
    }

    private lateinit var viewModel: FramentModalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.frament_modal_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FramentModalViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
