package layout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dispositivosmoviles.Product
import com.example.dispositivosmoviles.R

class ProductAdapter(var products: Array<Product>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    val image = R.drawable.logo

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productImage: ImageView
        var productName: TextView
        var productPrice: TextView
        var productQuantity: TextView

        init {
            productImage = view.findViewById(R.id.productImageView)
            productName = view.findViewById(R.id.productNameTextView)
            productPrice = view.findViewById(R.id.productPriceTextView)
            productQuantity = view.findViewById(R.id.productQuantityTextView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.productImage.setImageResource(image)
        viewHolder.productName.text = products[position].name
        viewHolder.productPrice.text = products[position].price.toString()
        viewHolder.productQuantity.text = products[position].quantity.toString()
    }

    override fun getItemCount() = products.size

}
