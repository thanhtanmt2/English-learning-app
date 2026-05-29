package com.example.english_learning_app.ui.progress

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class ProgressFragment : Fragment(R.layout.fragment_progress) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_progress_to_home).setOnClickListener {
            findNavController().navigate(R.id.action_progress_to_home)
        }
    }
}
