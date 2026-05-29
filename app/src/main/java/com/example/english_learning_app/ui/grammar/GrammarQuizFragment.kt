package com.example.english_learning_app.ui.grammar

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class GrammarQuizFragment : Fragment(R.layout.fragment_grammar_quiz) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_grammar_quiz_to_list).setOnClickListener {
            findNavController().navigate(R.id.action_grammarQuiz_to_grammarList)
        }
    }
}
