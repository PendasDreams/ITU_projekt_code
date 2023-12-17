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
import android.view.Gravity
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.Locale

/*
* Autor:xnovos14
* */
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

    private lateinit var linearLayout: LinearLayout // Add this property


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
        linearLayout = LinearLayout(requireContext())
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

        // Aktuální reálný čas
        val currentTimeMillis = System.currentTimeMillis()

        for ((id, data) in purchasedTickets) { // Změněno na procházení dvojic ID a dat
            val odkud = data.odkud
            val kam = data.kam
            val casOd = data.casOd
            val casDo = data.casDo
            val vehicle = data.vehicle
            val cena = data.cena

            // Převedení času odjezdu z formátu "yyyy-MM-dd HH:mm" na časový milisekundy
            val departureTimeMillis = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(casOd)?.time ?: 0

            // Pokud je čas odjezdu menší než aktuální reálný čas, odstraňte jízdenku
            if (departureTimeMillis < currentTimeMillis) {
                dbHelper.deleteKoupenaJizdenkaByCasOd(casOd)
                continue // Přeskočit tuto jízdenku
            }

            val entryLayout = createEntryLayout(id, odkud, kam, casOd, casDo, vehicle, cena)
            linearLayout.addView(entryLayout)
        }

        Log.d("DIS", "Displayed ${purchasedTickets.size} jizdenky")
    }
    private fun createEntryLayout(
        id: Int,
        odkud: String,
        kam: String,
        casOd: String,
        casDo: String,
        vehicle: String,
        cena: Double
    ): LinearLayout {
        // Create a new LinearLayout for displaying purchased ticket data
        val entryLayout = LinearLayout(requireContext())
        val entryLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        entryLayout.layoutParams = entryLayoutParams
        entryLayout.orientation = LinearLayout.VERTICAL

        // Create a blue rectangle (modrý obdélník) containing remaining time
        val blueRectangle = LinearLayout(requireContext())
        val blueRectangleLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            100  // Zde můžete nastavit požadovanou výšku obdélníku
        )
        blueRectangle.setBackgroundResource(R.color.colorSecondary)
        blueRectangle.layoutParams = blueRectangleLayoutParams
        blueRectangle.orientation = LinearLayout.HORIZONTAL
        blueRectangle.gravity = Gravity.CENTER_VERTICAL  // Zarovnání na střed vertikálně

// Create TextView for "Za zbývá:"
        val textViewZbyva = TextView(requireContext())
        val textViewZbyvaLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textViewZbyvaLayoutParams.leftMargin = 16  // Zde můžete nastavit požadovaný odstup od levého okraje
        textViewZbyva.layoutParams = textViewZbyvaLayoutParams
        textViewZbyva.text = "Za "
        textViewZbyva.textSize = 19f
        textViewZbyva.setTextColor(Color.WHITE)
        textViewZbyva.gravity = Gravity.CENTER_VERTICAL  // Zarovnání na střed vertikálně

// Calculate and display the remaining time
        val remainingTime = calculateRemainingTime(casOd)
        val textViewRemainingTime = TextView(requireContext())
        val remainingTimeLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        remainingTimeLayoutParams.leftMargin = 16  // Zde můžete nastavit požadovaný odstup od levého okraje
        textViewRemainingTime.layoutParams = remainingTimeLayoutParams
        textViewRemainingTime.text = remainingTime
        textViewRemainingTime.textSize = 19f
        textViewRemainingTime.setTextColor(Color.WHITE)
        textViewRemainingTime.gravity = Gravity.CENTER_VERTICAL or Gravity.START  // Zarovnání na střed vertikálně a na začátek horizontálně

// Add TextViews to the blue rectangle
        blueRectangle.addView(textViewZbyva)
        blueRectangle.addView(textViewRemainingTime)

// Add the blue rectangle to the entryLayout
        entryLayout.addView(blueRectangle)

        // Add space above vehicle text
        val spaceAboveVehicle = TextView(requireContext())
        val spaceAboveVehicleLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            16 // Zde můžete nastavit výšku mezery nad polem "vehicle"
        )
        spaceAboveVehicle.layoutParams = spaceAboveVehicleLayoutParams
        entryLayout.addView(spaceAboveVehicle)

        // Create TextView for displaying vehicle
        val textViewVehicle = TextView(requireContext()) // TextView pro typ vozidla
        textViewVehicle.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val vehicleText = "$vehicle"
        textViewVehicle.text = vehicleText
        textViewVehicle.textSize = 19f
        textViewVehicle.setTextColor(Color.RED)
        entryLayout.addView(textViewVehicle)

        // Add space below vehicle text
        val spaceBelowVehicle = TextView(requireContext())
        val spaceBelowVehicleLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            16 // Zde můžete nastavit výšku mezery pod polem "vehicle"
        )
        spaceBelowVehicle.layoutParams = spaceBelowVehicleLayoutParams
        entryLayout.addView(spaceBelowVehicle)

        // Create a horizontal LinearLayout for "Odkud" and "Čas odjezdu" on the first row
        val firstRowLayout = LinearLayout(requireContext())
        firstRowLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        firstRowLayout.orientation = LinearLayout.HORIZONTAL

        // Create TextView for "Odkud"
        val textViewOdkud = TextView(requireContext())
        val odkudText = "   $odkud"
        textViewOdkud.text = odkudText
        textViewOdkud.textSize = 16f
        textViewOdkud.setTextColor(Color.WHITE)

        // Create TextView for "Čas odjezdu"
        val textViewCasOd = TextView(requireContext())
        val casOdText = formatDateTimeToTime(casOd) // Zde použijeme formátovaný čas
        textViewCasOd.text = casOdText
        textViewCasOd.textSize = 16f
        textViewCasOd.setTextColor(Color.WHITE)

        // Add TextViews for "Odkud" and "Čas odjezdu" to the first row
        firstRowLayout.addView(textViewCasOd)
        firstRowLayout.addView(textViewOdkud)

        // Create a horizontal LinearLayout for "Kam" and "Čas příjezdu" on the second row
        val secondRowLayout = LinearLayout(requireContext())
        secondRowLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        secondRowLayout.orientation = LinearLayout.HORIZONTAL

        // Create TextView for "Kam"
        val textViewKam = TextView(requireContext())
        val kamText = "   $kam"
        textViewKam.text = kamText
        textViewKam.textSize = 16f
        textViewKam.setTextColor(Color.WHITE)

        // Create TextView for "Čas příjezdu"
        val textViewCasDo = TextView(requireContext())
        val casDoText = formatDateTimeToTime(casDo) // Zde použijeme formátovaný čas
        textViewCasDo.text = casDoText
        textViewCasDo.textSize = 16f
        textViewCasDo.setTextColor(Color.WHITE)

        // Add TextViews for "Kam" and "Čas příjezdu" to the second row
        secondRowLayout.addView(textViewCasDo)
        secondRowLayout.addView(textViewKam)

        // Add first and second rows to the entryLayout
        entryLayout.addView(firstRowLayout)
        entryLayout.addView(secondRowLayout)




        // Create a blue rectangle (modrý obdélník) containing remaining time
        val blueRectangle2 = LinearLayout(requireContext())
        val blueRectangleLayoutParams2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            5  // Zde můžete nastavit požadovanou výšku obdélníku
        )
        blueRectangle2.setBackgroundResource(R.color.colorSecondary)
        blueRectangle2.layoutParams = blueRectangleLayoutParams2
        blueRectangle2.orientation = LinearLayout.HORIZONTAL
        blueRectangle2.gravity = Gravity.CENTER_VERTICAL  // Zarovnání na střed vertikálně


        // Add the blue rectangle to the entryLayout
        entryLayout.addView(blueRectangle2)



        // Create a blue rectangle (modrý obdélník) containing transaction code, date, tariff, and price
        val blueRectangle3 = LinearLayout(requireContext())
        val blueRectangleLayoutParams3 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        blueRectangle3.setBackgroundResource(R.color.black)
        blueRectangle3.layoutParams = blueRectangleLayoutParams3
        blueRectangle3.orientation = LinearLayout.VERTICAL

// Create TextView for "Kód transakce"
        val textViewTransactionCode = TextView(requireContext())
        val transactionCode = "Kód transakce    XXXX"
        textViewTransactionCode.text = transactionCode
        textViewTransactionCode.textSize = 16f
        textViewTransactionCode.setTextColor(Color.WHITE)

// Create TextView for "Datum" (použije nový formát datumu)
        val textViewDate = TextView(requireContext())
        val dateText = "Datum                 ${formatDateTimeToDate(casOd)}"
        textViewDate.text = dateText
        textViewDate.textSize = 16f
        textViewDate.setTextColor(Color.WHITE)

// Create TextView for "Tarif"
        val textViewTarif = TextView(requireContext())
        val tarifText = "Tarif                     Základní"
        textViewTarif.text = tarifText
        textViewTarif.textSize = 16f
        textViewTarif.setTextColor(Color.WHITE)

// Create TextView for "Cena"
        val textViewPrice = TextView(requireContext())
        val priceText = "Cena                    $cena Kč"
        textViewPrice.text = priceText
        textViewPrice.textSize = 16f
        textViewPrice.setTextColor(Color.WHITE)

// Add TextViews for transaction code, date, tariff, and price to the blue rectangle
        blueRectangle3.addView(textViewTransactionCode)
        blueRectangle3.addView(textViewDate)
        blueRectangle3.addView(textViewTarif)
        blueRectangle3.addView(textViewPrice)

// Add the blue rectangle to the entryLayout
        entryLayout.addView(blueRectangle3)


// Create a blue rectangle (modrý obdélník) with a height of 5
        val blueRectangle4 = LinearLayout(requireContext())
        val blueRectangleLayoutParams4 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            5  // Zde můžete nastavit požadovanou výšku obdélníku (5 px)
        )
        blueRectangle4.setBackgroundResource(R.color.colorSecondary)
        blueRectangle4.layoutParams = blueRectangleLayoutParams4
        blueRectangle4.orientation = LinearLayout.HORIZONTAL
        blueRectangle4.gravity = Gravity.CENTER_VERTICAL  // Zarovnání na střed vertikálně

// Add the fourth blue rectangle to the entryLayout
        entryLayout.addView(blueRectangle4)

        // Create a red button for "reklamovat" with smaller size
        val reklamovatButton = Button(requireContext())
        val reklamovatButtonLayoutParams = LinearLayout.LayoutParams(
            350, // Nastavte požadovanou šířku tlačítka
            100  // Nastavte požadovanou výšku tlačítka
        )
        reklamovatButtonLayoutParams.gravity = Gravity.RIGHT
        reklamovatButton.layoutParams = reklamovatButtonLayoutParams
        reklamovatButton.text = "Reklamovat"
        reklamovatButton.setTextColor(Color.WHITE)
        reklamovatButton.setBackgroundResource(R.color.red) // Zde můžete použít vlastní drawable pro pozadí
        reklamovatButton.setOnClickListener {
            // Obsluha události pro tlačítko "reklamovat" (co se má stát po kliknutí)
            // Zde můžete zavolat funkci pro smazání položky z databáze s předáním ID.
            // Například:
            dbHelper.deleteKoupenaJizdenkaById(id)
            // Poté, co je položka smazána z databáze, odstraňte také LinearLayout s touto položkou z view.
            linearLayout.removeView(entryLayout)
        }
        entryLayout.addView(reklamovatButton)



// Přidejte mezeru pod blueRectangle4
        val spaceBelowBlueRectangle4 = TextView(requireContext())
        val spaceBelowBlueRectangle4LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            16 // Zde můžete nastavit výšku mezery pod blueRectangle4
        )
        spaceBelowBlueRectangle4.layoutParams = spaceBelowBlueRectangle4LayoutParams
        entryLayout.addView(spaceBelowBlueRectangle4)







        return entryLayout
    }





    private fun formatDateTimeToTime(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return outputFormat.format(date)
    }

    private fun calculateRemainingTime(casOd: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDate = System.currentTimeMillis()
        val departureDate = dateFormat.parse(casOd)?.time ?: 0
        val remainingTimeMillis = departureDate - currentDate

        val hours = (remainingTimeMillis / (1000 * 60 * 60)).toInt()
        val minutes = ((remainingTimeMillis / (1000 * 60)) % 60).toInt()

        return String.format("%02d:%02d", hours, minutes)
    }

    private fun formatDateTimeToDate(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return outputFormat.format(date)
    }





}