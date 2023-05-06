package com.example.dispositivosmoviles

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
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

class ShoppingCartFragment : Fragment() {
    private lateinit var root: ViewGroup
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var recyclerView: RecyclerView
    private lateinit var logoutButton: ImageView
    private lateinit var checkoutButton: Button
    lateinit var adapter: ProductAdapter
    var total: Float = 0.0f
    lateinit var totalCostTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_shopping_cart, container, false) as ViewGroup

        totalCostTextView = root.findViewById(R.id.totalCostTextView)

        initRecyclerView(root)
        initCheckoutButton(root)
        initScanButton(root)

        logoutButton = root.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener { logout() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) {
            goToLogin()
        }
    }

    private fun initRecyclerView(root: ViewGroup) {
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ProductAdapter(arrayOf())

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun initCheckoutButton(root: ViewGroup) {
        val checkoutModalFragment = CheckoutModalFragment()
        checkoutButton = root.findViewById(R.id.checkoutButton)

        checkoutButton.setOnClickListener {
            checkoutModalFragment.total = total
            checkoutModalFragment.show(requireActivity().supportFragmentManager, "CheckoutModalFragment")
        }
    }

    private fun initScanButton(root: ViewGroup) {
        val scanButton = root.findViewById<Button>(R.id.scanButton)
        // CAMBIAR ESTO
        scanButton.setOnClickListener { getProduct("UCJkoyzRpdnKdthEIqcl") }
    }

    private fun initScanner() {
        val integrator = IntentIntegrator(activity)
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
                Toast.makeText(activity, "Operación cancelada", Toast.LENGTH_SHORT).show()
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
                Log.d(ContentValues.TAG, "FOUND")
                Log.d(ContentValues.TAG, result.data.toString())
                val product = parseHashMap(code, result.data as HashMap<*, *>)
                addProductToList(product)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting document", exception)
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

    private fun goToLogin() {
        val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToLoginFragment()
        root.findNavController().navigate(action)
    }

    private fun logout() {
        Firebase.auth.signOut()

        goToLogin()
    }
}