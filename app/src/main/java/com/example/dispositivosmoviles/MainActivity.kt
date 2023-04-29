package com.example.dispositivosmoviles

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import layout.ProductAdapter
import layout.com.example.dispositivosmoviles.CheckoutModalFragment

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var recyclerView: RecyclerView
    private lateinit var checkoutButton: Button
    lateinit var adapter: ProductAdapter
    var total: Float = 0.0f
    lateinit var totalCostTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        db = Firebase.firestore

        totalCostTextView = findViewById(R.id.totalCostTextView)

        initRecyclerView()
        initCheckoutButton()
        initScanButton()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ProductAdapter(arrayOf())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initCheckoutButton() {
        val checkoutModalFragment = CheckoutModalFragment(total)
        checkoutButton = findViewById(R.id.checkoutButton)

        checkoutButton.setOnClickListener {
            checkoutModalFragment.show(supportFragmentManager, "CheckoutModalFragment")
        }
    }

    private fun initScanButton() {
        val scanButton = findViewById<Button>(R.id.scanButton)
        // CAMBIAR ESTO
        scanButton.setOnClickListener { getProduct("9300675009829") }
    }

    private fun initScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código de barras")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                getProduct(result.contents)
            } else {
                Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getProduct(code: String) {
        db.collection("products")
            .document(code)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "FOUND")
                Log.d(TAG, result.data.toString())
                val product = parseHashMap(code, result.data as HashMap<*, *>)
                addProductToList(product)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting document", exception)
            }
    }
    private fun parseHashMap(code: String, data: HashMap<*, *>): Product {
        return Product(
            code,
            data["name"] as String,
            data["image"] as String,
            data["price"] as Number,
            quantity = 1
        )
    }

    private fun addProductToList(product: Product) {
        total += product.price.toFloat()
        totalCostTextView.text = "$${total.toString()}"

        var index = 0
        for (adapterProduct in adapter.products) {
            if (adapterProduct.code == product.code) {
                adapterProduct.quantity++
                adapter.notifyItemChanged(index)
                return
            }

            index++
        }

        adapter.products += product
        adapter.notifyItemInserted(adapter.products.size - 1)
    }
}