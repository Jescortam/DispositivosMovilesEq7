package layout.com.example.dispositivosmoviles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.dispositivosmoviles.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CheckoutModalFragment(): BottomSheetDialogFragment() {
    lateinit var root: ViewGroup
    lateinit var totalCostTextView: TextView
    var total = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.checkout_layout, container, false) as ViewGroup
        totalCostTextView = root.findViewById(R.id.totalCostTextView)

        return root
    }

    override fun onResume() {
        super.onResume()

        totalCostTextView.text = "$${total.toString()}"
    }
}