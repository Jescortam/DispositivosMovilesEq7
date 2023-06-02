package com.example.dispositivosmoviles

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class FruitAdapter(private var fruitCardClickListener: FruitCardClickListener): RecyclerView.Adapter<FruitAdapter.ViewHolder>() {
    var images = listOf(R.drawable.manzana, R.drawable.platano, R.drawable.naranja, R.drawable.limon, R.drawable.tomate)
    var names = listOf("Manzanas", "Plátanos", "Naranjas", "Limones", "Tomates")
    var costsPerKilo = listOf(60.0, 12.0, 26.90, 55.0, 28.0)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var fruitImage: ImageView
        var fruitName: TextView
        var fruitPrice: TextView
        var fruitCardView: CardView

        init {
            fruitImage = view.findViewById(R.id.productImageView)
            fruitName = view.findViewById(R.id.productNameTextView)
            fruitPrice = view.findViewById(R.id.productPriceTextView)
            fruitCardView = view.findViewById(R.id.productView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_layout, viewGroup, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val cost= ((costsPerKilo[position] * 100).roundToInt() / 100).toDouble()

        viewHolder.fruitImage.setImageResource(images[position])
        viewHolder.fruitName.text = names[position]
        viewHolder.fruitPrice.text = "$${cost} por kilo"

        viewHolder.fruitCardView.setOnClickListener { fruitCardClickListener.onFruitCardClick(
            names[position],
            cost,
            images[position]
        ) }
    }

    override fun getItemCount() = names.size

}
