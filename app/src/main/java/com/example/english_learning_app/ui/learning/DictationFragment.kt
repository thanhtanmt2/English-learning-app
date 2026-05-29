package com.example.english_learning_app.ui.learning

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class DictationFragment : Fragment(R.layout.fragment_dictation) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_dictation_to_home).setOnClickListener {
            findNavController().navigate(R.id.action_dictation_to_home)
        }
    }
}
