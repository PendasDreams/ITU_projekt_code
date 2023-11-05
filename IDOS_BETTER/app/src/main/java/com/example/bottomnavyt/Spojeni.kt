import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.database.Cursor
import com.example.bottomnavyt.R


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

    private fun displaySpojeniData(view: View) {
        val textView = view.findViewById<TextView>(R.id.textView)

        val odkudValues = dbHelper.getAllOdkudValues()

        for (odkudValue in odkudValues) {
            // Display data in the TextView
            textView.append("Odkud: $odkudValue\n")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spojeni, container, false)

        // Initialize dbHelper
        dbHelper = DataBaseHandler(requireContext())

        // Insert data into the Spojeni table
        dbHelper.insertSpojeni("Praha", "Brno")

        // Get and display data from the Spojeni table using the new function
        displaySpojeniData(view)

        return view
    }
}
