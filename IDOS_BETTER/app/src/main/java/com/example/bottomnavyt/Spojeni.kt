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


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Spojeni : Fragment() {
    private lateinit var dbHelper: DataBaseHandler

    companion object {
        fun newInstance(param1: String, param2: String) = Spojeni().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
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

        // Display and insert data into LinearLayout
        displayAndInsertSpojeniData(linearLayout)

        return view
    }


    private fun displayAndInsertSpojeniData(linearLayout: LinearLayout) {
        // Initialize dbHelper
        dbHelper = DataBaseHandler(requireContext())

        // Clear existing data by deleting all entries
        dbHelper.deleteAllSpojeni()

        // Insert initial data
        dbHelper.insertInitialSpojeniData()

        val cursor = dbHelper.getAllSpojeni()

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

            val odkud = cursor.getString(cursor.getColumnIndex(COL_ODKUD))
            val kam = cursor.getString(cursor.getColumnIndex(COL_KAM))
            val casDo = cursor.getString(cursor.getColumnIndex(COL_CAS_DO))
            val cena = cursor.getDouble(cursor.getColumnIndex(COL_CENA)).toInt() // Převedení na celé číslo

            // Zde vytvořte výpis spojení pro každý řádek a přidejte jej do LinearLayout
            val formattedCasOd = formatDateTimeToTime(casOd)
            val formattedCasDo = formatDateTimeToTime(casDo)
            val entryLayout = createEntryLayout(odkud, kam, formattedCasOd, formattedCasDo, cena, casOd)
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
        casOd: String // Add casOd as a parameter
    ): LinearLayout {
        val entryLayout = LinearLayout(requireContext())
        entryLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        entryLayout.orientation = LinearLayout.VERTICAL



        // Text na druhém řádku (Čas do a Odkud)
        val casOdkudText = TextView(requireContext())
        casOdkudText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        casOdkudText.text = "Čas do: $arrivalDateTime - Odkud: $odkud"
        casOdkudText.textSize = 16f
        casOdkudText.setTextColor(Color.WHITE)
        entryLayout.addView(casOdkudText)


        // Text na prvním řádku (Čas od a Kam)
        val casKamText = TextView(requireContext())
        casKamText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        casKamText.text = "Čas od: $casOd - Kam: $kam"
        casKamText.textSize = 16f
        casKamText.setTextColor(Color.WHITE)
        entryLayout.addView(casKamText)


        // Price
        val priceText = TextView(requireContext())
        priceText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        priceText.text = "$cena Kč"
        priceText.textSize = 16f
        priceText.setTextColor(Color.WHITE)

        entryLayout.addView(priceText)

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
