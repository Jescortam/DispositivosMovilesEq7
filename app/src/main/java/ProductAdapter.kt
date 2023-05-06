package layout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.dispositivosmoviles.Product
import com.example.dispositivosmoviles.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProductAdapter(var products: Array<Product>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private lateinit var glideRef: RequestManager

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

        glideRef = Glide.with(view)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val storageRef = Firebase.storage.reference

        storageRef.child(products[position].image).downloadUrl.addOnSuccessListener { uri ->
            glideRef.load(uri).into(viewHolder.productImage)
        }

        viewHolder.productName.text = products[position].name
        viewHolder.productPrice.text = products[position].price.toString()
        viewHolder.productQuantity.text = products[position].quantity.toString()
    }

    override fun getItemCount() = products.size

}
