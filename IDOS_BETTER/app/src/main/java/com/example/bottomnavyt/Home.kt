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
import DataBaseHandler
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale



class Home : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var dbHelper: DataBaseHandler


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

        dbHelper = DataBaseHandler(requireContext())

        val historyListView = view.findViewById<ListView>(R.id.historyListView)

        // Call the displayVyhledavani function to get the history entries
        val historyEntries = dbHelper.displayVyhledavani()

        val historyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, historyEntries)
        historyListView.adapter = historyAdapter

        historyListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEntry = historyAdapter.getItem(position)
            // Now you have the selected history entry, do something with it
            if (selectedEntry != null) {
                val currentTime = getCurrentTime() // Získání aktuálního času
                val entryParts = selectedEntry.split("->")
                if (entryParts.size == 2) {
                    val odkud = entryParts[0].trim()
                    val kam = entryParts[1].trim()
                    openSpojeniFragment(odkud, kam, currentTime)
                }
            }
        }

        //Michal Dohnal xdohna52
        val favouriteListView = view.findViewById<ListView>(R.id.favouriteListView)
        val favouriteEntries = dbHelper.displayFavourite()
        val  favAdapter =  favouriteAdapter( requireActivity() ,favouriteEntries)
        favouriteListView.adapter = favAdapter


        val buttonHledat = view.findViewById<RelativeLayout>(R.id.buttonHledat)
        val editTextOdkud = view.findViewById<EditText>(R.id.editTextOdkud)
        val editTextKam = view.findViewById<EditText>(R.id.editTextKam)
        val editTextCasOdjezdu = view.findViewById<EditText>(R.id.timeTextView) // Přidáno pro získání času odjezdu

        buttonHledat.setOnClickListener {
            var odkud = editTextOdkud.text.toString()
            var kam = editTextKam.text.toString()
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

            if (kam.isEmpty()) {
                kam = "Semilasso"
            }

            if (casOdjezdu.isEmpty()) {
                casOdjezdu = "15:00"
            }

            dbHelper.insertVyhledavani(odkud, kam)

            Log.d("HomeFragment", "Vyhledávání bylo vloženo: Odkud: $odkud, Kam: $kam, Čas odjezdu: $casOdjezdu")

            openSpojeniFragment(odkud, kam, casOdjezdu)
        }

        return view
    }


    fun openSpojeniFragment(odkud: String, kam: String, casOdjezdu: String) {
        val spojeniFragment = Spojeni.newInstance(odkud, kam, casOdjezdu)
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

    fun getCurrentTime(): String {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(currentTime)
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
