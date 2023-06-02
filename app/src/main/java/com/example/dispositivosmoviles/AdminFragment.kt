package com.example.dispositivosmoviles

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AdminFragment : Fragment() {
    private lateinit var root: ViewGroup
    lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var adminRef: DatabaseReference
    private var adminStatus: Boolean = false
    private lateinit var adminName: String
    private lateinit var logoutButton: ImageView
    private lateinit var activarCuentaButton: Button
    private lateinit var adminStatusTextView: TextView
    private lateinit var adminNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_admin, container, false) as ViewGroup

        adminStatusTextView = root.findViewById(R.id.adminStatusTextView)

        adminNameTextView = root.findViewById(R.id.adminNameTextView)

        activarCuentaButton = root.findViewById(R.id.activarCuentaButton)
        activarCuentaButton.setOnClickListener { toggleActiveStatus() }

        logoutButton = root.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener { logout() }

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adminDatabase = database.child("/administrators")
        adminRef = adminDatabase.child(auth.currentUser!!.uid)
        adminRef
            .get()
            .addOnSuccessListener {
                val data = it.value as HashMap<*, *>
                adminStatus = data["isActive"] as Boolean
                adminName = data["name"] as String
                adminNameTextView.text = adminName
                updateAdminStatusInLayout()
            }
    }

    private fun toggleActiveStatus() {
        adminStatus = !adminStatus

        adminRef.child("isActive").setValue(adminStatus).addOnSuccessListener {
            updateAdminStatusInLayout()
        }
    }

    private fun updateAdminStatusInLayout() {
        adminStatusTextView.text = if (adminStatus) "Conectado" else "Desconectado"
        activarCuentaButton.text = if (adminStatus) "Desactivar cuenta" else "Activar cuenta"
    }

    private fun logout() {
        Firebase.auth.signOut()

        goToLogin()
    }

    private fun goToLogin() {
        val action = AdminFragmentDirections.actionAdminFragmentToLoginFragment()
        root.findNavController().navigate(action)
    }
}