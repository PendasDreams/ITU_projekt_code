package com.example.bottomnavyt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import DataBaseHandler
import com.example.bottomnavyt.CreditUpdateListener
import com.example.bottomnavyt.R

/*
* autor: xpolia05
* */
class AddCredit : Fragment() {

    // Declare UI components
    private lateinit var editTextCreditAmount: EditText
    private lateinit var btnPayByCard: Button
    private lateinit var databaseHandler: DataBaseHandler

    // Initialize listener that will be used to notify other components
    var creditUpdateListener: CreditUpdateListener? = null
        set(value) {
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addcredit, container, false)

        // Initialize the UI components
        editTextCreditAmount = view.findViewById(R.id.editTextCreditAmount)
        btnPayByCard = view.findViewById(R.id.btnPayByCard)

        // Initialize the database handler
        databaseHandler = DataBaseHandler(requireContext())

        // Set the button click listener
        btnPayByCard.setOnClickListener {
            handlePayAction()
        }

        return view
    }

    private fun handlePayAction() {
        val creditAmountStr = editTextCreditAmount.text.toString()
        if (creditAmountStr.isNotEmpty()) {
            try {
                val creditAmount = creditAmountStr.toDouble()
                val resultId = databaseHandler.insertCredit(creditAmount)

                if (resultId > 0) {

                    creditUpdateListener?.onCreditUpdated(creditAmount)

                    // Clear the input field after inserting the credit
                    editTextCreditAmount.text.clear()
                    // Show a success message
                    Toast.makeText(context, "Credit added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the error, insertion didn't work
                    Toast.makeText(context, "Error adding credit", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                // Handle the error if credit amount is not a number
                Toast.makeText(context, "Please enter a valid credit amount", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please enter a credit amount", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCreditUpdateListener(listener: CreditUpdateListener) {
        creditUpdateListener = listener
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddCredit()
    }
}
