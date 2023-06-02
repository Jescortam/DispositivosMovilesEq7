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
import android.widget.RadioButton
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

private const val PARSED_TICKET = "parsedTicket"

class AddCreditCardFragment : Fragment() {
    lateinit var root: ViewGroup
    lateinit var auth: FirebaseAuth
    lateinit var parsedTicket: String
    lateinit var editTextNumber: EditText
    lateinit var editTextName: EditText
    lateinit var editTextExpirationMonth: EditText
    lateinit var editTextExpirationYear: EditText
    lateinit var radioButtonVisa: RadioButton
    lateinit var radioButtonMasterCard: RadioButton
    lateinit var buttonGoBack: Button
    lateinit var buttonAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parsedTicket = it.getString(PARSED_TICKET).toString()
        }

        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_add_credit_card, container, false) as ViewGroup

        editTextNumber = root.findViewById(R.id.editTextNumber)
        editTextName = root.findViewById(R.id.editTextName)
        editTextExpirationMonth = root.findViewById(R.id.editTextExpirationMonth)
        editTextExpirationYear = root.findViewById(R.id.editTextExpirationYear)
        radioButtonVisa = root.findViewById(R.id.radioButtonVisa)
        radioButtonMasterCard = root.findViewById(R.id.radioButtonMasterCard)

        buttonGoBack = root.findViewById(R.id.buttonGoBack)
        buttonGoBack.setOnClickListener { goBack() }

        buttonAdd = root.findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener { addCreditCard() }

        return root
    }

    private fun goBack() {
        val action = AddCreditCardFragmentDirections.actionAddCreditCardFragmentToCheckoutFragment(parsedTicket)
        root.findNavController().navigate(action)
    }

    private fun addCreditCard() {
        val id = UUID.randomUUID().toString()

        Log.d(TAG, editTextExpirationMonth.text.toString())
        Log.d(TAG, editTextExpirationYear.text.toString())

        val number = editTextNumber.text.toString()
        val name = editTextName.text.toString()
        val expirationMonth = editTextExpirationMonth.text.toString().toIntOrNull()
        val expirationYear = editTextExpirationYear.text.toString().toIntOrNull()
        val isVisaChecked = radioButtonVisa.isChecked
        val isMasterCardChecked = radioButtonMasterCard.isChecked

        Log.d(TAG, expirationMonth.toString())
        Log.d(TAG, expirationYear.toString())

        if (number.isNotEmpty() && name.isNotEmpty() && expirationMonth != null && expirationYear != null && (isVisaChecked || isMasterCardChecked)) {
            val type = if (isVisaChecked) "VISA" else "MASTERCARD"
            val creditCard = CreditCard(auth.currentUser!!.uid, name, number.takeLast(4), type, "${expirationMonth}/${expirationYear}")

            val db = Firebase.firestore
            db.collection("creditCards").document(id).set(creditCard)
                .addOnSuccessListener {
                    goBack()
                }
        }
    }
}
