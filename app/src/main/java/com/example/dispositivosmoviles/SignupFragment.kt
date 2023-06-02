package com.example.dispositivosmoviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {
    private lateinit var root: ViewGroup

    private lateinit var buttonLogin: Button
    private lateinit var buttonSignup: Button
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_signup, container, false) as ViewGroup

        initForm(root)

        buttonLogin = root.findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener { goToLogin() }

        return root
    }

    private fun initForm(root: ViewGroup) {
        editTextEmail = root.findViewById(R.id.editTextEmail)
        editTextPassword = root.findViewById(R.id.editTextPassword)

        buttonSignup = root.findViewById(R.id.buttonSignup)
        buttonSignup.setOnClickListener { signup() }
    }

    private fun signup() {
        if (editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    goToShoppingCart()
                } else {
                    Toast.makeText(activity, "Error: Intente nuevamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToLogin() {
        val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
        root.findNavController().navigate(action)
    }

    private fun goToShoppingCart() {
        val action = SignupFragmentDirections.actionSignupFragmentToShoppingCartFragment(null, false)
        root.findNavController().navigate(action)
    }
}