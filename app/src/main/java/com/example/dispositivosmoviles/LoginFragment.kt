package com.example.dispositivosmoviles

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import layout.com.example.dispositivosmoviles.Administrator

class LoginFragment : Fragment() {
    private lateinit var root: ViewGroup

    private lateinit var buttonCreateAccount: Button
    private lateinit var buttonLogin: Button
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
        root = inflater.inflate(R.layout.fragment_login, container, false) as ViewGroup

        editTextEmail = root.findViewById(R.id.editTextEmail)
        editTextPassword = root.findViewById(R.id.editTextPassword)

        buttonLogin = root.findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener { login() }

        buttonCreateAccount = root.findViewById(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener { goToCreateAccount() }

        return root
    }

    private fun login() {
        if (editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty()) {
            auth.signInWithEmailAndPassword(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            ).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    val admin = Firebase.database.getReference("/administrators").child(auth.currentUser!!.uid).get()
                    admin
                        .addOnSuccessListener { data ->
                            if (data.value != null) {
                                goToAdmin()
                            } else {
                                goToShoppingCart()
                            }
                        }
                        .addOnFailureListener{
                            goToShoppingCart()
                        }
                } else {
                    Toast.makeText(activity, "Error: Intente nuevamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToAdmin() {
        val action = LoginFragmentDirections.actionLoginFragmentToAdminFragment()
        root.findNavController().navigate(action)
    }

    private fun goToShoppingCart() {
        val action = LoginFragmentDirections.actionLoginFragmentToShoppingCartFragment()
        root.findNavController().navigate(action)
    }

    private fun goToCreateAccount() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
        root.findNavController().navigate(action)
    }
}