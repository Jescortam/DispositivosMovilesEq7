package com.example.dispositivosmoviles

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

private const val PARSED_TICKET = "parsedTicket"

class CheckoutFragment(): Fragment(), CoroutineScope, CreditCardClickListener {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var root: ViewGroup
    lateinit var totalCostTextView: TextView
    lateinit var parsedTicket: String
    lateinit var ticket: Ticket
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var total = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parsedTicket = it.getString(PARSED_TICKET).toString()
        }

        auth = Firebase.auth
        db = Firebase.firestore

        val gson = Gson()
        val sType = object: TypeToken<List<Product>>() {}.type
        val ticketProducts = gson.fromJson<List<Product>>(parsedTicket, sType)

        ticket = Ticket(auth.currentUser!!.uid, ticketProducts)

        for (product in ticket.products) {
            total += product.price.toFloat()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_checkout, container, false) as ViewGroup
        totalCostTextView = root.findViewById(R.id.totalCostTextView)


        db.collection("creditCards")
            .whereEqualTo("userId", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                val creditCards = mutableListOf<CreditCard>()
                for (document in documents) {
                    val data = document.data
                    val creditCard = CreditCard(
                        data["userId"].toString(),
                        data["name"].toString(),
                        data["lastDigits"].toString(),
                        data["type"].toString(),
                        data["expirationDate"].toString())

                    creditCards.add(creditCard)
                }

                val recyclerView = root.findViewById<RecyclerView>(R.id.creditCardRecyclerView)
                val adapter = CreditCardAdapter(creditCards, this)

                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = adapter
            }

        val buttonAddSampleCard = root.findViewById<Button>(R.id.buttonAddSampleCard)
        buttonAddSampleCard.setOnClickListener { launch { addSampleCard() } }

        val buttonAddCreditCard = root.findViewById<Button>(R.id.buttonAddCreditCard)
        buttonAddCreditCard.setOnClickListener { goToAddCreditCard() }

        val buttonGoBack = root.findViewById<Button>(R.id.buttonGoBack)
        buttonGoBack.setOnClickListener { goBack(false) }

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        totalCostTextView.text = "$${total.toString()}"
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun addSampleCard() {
//        val gson = Gson()

        try {
            val creditCardService = CreditCardService.create()
            val creditCards = creditCardService.getCreditCards()
            val creditCard = creditCards[0]

            val userService = UserService.create()
            val userResponse = userService.getUser()
            val user = userResponse.results[0]
            val username = user.name.first + " " + user.name.last

            val sampleCreditCard = CreditCard(
                auth.currentUser!!.uid,
                username,
                creditCard.card.takeLast(4),
                creditCard.name,
                creditCard.expiration_date
            )

            val db = Firebase.firestore
            db.collection("creditCards").document(UUID.randomUUID().toString())
                .set(sampleCreditCard)
                .addOnSuccessListener {
                    db.collection("creditCards")
                        .whereEqualTo("userId", auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            val creditCards = mutableListOf<CreditCard>()
                            for (document in documents) {
                                val data = document.data
                                val creditCard = CreditCard(
                                    data["userId"].toString(),
                                    data["name"].toString(),
                                    data["lastDigits"].toString(),
                                    data["type"].toString(),
                                    data["expirationDate"].toString()
                                )

                                creditCards.add(creditCard)
                            }

                            val recyclerView =
                                root.findViewById<RecyclerView>(R.id.creditCardRecyclerView)
                            val adapter = CreditCardAdapter(creditCards, this)

                            recyclerView.layoutManager = LinearLayoutManager(activity)
                            recyclerView.adapter = adapter
                        }
                }
        } catch(e: Exception) {
            addSampleCard()
        }
    }

    override fun onCreditCardClick() {
        goBack(true)
    }

    private fun goToAddCreditCard() {
        val action =
            CheckoutFragmentDirections.actionCheckoutFragmentToAddCreditCardFragment(parsedTicket)
        root.findNavController().navigate(action)
    }

    private fun goBack(purchaseMade: Boolean) {
        val action = CheckoutFragmentDirections.actionCheckoutFragmentToShoppingCartFragment(
            parsedProducts = null, purchaseMade = purchaseMade)
        root.findNavController().navigate(action)
    }
}