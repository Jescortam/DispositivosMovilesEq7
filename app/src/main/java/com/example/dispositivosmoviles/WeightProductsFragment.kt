package com.example.dispositivosmoviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PARSED_PRODUCTS = "parsedProducts"

class WeightProductsFragment : Fragment(), FruitCardClickListener {

    private lateinit var root: ViewGroup
    private var parsedProducts: String? = null
    private lateinit var editTextPeso: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            parsedProducts = it.getString(PARSED_PRODUCTS).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_weight_products, container, false) as ViewGroup

        editTextPeso = root.findViewById<EditText>(R.id.editTextPeso)

        val buttonRegresar = root.findViewById<Button>(R.id.buttonGoBack)
        buttonRegresar.setOnClickListener { goToShoppingCart() }

        val buttonBluetooth = root.findViewById<Button>(R.id.buttonUsarBluetooth)
        buttonBluetooth.setOnClickListener { goToBluetooth() }

        val recyclerView = root.findViewById<RecyclerView>(R.id.fruitsRecyclerView)
        val adapter = FruitAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        return root
    }

    override fun onFruitCardClick(fruitName: String, fruitCostPerKilo: Double, fruitImage: Int) {
        var weight = editTextPeso.text.toString().toDoubleOrNull()
        if (weight != null) {
            weight /= 1000
            val fruit = Product("", fruitName, null, fruitImage, fruitCostPerKilo, weight)
            addProduct(fruit)
        }
    }

    private fun addProduct(fruit: Product) {


        val typeToken = object : TypeToken<List<Product>>() {}.type
        var products = Gson().fromJson<List<Product>>(parsedProducts, typeToken)

        products += fruit

        parsedProducts = Gson().toJson(products)

        goToShoppingCart()
    }

    private fun goToShoppingCart() {
        val action = WeightProductsFragmentDirections.actionWeightProductsFragmentToShoppingCartFragment(
            parsedProducts, false)
        root.findNavController().navigate(action)
    }

    private fun goToBluetooth() {
        val action = WeightProductsFragmentDirections.actionWeightProductsFragmentToBluetoothFragment(parsedProducts)
        root.findNavController().navigate(action)
    }
}