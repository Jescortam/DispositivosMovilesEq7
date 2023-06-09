package com.example.dispositivosmoviles

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CreditCardAdapter(var creditCards: List<CreditCard>, var creditCardClickListener: CreditCardClickListener): RecyclerView.Adapter<CreditCardAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var creditCardNumber: TextView
        var creditCardName: TextView
        var creditCardType: TextView
        var creditCardExpirationDate: TextView
        var creditCardView: CardView

        init {
            creditCardNumber = view.findViewById(R.id.creditCardNumberTextView)
            creditCardName = view.findViewById(R.id.creditCardNameTextView)
            creditCardType = view.findViewById(R.id.creditCardTypeTextView)
            creditCardExpirationDate = view.findViewById(R.id.creditCardExpirationDate)
            creditCardView = view.findViewById(R.id.creditCardView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.credit_card_layout, viewGroup, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.creditCardNumber.text = "XXXX-XXXX-XXXX-" + creditCards[position].lastDigits
        viewHolder.creditCardName.text = creditCards[position].name
        viewHolder.creditCardType.text = creditCards[position].type
        viewHolder.creditCardExpirationDate.text = creditCards[position].expirationDate

        viewHolder.creditCardView.setOnClickListener { creditCardClickListener.onCreditCardClick() }
    }

    override fun getItemCount() = creditCards.size
}