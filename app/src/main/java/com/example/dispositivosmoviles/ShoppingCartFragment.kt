package com.example.dispositivosmoviles

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import layout.com.example.dispositivosmoviles.ProductAdapter
import layout.com.example.dispositivosmoviles.Constants
import kotlin.math.roundToInt

private const val PARSED_PRODUCTS = "parsedProducts"
private const val PURCHASE_MADE = "purchaseMade"

class ShoppingCartFragment : Fragment() {
    private lateinit var root: ViewGroup
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var adminRef: DatabaseReference
    private lateinit var adminName: String
    private var adminStatus: Boolean = false
    lateinit var recyclerView: RecyclerView
    private lateinit var logoutButton: ImageView
    private lateinit var checkoutButton: Button
    lateinit var adapter: ProductAdapter
    var total: Float = 0.0f
    lateinit var totalCostTextView: TextView
    lateinit var adminNameTextView: TextView
    private var parsedProducts: String? = null
    private var purchaseMade: Boolean = false
    private var products: List<Product>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parsedProducts = it.getString(PARSED_PRODUCTS).toString()
            purchaseMade = it.getBoolean(PURCHASE_MADE)
        }

        val typeToken = object : TypeToken<List<Product>>() {}.type
        products = Gson().fromJson(parsedProducts, typeToken)

        if (products != null) {
            for (product in products!!) {
                Log.d(TAG, "debug $product")
                total += (product.price * product.quantity).toFloat()
            }
        }

        total = ((total * 100).roundToInt() / 100).toFloat()

        auth = Firebase.auth
        db = Firebase.firestore
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_shopping_cart, container, false) as ViewGroup

        totalCostTextView = root.findViewById(R.id.totalCostTextView)
        totalCostTextView.text = "$${total}"

        initRecyclerView(root)

        val scanButton = root.findViewById<Button>(R.id.scanButton)
        scanButton.setOnClickListener { initScanner() }

        val weightProducts = root.findViewById<Button>(R.id.weightProducts)
        weightProducts.setOnClickListener { goToWeightProducts() }

        checkoutButton = root.findViewById(R.id.checkoutButton)
        checkoutButton.setOnClickListener { goToCheckout(makeTicket()) }

        logoutButton = root.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener { logout() }

        adminNameTextView = root.findViewById(R.id.adminNameTextView)

        if (purchaseMade) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Su compra fue exitosa!")
                .setMessage("Gracias por su compra, tenga un buen día.")
                .show()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) {
            goToLogin()
            return
        } else if (auth.currentUser!!.uid == Constants.ADMIN_KEY) {
            goToAdmin()
            return
        }

        databaseSetup()
    }

    private fun databaseSetup() {
        val database = Firebase.database.getReference("/administrators")
        adminRef = database.child(Constants.ADMIN_KEY)

        val adminListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value as HashMap<*, *>
                adminStatus = data["isActive"] as Boolean
                updateCheckoutButtonStatus()

                adminName = data["name"] as String
                updateAdminName()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        adminRef.addValueEventListener(adminListener)
    }

    private fun updateCheckoutButtonStatus() {
        checkoutButton.isEnabled = adminStatus
        checkoutButton.isClickable = adminStatus
    }
    private fun updateAdminName() {
        adminNameTextView.text = if (adminStatus && adminName.isNotEmpty()) "Administrador: $adminName" else "Admin desconectado"
    }

    private fun initRecyclerView(root: ViewGroup) {
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ProductAdapter(products ?: listOf())


        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun initScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea el código de barras")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
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
        } catch(e: Exception) {
            Toast.makeText(activity, "Error en el escaneo. Compruebe que sea válido el código", Toast.LENGTH_SHORT).show()
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
            null,
            (data["price"] as Number).toDouble(),
            quantity = 1.0
        )
    }

    @SuppressLint("SetTextI18n")
    private fun addProductToList(product: Product) {
        total += product.price.toFloat()
        total = ((total * 100).roundToInt() / 100).toFloat()
        totalCostTextView.text = "$$total"

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

    private fun makeTicket(): String? {
        val gson = Gson()
        return gson.toJson(adapter.products)
    }

    private fun logout() {
        Firebase.auth.signOut()
        goToLogin()
    }

    private fun goToWeightProducts() {
        val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToWeightProductsFragment(makeTicket())
        root.findNavController().navigate(action)
    }

    private fun goToCheckout(ticket: String?) {
        if (ticket == null || adapter.products.isEmpty()) {
            return
        }

        val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToCheckoutFragment(ticket)
        root.findNavController().navigate(action)
    }

    private fun goToLogin() {
        val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToLoginFragment()
        root.findNavController().navigate(action)
    }

    private fun goToAdmin() {
        val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToAdminFragment()
        root.findNavController().navigate(action)
    }
}