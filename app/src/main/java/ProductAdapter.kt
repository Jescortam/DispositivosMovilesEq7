package layout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dispositivosmoviles.R

class ProductAdapter: RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    val images = intArrayOf(
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    val names = arrayOf(
        "Producto 1",
        "Producto 2",
        "Producto 3",
        "Producto 4",
    )

    val prices = arrayOf(
        "$9.99",
        "$14.99",
        "$49.99",
        "$1.99"
    )

    val quantities = arrayOf(
        "10",
        "4",
        "8",
        "6"
    )

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
        viewHolder.productImage.setImageResource(images[position])
        viewHolder.productName.text = names[position]
        viewHolder.productPrice.text = prices[position]
        viewHolder.productQuantity.text = quantities[position]

    }

    override fun getItemCount() = names.size

}
