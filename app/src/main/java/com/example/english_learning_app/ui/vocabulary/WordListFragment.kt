package com.example.english_learning_app.ui.vocabulary

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class WordListFragment : Fragment(R.layout.fragment_word_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_word_list_to_add_edit_word).setOnClickListener {
            findNavController().navigate(R.id.action_wordList_to_addEditWord)
        }
    }
}
