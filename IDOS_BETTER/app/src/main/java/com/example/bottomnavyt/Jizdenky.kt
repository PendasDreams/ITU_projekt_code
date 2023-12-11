package com.example.bottomnavyt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView  // Přidejte tento import
import DataBaseHandler
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView
import android.graphics.Color










// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Jizdenky.newInstance] factory method to
 * create an instance of this fragment.
 */
class Jizdenky : Fragment() {

    private lateinit var dbHelper: DataBaseHandler
    private lateinit var listView: ListView

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Jizdenky().apply {
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
        val view = inflater.inflate(R.layout.fragment_jizdenky, container, false)

        // Inicializace dbHelper
        dbHelper = DataBaseHandler(requireContext())

        // Inicializace ListView
        listView = view.findViewById(R.id.listView)

        // Najděte stávající ScrollView pro data
        val scrollView = view.findViewById<ScrollView>(R.id.scrollView)

        // Vytvořte nový LinearLayout pro zobrazení nových dat
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL

        // Insert an initial ticket into the "KoupenaJizdenka" table
        //insertInitialKoupenaJizdenkaData()

        Log.d("DIS", "Displaying jizdenky")

        // Načtení a zobrazení dat z databáze koupených jízdenek
        displayKoupenaJizdenkaData(linearLayout)

        // Přidejte nový LinearLayout s daty pod stávající ScrollView
        val scrollViewLayout = scrollView.getChildAt(0) as LinearLayout
        scrollViewLayout.addView(linearLayout)

        return view
    }



    private fun displayKoupenaJizdenkaData(linearLayout: LinearLayout) {
        val dbHelper = DataBaseHandler(requireContext())
        val purchasedTickets = dbHelper.getKoupenaJizdenkaData()

        for (data in purchasedTickets) {
            val odkud = data.odkud
            val kam = data.kam
            val casOd = data.casOd
            val casDo = data.casDo
            val vehicle = data.vehicle
            val cena = data.cena

            val entryLayout = createEntryLayout(odkud, kam, casOd, casDo, vehicle, cena) // Přidejte typ vozidla
            linearLayout.addView(entryLayout)
        }

        Log.d("DIS", "Displayed ${purchasedTickets.size} jizdenky")
    }
    private fun createEntryLayout(
        odkud: String,
        kam: String,
        casOd: String,
        casDo: String,
        vehicle: String, // Přidejte typ vozidla
        cena: Double
    ): LinearLayout {
        // Create a new LinearLayout for displaying purchased ticket data
        val entryLayout = LinearLayout(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        entryLayout.layoutParams = layoutParams
        entryLayout.orientation = LinearLayout.VERTICAL

        // Create TextViews for displaying ticket details
        val textViewOdkud = TextView(requireContext())
        val textViewKam = TextView(requireContext())
        val textViewCasOd = TextView(requireContext())
        val textViewCasDo = TextView(requireContext())
        val textViewVehicle = TextView(requireContext()) // TextView pro typ vozidla
        val textViewCena = TextView(requireContext())

        // Set text and formatting for TextViews
        val odkudText = "Odkud: $odkud"
        val kamText = "Kam: $kam"
        val casOdText = "Čas odjezdu: $casOd"
        val casDoText = "Čas příjezdu: $casDo"
        val vehicleText = "Typ vozidla: $vehicle" // Zobrazení typu vozidla
        val cenaText = "Cena: $cena"
        textViewOdkud.text = odkudText
        textViewKam.text = kamText
        textViewCasOd.text = casOdText
        textViewCasDo.text = casDoText
        textViewVehicle.text = vehicleText // Nastavení textu pro typ vozidla
        textViewCena.text = cenaText

        // Set text color to white
        textViewOdkud.setTextColor(Color.WHITE)
        textViewKam.setTextColor(Color.WHITE)
        textViewCasOd.setTextColor(Color.WHITE)
        textViewCasDo.setTextColor(Color.WHITE)
        textViewVehicle.setTextColor(Color.WHITE) // Nastavení barvy textu pro typ vozidla
        textViewCena.setTextColor(Color.WHITE)

        // Add TextViews to the entryLayout
        entryLayout.addView(textViewOdkud)
        entryLayout.addView(textViewKam)
        entryLayout.addView(textViewCasOd)
        entryLayout.addView(textViewCasDo)
        entryLayout.addView(textViewVehicle) // Přidání TextView pro typ vozidla
        entryLayout.addView(textViewCena)

        return entryLayout
    }
}