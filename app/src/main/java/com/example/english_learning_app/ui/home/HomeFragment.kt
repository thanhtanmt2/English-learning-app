package com.example.english_learning_app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_home_to_word_set_list).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_wordSetList)
        }

        view.findViewById<Button>(R.id.btn_home_to_flashcard).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_flashcard)
        }

        view.findViewById<Button>(R.id.btn_home_to_dictation).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_dictation)
        }

        view.findViewById<Button>(R.id.btn_home_to_grammar_list).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_grammarList)
        }

        view.findViewById<Button>(R.id.btn_home_to_progress).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_progress)
        }
    }
}
