package com.example.english_learning_app.ui.vocabulary

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class AddEditWordFragment : Fragment(R.layout.fragment_add_edit_word) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_add_edit_word_to_word_list).setOnClickListener {
            findNavController().navigate(R.id.action_addEditWord_to_wordList)
        }
    }
}
