package com.example.bottomnavyt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.database.Cursor
import android.widget.ListView  // Přidejte tento import
import DataBaseHandler
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView
import android.widget.Toast  // Add this import statement
import android.graphics.Color










// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {

    private lateinit var dbHelper: DataBaseHandler
    private lateinit var listView: ListView

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    // TODO: Rename and change types of parameters
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
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicializace dbHelper
        dbHelper = DataBaseHandler(requireContext())

        // Inicializace ListView
        listView = view.findViewById(R.id.listView)


        // Vytvoření lineárního rozložení pro zobrazení dat
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL

        // Insert an initial ticket into the "KoupenaJizdenka" table
        //insertInitialKoupenaJizdenkaData()

        Log.d("DIS", "Displaying jizdenky")

        // Načtení a zobrazení dat z databáze koupených jízdenek
        displayKoupenaJizdenkaData(linearLayout)

        return linearLayout  // Set linearLayout as the root view of the fragment
    }




    private fun displayKoupenaJizdenkaData(linearLayout: LinearLayout) {
        val dbHelper = DataBaseHandler(requireContext())
        val purchasedTickets = dbHelper.getKoupenaJizdenkaData()

        for (data in purchasedTickets) {
            val casOd = data.casOd
            val casDo = data.casDo
            val cena = data.cena

            val entryLayout = createEntryLayout(casOd, casDo, cena)
            linearLayout.addView(entryLayout)
        }

        Log.d("DIS", "Displayed ${purchasedTickets.size} jizdenky")
    }
    private fun createEntryLayout(casOd: String, casDo: String, cena: Double): LinearLayout {
        // Create a new LinearLayout for displaying purchased ticket data
        val entryLayout = LinearLayout(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        entryLayout.layoutParams = layoutParams
        entryLayout.orientation = LinearLayout.VERTICAL

        // Create TextViews for displaying ticket details
        val textViewCasOd = TextView(requireContext())
        val textViewCasDo = TextView(requireContext())
        val textViewCena = TextView(requireContext())

        // Set text and formatting for TextViews
        val casOdText = "Čas odjezdu: $casOd"
        val casDoText = "Čas příjezdu: $casDo"
        val cenaText = "Cena: $cena"
        textViewCasOd.text = casOdText
        textViewCasDo.text = casDoText
        textViewCena.text = cenaText

        // Set text color to white
        textViewCasOd.setTextColor(Color.BLACK)
        textViewCasDo.setTextColor(Color.BLACK)
        textViewCena.setTextColor(Color.BLACK)

        // Add TextViews to the entryLayout
        entryLayout.addView(textViewCasOd)
        entryLayout.addView(textViewCasDo)
        entryLayout.addView(textViewCena)

        // Log the values for debugging
        Log.d("DIS", "createEntryLayout called with:")
        Log.d("DIS", "Cas Odjezdu: $casOd")
        Log.d("DIS", "Cas Příjezdu: $casDo")
        Log.d("DIS", "Cena: $cena")

        return entryLayout
    }

}