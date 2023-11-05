package com.example.bottomnavyt

import DataBaseHandler
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Date
import com.example.bottomnavyt.History_obj

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class History_obj(
    val casOd: String?,
    val casDo: String?,
    val mistoOd: String?,
    val mistoDo: String?,
    val cena: Int
)

class History : Fragment() {

    // Declare UI elements
    private lateinit var editTextCasOd: EditText
    private lateinit var editTextCasDo: EditText
    private lateinit var editTextMistoOd: EditText
    private lateinit var editTextMistoDo: EditText
    private lateinit var editTextCena: EditText
    private lateinit var buttonPridat: Button
    private lateinit var textViewResultLeft: TextView
    private lateinit var textViewResultRight: TextView
    private lateinit var resultLayout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Initialize UI elements
        editTextCasOd = view.findViewById(R.id.editTextCasOd)
        editTextCasDo = view.findViewById(R.id.editTextCasDo)
        editTextMistoOd = view.findViewById(R.id.editTextMistoOd)
        editTextMistoDo = view.findViewById(R.id.editTextMistoDo)
        editTextCena = view.findViewById(R.id.editTextCena)
        buttonPridat = view.findViewById(R.id.buttonPridat)
        textViewResultLeft = view.findViewById(R.id.textViewResultLeft)
        textViewResultRight = view.findViewById(R.id.textViewResultRight)
        resultLayout = view.findViewById(R.id.resultLayout)

        buttonPridat.setOnClickListener {
            try {
                val data = getFormData()
                val dbHandler = DataBaseHandler(requireContext())

                // Vložení dat do databáze
                val insertedId = dbHandler.insertData(data)

                // Nastavení textu pro levý a pravý TextView
                textViewResultLeft.text = "${data.casOd} ${data.casDo} -> ${data.mistoDo}"
                textViewResultRight.text = "${data.cena} Kč"

                // Nastavení vzhledu TextView pro vylepšený vzhled
                resultLayout.setBackgroundResource(R.drawable.text_view_background) // Nastavte pozadí
                textViewResultLeft.setTextColor(Color.WHITE) // Nastavte barvu textu pro levý TextView
                textViewResultRight.setTextColor(Color.WHITE) // Nastavte barvu textu pro pravý TextView
                textViewResultLeft.textSize = 18f // Nastavte velikost textu pro levý TextView
                textViewResultRight.textSize = 18f // Nastavte velikost textu pro pravý TextView

                // Získání aktuálního textu z levého a pravého TextView
                val currentTextLeft = textViewResultLeft.text.toString()
                val currentTextRight = textViewResultRight.text.toString()

                // Vytvoření nového textu, který zahrnuje předchozí data a nová data
                val newTextLeft = "$currentTextLeft"
                val newTextRight = "$currentTextRight"

                // Aktualizace textu v levém a pravém TextView
                textViewResultLeft.text = newTextLeft
                textViewResultRight.text = newTextRight


            } catch (e: Exception) {
                val errorMessage = "Chyba při vkládání dat: ${e.message}"
                textViewResultLeft.text = errorMessage
            }
        }

        return view
    }

    private fun getFormData(): History_obj {
        val casOd = editTextCasOd.text.toString()
        val casDo = editTextCasDo.text.toString()
        val mistoOd = editTextMistoOd.text.toString()
        val mistoDo = editTextMistoDo.text.toString()
        val cena = editTextCena.text.toString().toInt()

        return History_obj(casOd, casDo, mistoOd, mistoDo, cena)
    }

    private fun insertDataToDatabase() {
        val data = getFormData()
        val dbHandler = DataBaseHandler(requireContext())

        val insertedId = dbHandler.insertData(data)

        val message = "Data byla úspěšně vložena s ID: $insertedId"
        textViewResultLeft.text = message
    }

    companion object {
        fun newInstance(param1: String, param2: String): History {
            val fragment = History()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
