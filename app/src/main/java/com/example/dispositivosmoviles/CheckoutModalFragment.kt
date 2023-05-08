package layout.com.example.dispositivosmoviles

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.dispositivosmoviles.R
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stripe.android.PaymentConfiguration
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CheckoutModalFragment(): BottomSheetDialogFragment() {
    lateinit var root: ViewGroup
    lateinit var totalCostTextView: TextView
    private lateinit var clientSecret: String
    private lateinit var googlePayButton: Button
    private lateinit var googlePayLauncher: GooglePayLauncher
    var total = 0.0f


//    private val baseRequest = JSONObject().apply {
//        put("apiVersion", 2)
//        put("apiVersionMinor", 0)
//    }
//
//    private fun gatewayTokenizationSpecification(): JSONObject {
//        return JSONObject().apply {
//            put("type", "PAYMENT_GATEWAY")
//            put("parameters", JSONObject(mapOf(
//                "gateway" to "stripe",
//                "stripe:version" to "2018-10-31",
//                "gatewayMerchantId" to "pk_test_51N4vrQDikqiPKbujXD2UMkbIRU4VSP5HLrOVsNKeMau72Plg1TiU85113MvmPvH7IRW91swOqv5ujLCucXfmHxdP00TWrtGas3")))
//        }
//    }
//
//    private val allowedCardNetworks = JSONArray(listOf(
//        "AMEX",
//        "DISCOVER",
//        "INTERAC",
//        "JCB",
//        "MASTERCARD",
//        "VISA"))
//
//    private val allowedCardAuthMethods = JSONArray(listOf(
//        "PAN_ONLY",
//        "CRYPTOGRAM_3DS"))
//
//    private fun baseCardPaymentMethod(): JSONObject {
//        return JSONObject().apply {
//
//            val parameters = JSONObject().apply {
//                put("allowedAuthMethods", allowedCardAuthMethods)
//                put("allowedCardNetworks", allowedCardNetworks)
//                put("billingAddressRequired", true)
//                put("billingAddressParameters", JSONObject().apply {
//                    put("format", "FULL")
//                })
//            }
//
//            put("type", "CARD")
//            put("parameters", parameters)
//        }
//    }
//
//    private fun cardPaymentMethod(): JSONObject {
//        val cardPaymentMethod = baseCardPaymentMethod()
//        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())
//
//        return cardPaymentMethod
//    }
//
//    fun createPaymentsClient(activity: Activity): PaymentsClient {
//        val walletOptions = Wallet.WalletOptions.Builder()
//            .setEnvironment(Constants.PAYMENTS_ENVIRONMENT)
//            .build()
//
//        return Wallet.getPaymentsClient(activity, walletOptions)
//    }
//
//    fun isReadyToPayRequest(): JSONObject? {
//        return try {
//            baseRequest.apply {
//                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
//            }
//
//        } catch (e: JSONException) {
//            null
//        }
//    }
//
//    private fun getTransactionInfo(price: String): JSONObject {
//        return JSONObject().apply {
//            put("totalPrice", price)
//            put("totalPriceStatus", "FINAL")
//            put("countryCode", Constants.COUNTRY_CODE)
//            put("currencyCode", Constants.CURRENCY_CODE)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PaymentConfiguration.init(requireActivity(), Constants.PUBLISHABLE_KEY)

        googlePayLauncher = GooglePayLauncher(
            activity = requireActivity(),
            config = GooglePayLauncher.Config(
                environment = GooglePayEnvironment.Test,
                merchantCountryCode = "MX",
                merchantName = "Dispositivos Moviles"
            ),
            readyCallback = ::onGooglePayReady,
            resultCallback = ::onGooglePayResult
        )
    }

    private fun onGooglePayReady(isReady: Boolean) {
        googlePayButton.isEnabled = isReady
    }

    private fun onGooglePayResult(result: GooglePayLauncher.Result) {
        when (result) {
            GooglePayLauncher.Result.Completed -> {
                Log.d(TAG, "Completed")
            }
            GooglePayLauncher.Result.Canceled -> {
                Log.d(TAG, "Canceled")
            }
            is GooglePayLauncher.Result.Failed -> {
                Log.d(TAG, "Failed")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.checkout_layout, container, false) as ViewGroup
        totalCostTextView = root.findViewById(R.id.totalCostTextView)

        googlePayButton = root.findViewById(R.id.googlePayButton)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googlePayButton.setOnClickListener {
            googlePayLauncher.presentForPaymentIntent(Constants.CLIENT_SECRET)
        }
    }

    override fun onResume() {
        super.onResume()

        totalCostTextView.text = "$${total.toString()}"
    }
}