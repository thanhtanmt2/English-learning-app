package com.example.english_learning_app.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.english_learning_app.R

class RegisterFragment : Fragment(R.layout.fragment_register) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_register_to_login).setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        view.findViewById<Button>(R.id.btn_register_to_home).setOnClickListener {
            findNavController().navigate(R.id.action_register_to_home)
        }
    }
}
