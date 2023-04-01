package com.example.dispositivosmoviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.zxing.integration.android.IntentIntegrator
import layout.ProductAdapter
import layout.com.example.dispositivosmoviles.CheckoutModalFragment
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
//    private lateinit var paymentsClient: PaymentsClient
//
//    private val allowedCardNetworks = JSONArray(listOf(
//        "AMEX",
//        "DISCOVER",
//        "INTERAC",
//        "JCB",
//        "MASTERCARD",
//        "VISA"))
//
//    private val allowedCardAuthMethods = JSONArray(
//        listOf(
//            "PAN_ONLY",
//            "CRYPTOGRAM_3DS"
//        )
//    )

    lateinit var recyclerView: RecyclerView
    lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ProductAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val checkoutModalFragment = CheckoutModalFragment()
        checkoutButton = findViewById(R.id.checkoutButton)

        checkoutButton.setOnClickListener {
            checkoutModalFragment.show(supportFragmentManager, "CheckoutModalFragment")
        }

        val scanButton = findViewById<Button>(R.id.scanButton)

        scanButton.setOnClickListener { initScanner() }

//        val walletOptions: Wallet.WalletOptions = Wallet.WalletOptions.Builder()
//            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
//            .build();
//
//        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);
//
//        val readyToPayRequest: IsReadyToPayRequest =
//            IsReadyToPayRequest.fromJson(baseConfigurationJson().toString())
//
//        val task: Task<Boolean> = paymentsClient.isReadyToPay(readyToPayRequest)
//        task.addOnCompleteListener(this, OnCompleteListener {
//            fun onComplete(completeTask: Task<Boolean>) {
//                if (completeTask.isSuccessful) {
//                    showGooglePayButton(completeTask.result)
//                } else {
//                    // Handle the error accordingly
//                }
//            }
//        })
//
//        val paymentRequestJson: JSONObject = baseConfigurationJson();
//        paymentRequestJson.put("transactionInfo", JSONObject()
//            .put("totalPrice", "123.45")
//            .put("totalPriceStatus", "FINAL")
//            .put("currencyCode", "MXN"));
//        paymentRequestJson.put("merchantInfo", JSONObject()
//            .put("merchantId", "01234567890123456789")
//            .put("merchantName", "Example Merchant"))
//
//        val request = PaymentDataRequest.fromJson(paymentRequestJson.toString())
//
//        AutoResolveHelper.resolveTask(
//            paymentsClient.loadPaymentData(request),
//            this, LOAD_PAYMENT_DATA_REQUEST_CODE
//        )
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
                Toast.makeText(
                    this,
                    "El valor escaneado es: ${result.contents}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

//    private fun baseConfigurationJson(): JSONObject {
//        return JSONObject()
//            .put("apiVersion", 2)
//            .put("apiVersionMinor", 0)
//            .put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod().put("tokenizationSpecification", gatewayTokenizationSpecification())))
//    }
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
//    private fun gatewayTokenizationSpecification(): JSONObject {
//        return JSONObject().apply {
//            put("type", "PAYMENT_GATEWAY")
//            put("parameters", JSONObject(mapOf(
//                "gateway" to "example",
//                "gatewayMerchantId" to "exampleGatewayMerchantId")))
//        }
//    }
//
//    private fun showGooglePayButton(userIsReadyToPay: Boolean) {
//        if (userIsReadyToPay) {
//            // Update your UI to show the Google Pay button
//            // eg.: googlePayButton.setVisibility(View.VISIBLE)
//        } else {
//            // Google Pay is not supported. Do not show the button.
//        }
//    }
}