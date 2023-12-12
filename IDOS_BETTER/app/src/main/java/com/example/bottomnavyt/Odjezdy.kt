package com.example.bottomnavyt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ImageView
import com.example.bottomnavyt.History
import android.widget.EditText
import android.widget.Toast


class Odjezdy : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_odjezdy, container, false)

        val buttonHledat = view.findViewById<RelativeLayout>(R.id.buttonHledat)
        val editTextOdkud = view.findViewById<EditText>(R.id.editTextOdkud)
        val editTextCasOdjezdu = view.findViewById<EditText>(R.id.timeTextView) // Přidáno pro získání času odjezdu

        buttonHledat.setOnClickListener {
            var odkud = editTextOdkud.text.toString()
            var casOdjezdu = editTextCasOdjezdu.text.toString()

            if (!isCasOdjezduValid(casOdjezdu)) {
                // Čas odjezdu nebyl zadán ve správném formátu
                // Můžete zobrazit upozornění nebo provést jinou akci
                // Například zobrazení Toast zprávy
                Toast.makeText(requireContext(), "Neplatný formát času odjezdu (očekává se HH:mm)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pokračujte pouze pokud je čas odjezdu ve správném formátu
            if (odkud.isEmpty()) {
                odkud = "Hlavní nádraží"
            }



            if (casOdjezdu.isEmpty()) {
                casOdjezdu = "15:00"
            }

            openSpojeniFragment(odkud,"Hlavní nádraží" ,casOdjezdu)
        }



        return view
    }

    fun openSpojeniFragment(odkud: String, kam: String, casOdjezdu: String) {
        val spojeniFragment = Spojeni.newInstance(odkud, "Hlavní nádraží", casOdjezdu)
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, spojeniFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun isCasOdjezduValid(casOdjezdu: String): Boolean {
        val timeRegex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]\$")
        return timeRegex.matches(casOdjezdu)
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
