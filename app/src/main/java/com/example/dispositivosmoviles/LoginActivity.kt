package com.example.dispositivosmoviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var buttonCreateAccount: Button
    private lateinit var buttonLogin: Button
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        initForm()

        buttonCreateAccount = findViewById(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener { goToSignup() }
    }

    private fun initForm() {
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener { login() }
    }

    private fun login() {
        if (editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty()) {
            auth.signInWithEmailAndPassword(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error: Intente nuevamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToSignup() {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}