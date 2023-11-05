package com.example.bottomnavyt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ImageView
import com.example.bottomnavyt.History


class Home : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val buttonHledat = view.findViewById<RelativeLayout>(R.id.buttonHledat)
        buttonHledat.setOnClickListener {
            openSpojeniFragment()
        }
        return view
    }

    fun openSpojeniFragment() {
        val spojeniFragment = Spojeni.newInstance("param1", "param2")
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,spojeniFragment )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun openPrehledVydajuFragment() {
        val expensesOverviewFragment = ExpensesOverview.newInstance("param1", "param2")
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, expensesOverviewFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun openAddCreditFragment() {
        val addCreditFragment = AddCredit.newInstance() // Replace with the actual new fragment class
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, addCreditFragment) // Use your container id
        transaction.addToBackStack(null)
        transaction.commit()
    }

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
