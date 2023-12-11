import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bottomnavyt.R
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.util.Log
import android.widget.Toast






class Spojeni : Fragment() {
    private lateinit var dbHelper: DataBaseHandler

    companion object {
        fun newInstance(odkud: String, kam: String): Spojeni {
            val fragment = Spojeni()
            val args = Bundle()
            args.putString("odkud", odkud)
            args.putString("kam", kam)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spojeni, container, false)

        // Initialize dbHelper
        dbHelper = DataBaseHandler(requireContext())

        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)

        // Získání hodnot z arguments
        val odkud = arguments?.getString("odkud") ?: ""
        val kam = arguments?.getString("kam") ?: ""

        // Display and insert data into LinearLayout
        Log.d("Database", "before")

        displayAndInsertSpojeniData(linearLayout, odkud, kam)

        Log.d("Database", "after")

        return view
    }


    private fun displayAndInsertSpojeniData(linearLayout: LinearLayout, odkud: String, kam: String) {
        // Initialize dbHelper

        Log.d("SpojeniFragment", "Odkud: $odkud, Kam: $kam")


        val dbHelper = DataBaseHandler(requireContext())

        Log.d("Database", "before delete")

        // Clear existing data by deleting all entries
        dbHelper.deleteAllSpojeni()

        // Insert initial data
        dbHelper.insertInitialSpojeniData()

        val cursor = dbHelper.getSpojeniByOdkudKam(odkud, kam)

        while (cursor.moveToNext()) {
            // Přidejte TextView s hodnotou casOd před každým výpisem spojení
            val timeDateText = TextView(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                110 // Změňte výšku podle vašich potřeb
            )
            layoutParams.gravity = Gravity.LEFT // Nastavení zarovnání k levému okraji
            timeDateText.layoutParams = layoutParams

            // Nastavení vertikálního zarovnání na střed
            timeDateText.gravity = Gravity.CENTER_VERTICAL

            // Odsazení od levého okraje (změňte hodnotu dle potřeby)
            val leftPadding = 16
            timeDateText.setPadding(leftPadding, 0, 0, 0)

            val casOd = cursor.getString(cursor.getColumnIndex(COL_CAS_OD))
            val formattedDate = formatDateTimeToMonth(casOd) + " " + formatDateTimeToTime(casOd)
            timeDateText.text = formattedDate
            timeDateText.textSize = 16f
            timeDateText.setBackgroundResource(R.color.colorSecondary)
            timeDateText.setTextColor(Color.WHITE)

            linearLayout.addView(timeDateText)

            // Zde můžete načíst data z databáze a vytvořit výpis spojení pro každý řádek
            val casDo = cursor.getString(cursor.getColumnIndex(COL_CAS_DO))
            val cena = cursor.getDouble(cursor.getColumnIndex(COL_CENA)).toInt() // Převedení na celé číslo

            val vehicle = cursor.getString(cursor.getColumnIndex(COL_VEHICLE))

            // Zde vytvořte výpis spojení pro každý řádek a přidejte jej do LinearLayout
            val formattedCasOd = formatDateTimeToTime(casOd)
            val formattedCasDo = formatDateTimeToTime(casDo)
            val entryLayout = createEntryLayout(odkud, kam, formattedCasOd, formattedCasDo, cena, casOd,vehicle)
            linearLayout.addView(entryLayout)
        }

        cursor.close()
    }

    private fun formatDateTimeToTime(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return outputFormat.format(date)
    }

    private fun formatDateTimeToMonth(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return outputFormat.format(date)
    }



    private fun createEntryLayout(
        odkud: String,
        kam: String,
        departureDateTime: String,
        arrivalDateTime: String,
        cena: Int,
        casOd: String,
        vehicle: String
    ): LinearLayout {

        dbHelper = DataBaseHandler(requireContext())


        val entryLayout = LinearLayout(requireContext())
        entryLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        entryLayout.orientation = LinearLayout.VERTICAL



        // Text na prvním řádku (Vozidlo)
        val vehicleText = TextView(requireContext())
        val vehicleLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        vehicleLayoutParams.topMargin = 20 // Mezera nad textem
        vehicleLayoutParams.bottomMargin = 20 // Mezera pod textem
        vehicleText.layoutParams = vehicleLayoutParams
        vehicleText.text = "$vehicle"
        vehicleText.textSize = 19f
        vehicleText.setTextColor(Color.RED)
        entryLayout.addView(vehicleText)


        // Text na druhém  řádku (Čas od a Kam)
        val casKamText = TextView(requireContext())
        casKamText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        casKamText.text = "$departureDateTime $odkud"
        casKamText.textSize = 16f
        casKamText.setTextColor(Color.WHITE)
        entryLayout.addView(casKamText)

        // Prázdný TextView pro mezeru
        val spaceText1 = TextView(requireContext())
        spaceText1.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            16 // Zde můžete nastavit výšku mezery
        )
        entryLayout.addView(spaceText1)

        // Text na třetím řádku (Čas do a Odkud)
        val casOdkudText = TextView(requireContext())
        casOdkudText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        casOdkudText.text = "$arrivalDateTime $kam"
        casOdkudText.textSize = 16f
        casOdkudText.setTextColor(Color.WHITE)
        entryLayout.addView(casOdkudText)







        // Cena jako tlačítko
        val priceButton = Button(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        priceButton.layoutParams = layoutParams
        priceButton.text = "$cena Kč"
        priceButton.textSize = 16f
        priceButton.setTextColor(Color.WHITE)
        priceButton.setBackgroundResource(R.color.colorPrimary)

        // Přidání akce na kliknutí na tlačítko s cenou
        priceButton.setOnClickListener {
            // Zde získáte informace o spojení
            val spojeni = dbHelper.getSpojeniByOdkudKam(odkud, kam)
            if (spojeni.moveToFirst()) {
                val spojeniId = spojeni.getLong(spojeni.getColumnIndex(COL_ID))
                val odkudSpojeni = spojeni.getString(spojeni.getColumnIndex(COL_ODKUD))
                val kamSpojeni = spojeni.getString(spojeni.getColumnIndex(COL_KAM))
                val casOdSpojeni = spojeni.getString(spojeni.getColumnIndex(COL_CAS_OD))
                val casDoSpojeni = spojeni.getString(spojeni.getColumnIndex(COL_CAS_DO))
                val vehicleSpojeni = spojeni.getString(spojeni.getColumnIndex(COL_VEHICLE))
                val cenaSpojeni = spojeni.getDouble(spojeni.getColumnIndex(COL_CENA))

                // Přidání spojení do koupených jízdenek
                val jizdenkaId = dbHelper.insertKoupenaJizdenka(spojeniId,odkudSpojeni,kamSpojeni, casOdSpojeni, casDoSpojeni,vehicle, cenaSpojeni)


                // Zde můžete zobrazit zprávu nebo provést další akce po přidání jízdenky
                Toast.makeText(requireContext(), "Jízdenka zakoupena s ID: $jizdenkaId", Toast.LENGTH_SHORT).show()
            }

            spojeni.close()
        }

        entryLayout.addView(priceButton)


        // Prázdný TextView pro mezeru
        val spaceText2 = TextView(requireContext())
        spaceText2.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            16 // Zde můžete nastavit výšku mezery
        )
        entryLayout.addView(spaceText2)



        return entryLayout
    }

    private fun createInfoLayout(label: String, value: String): LinearLayout {
        val infoLayout = LinearLayout(requireContext())
        infoLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // Use WRAP_CONTENT directly here
            LinearLayout.LayoutParams.WRAP_CONTENT  // Use WRAP_CONTENT directly here
        )
        infoLayout.orientation = LinearLayout.VERTICAL

        val labelTextView = TextView(requireContext())
        labelTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // Use WRAP_CONTENT directly here
            LinearLayout.LayoutParams.WRAP_CONTENT  // Use WRAP_CONTENT directly here
        )
        labelTextView.text = label
        labelTextView.textSize = 12f
        labelTextView.setTextColor(Color.GRAY)

        val valueTextView = TextView(requireContext())
        valueTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // Use WRAP_CONTENT directly here
            LinearLayout.LayoutParams.WRAP_CONTENT  // Use WRAP_CONTENT directly here
        )
        valueTextView.text = value
        valueTextView.textSize = 16f
        valueTextView.setTextColor(Color.WHITE)

        infoLayout.addView(labelTextView)
        infoLayout.addView(valueTextView)

        return infoLayout
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}
