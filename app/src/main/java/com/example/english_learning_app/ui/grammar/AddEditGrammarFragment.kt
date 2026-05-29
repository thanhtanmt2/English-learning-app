package com.example.english_learning_app.ui.grammar

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class AddEditGrammarFragment : Fragment(R.layout.fragment_add_edit_grammar) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_add_edit_grammar_to_list).setOnClickListener {
            findNavController().navigate(R.id.action_addEditGrammar_to_grammarList)
        }
    }
}
