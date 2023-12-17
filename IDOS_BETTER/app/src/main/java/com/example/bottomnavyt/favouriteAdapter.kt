package com.example.bottomnavyt

import DataBaseHandler
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
* Author: Michal Dohnal
* Login : xdohna52
*
* */
interface FavouriteItemClickListener {
    fun onFavouriteItemClick(position: Int)
    fun onFavouriteItemButtonClick(position: Int)
}
class favouriteAdapter(context: FragmentActivity, items: List<favouriteListElem?>?) :
    ArrayAdapter<favouriteListElem?>(
        context!!, R.layout.fav_list_elem, items!!
    ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.fav_list_elem, null)
        }
        val textView = view!!.findViewById<TextView>(R.id.favouriteText)
        val imageButton = view.findViewById<ImageButton>(R.id.favouriteMenu)
        val listItem = getItem(position)
        if (listItem != null) {
            textView.text = listItem.text //line 37
            view.setOnClickListener {
                handleListItemClick(listItem)
            }
            imageButton.tag = position
            imageButton.setOnClickListener {
                // Retrieve the tag (integer value) associated with the ImageButton
                val associatedValue = imageButton.tag as Int
                handleButtonClick( listItem, associatedValue)
            }
        }
        return view
    }

    private fun getCurrentTime(): String {
            val currentTime = Date()
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(currentTime)
    }

    private fun handleListItemClick(item: favouriteListElem) {

        val currentTime = getCurrentTime()
        val entryText = item.text
        val split = entryText.split("->")
        val odkud = split[0].trim()
        val kam = split[1].trim()

        val fragment = Spojeni.newInstance(odkud, kam, currentTime)
        val fragmentManager = (context as FragmentActivity).supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frame_layout, fragment)

        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

    private fun handleButtonClick(item: favouriteListElem, position: Int) {
        // Handle click on the ImageButton
        Toast.makeText(context, "Button clicked for position: $position", Toast.LENGTH_SHORT).show()
        // You can also use the context to navigate to another activity or fragment if needed
        val dbHelper = DataBaseHandler(context)
        if (dbHelper.deleteFavourite(item.id)) {
            // Entry successfully deleted from the database
            // You may also want to remove the item from the adapter's data set
            // and call notifyDataSetChanged() to refresh the ListView
            remove(item)
            notifyDataSetChanged()
        } else {
            // Failed to delete entry from the database
            Toast.makeText(context, "Failed to delete entry from the database", Toast.LENGTH_SHORT).show()
        }

    }
}
