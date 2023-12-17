package com.example.bottomnavyt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import DataBaseHandler

/*
* Autoři: xpolia05, xnovos14
*
* primárně xpolia05, xnovos14 označeno
* */
class Finance : Fragment(), CreditUpdateListener {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var databaseHandler: DataBaseHandler

    //xnovos14
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    //konec xnovos14
    override fun onResume() {
        super.onResume()
        if (::databaseHandler.isInitialized) {
            val latestCredit = databaseHandler.getTotalCredit()
            onCreditUpdated(latestCredit)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseHandler = DataBaseHandler(requireContext())

        val view = inflater.inflate(R.layout.fragment_finance, container, false)

        val buttonAddEntry = view.findViewById<ImageView>(R.id.addNewEntry)
        buttonAddEntry.setOnClickListener {
            openAddCreditFragment()
        }

        val latestCredit = databaseHandler.getTotalCredit()
        onCreditUpdated(latestCredit)

        return view
    }
    override fun onCreditUpdated(newCredit: Double) {
        // Update the TextView with ID currentCredit
        val currentCreditTextView = view?.findViewById<TextView>(R.id.currentCredit)
        currentCreditTextView?.text = "Aktuální kredit\n${newCredit} CZK"
    }
    private fun openAddCreditFragment() {
        val addCreditFragment = AddCredit.newInstance()
        addCreditFragment.updateCreditUpdateListener(this) // Set the listener
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, addCreditFragment) // Use your container id
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //xnovos14
    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Finance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
