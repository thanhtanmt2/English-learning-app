package com.example.english_learning_app.ui.grammar

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class GrammarListFragment : Fragment(R.layout.fragment_grammar_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_grammar_list_to_add_edit).setOnClickListener {
            findNavController().navigate(R.id.action_grammarList_to_addEditGrammar)
        }

        view.findViewById<Button>(R.id.btn_grammar_list_to_quiz).setOnClickListener {
            findNavController().navigate(R.id.action_grammarList_to_grammarQuiz)
        }
    }
}
