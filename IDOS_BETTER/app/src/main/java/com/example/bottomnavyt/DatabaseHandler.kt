import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.database.Cursor
import com.example.bottomnavyt.favouriteListElem

/*
* Autoři: xnovos14, xpolia05, xdohna52
*
* primarně xnovos14, xpolia05 a xdohna52 označeno
* */


const val TAG = "DataBaseHandler"

val DATABASE_NAME = "MyDB"


val COL_CAS_OD = "CasOd"
val COL_CAS_DO = "CasDo"
val COL_CENA = "Cena"

val TABLE_CREDITS = "Credits"
val COL_CREDIT_AMOUNT = "CreditAmount"
val COL_ID = "ID"

val TABLE_SPOJENI = "Spojeni"
val COL_ODKUD = "Odkud"
val COL_KAM = "Kam"

val TABLE_KOUPENA_JIZDENKA = "KoupeneSpojeni"
val COL_SPOJENI_ID = "Koupene_spojeni_ID"

val COL_VEHICLE ="vozidlo"

val TABLE_VYHLEDAVANI = "VyhledavaneSpojeni"

val TABLE_FAVOURITE = "OblibeneJizdenky"



data class SpojeniData(
    val odkud: String,
    val kam: String,
    val casOd: String,
    val casDo: String,
    val vehicle: String,
    val cena: Double
)


class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        Log.d("Database", "Creating database")


        Log.d("Database", "Table created")

        val createVyhledavaniTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_VYHLEDAVANI (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_ODKUD TEXT," + // Use TEXT instead of VARCHAR(255)
                    "$COL_KAM TEXT)"     // Use TEXT instead of VARCHAR(255)
        db?.execSQL(createVyhledavaniTable)

        Log.d("Database", "Table created")

        //MichaL Dohnal xdohna52
        val createoblibenaJizdenkaTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_FAVOURITE ("+
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_ODKUD TEXT," +
                    "$COL_KAM TEXT)"
        db?.execSQL(createoblibenaJizdenkaTable)
        Log.d("Database", "Table created")
        //

        val createSpojeniTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_SPOJENI (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_ODKUD VARCHAR(255)," +
                    "$COL_KAM VARCHAR(255)," +
                    "$COL_CAS_OD DATETIME," +
                    "$COL_CAS_DO DATETIME," +
                    "$COL_VEHICLE VARCHAR(255)," +
                    "$COL_CENA DECIMAL(10,2))"
        db?.execSQL(createSpojeniTable)

        Log.d("Database", "Table created")


        val createKoupenaJizdenkaTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_KOUPENA_JIZDENKA (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_SPOJENI_ID INTEGER," +
                    "$COL_ODKUD VARCHAR(255)," + // Nový sloupec pro odkud
                    "$COL_KAM VARCHAR(255)," +   // Nový sloupec pro kam
                    "$COL_CAS_OD DATETIME," +
                    "$COL_CAS_DO DATETIME," +
                    "$COL_VEHICLE VARCHAR(255)," +
                    "$COL_CENA DECIMAL(10,2)," +
                    "FOREIGN KEY ($COL_SPOJENI_ID) REFERENCES $TABLE_SPOJENI($COL_ID))"
        db?.execSQL(createKoupenaJizdenkaTable)
        //xpolia05
        val createCreditsTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_CREDITS (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_CREDIT_AMOUNT DECIMAL(10,2))"
        db?.execSQL(createCreditsTable)

        Log.d("Database", "Table created")
        //

        val initialValues = ContentValues()
        initialValues.put(COL_CREDIT_AMOUNT, 0.0)
        db?.insert(TABLE_CREDITS, null, initialValues)

    }

    fun insertKoupenaJizdenka(spojeniId: Long, odkud: String, kam: String, casOd: String, casDo: String,vehicle: String, cena: Double): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_SPOJENI_ID, spojeniId)
            put(COL_ODKUD, odkud)
            put(COL_KAM, kam)
            put(COL_CAS_OD, casOd)
            put(COL_CAS_DO, casDo)
            put(COL_VEHICLE, vehicle)
            put(COL_CENA, cena)
        }
        val id = db.insert(TABLE_KOUPENA_JIZDENKA, null, contentValues)
        db.close()
        return id
    }


    fun insertVyhledavani(odkud: String, kam: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_ODKUD, odkud)
            put(COL_KAM, kam)
        }
        val id = db.insert(TABLE_VYHLEDAVANI, null, contentValues)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun displayVyhledavani(): List<String> {
        val vyhledavaniCursor = getAllVyhledavani()
        val historyEntries = mutableSetOf<String>() // Použijeme Set pro uchování unikátních položek
        while (vyhledavaniCursor.moveToNext()) {
            val odkudValue = vyhledavaniCursor.getString(vyhledavaniCursor.getColumnIndex(COL_ODKUD))
            val kamValue = vyhledavaniCursor.getString(vyhledavaniCursor.getColumnIndex(COL_KAM))
            val entry = "$odkudValue -> $kamValue"
            historyEntries.add(entry) // Přidáme pouze unikátní položky
        }
        vyhledavaniCursor.close()
        return historyEntries.toList()
    }

    fun getAllVyhledavani(): Cursor {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_VYHLEDAVANI"
        return db.rawQuery(query, null)
    }


    //xdohna52
    @SuppressLint("Range")
    fun displayFavourite(): List<favouriteListElem>{
        val favouriteCursor = getAllFavourite()
        val favEntries = mutableListOf<favouriteListElem>()
        while (favouriteCursor.moveToNext()){
            val fromValue = favouriteCursor.getString(favouriteCursor.getColumnIndex(COL_ODKUD))
            val toValue = favouriteCursor.getString(favouriteCursor.getColumnIndex(COL_KAM))
            val text = "$fromValue->$toValue"
            val id = favouriteCursor.getInt(favouriteCursor.getColumnIndex(COL_ID))
            val entry = favouriteListElem(text,id)
            favEntries.add(entry)
        }
        favouriteCursor.close()
        return favEntries.toList()
    }

    fun getAllFavourite(): Cursor{
        val  db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FAVOURITE"
        return  db.rawQuery(query, null)
    }

    fun insertFavourite(odkud: String, kam: String) : Long{
        val db = this.writableDatabase
        val selection = "$COL_ODKUD =? AND $COL_KAM =?"
        val selectionargs = arrayOf(odkud,kam)

        val cursor = db.query(TABLE_FAVOURITE, null, selection, selectionargs, null, null, null)
        val count = cursor.count
        cursor.close()

        if(count == 0){
            val insert = ContentValues()
            insert.put(COL_ODKUD,"$odkud")
            insert.put(COL_KAM, "$kam")
            val result = db?.insert(TABLE_FAVOURITE, null, insert)
            db.close()
            return result!!
        }else{
            db.close()
            return -1
        }
    }

    fun deleteFavourite(entryId : Int):Boolean{
        val db = this.writableDatabase
        val where = "$COL_ID = ?"
        val whereArgs = arrayOf(entryId.toString())
        val result = db.delete(TABLE_FAVOURITE, where, whereArgs)
        return  result !=-1
    }
    //konec xdohna52

    @SuppressLint("Range")
    fun getKoupenaJizdenkaData(vararg ids: Int): List<Pair<Int, SpojeniData>> {
        val purchasedTickets = mutableListOf<Pair<Int, SpojeniData>>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_KOUPENA_JIZDENKA"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COL_ID))
            // Pokud jsou zadaná ID a aktuální řádek v databázi nemá ID v seznamu zadaných ID, přeskočte tento řádek.
            if (ids.isNotEmpty() && !ids.contains(id)) {
                continue
            }

            val odkud = cursor.getString(cursor.getColumnIndex(COL_ODKUD))
            val kam = cursor.getString(cursor.getColumnIndex(COL_KAM))
            val casOd = cursor.getString(cursor.getColumnIndex(COL_CAS_OD))
            val casDo = cursor.getString(cursor.getColumnIndex(COL_CAS_DO))
            val vehicle = cursor.getString(cursor.getColumnIndex(COL_VEHICLE))
            val cena = cursor.getDouble(cursor.getColumnIndex(COL_CENA))

            val purchasedTicket = SpojeniData(odkud, kam, casOd, casDo, vehicle, cena)
            purchasedTickets.add(Pair(id, purchasedTicket))
        }

        cursor.close()
        db.close()

        return purchasedTickets
    }


    fun deleteKoupenaJizdenkaByCasOd(casOd: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_KOUPENA_JIZDENKA, "$COL_CAS_OD <= ?", arrayOf(casOd))
    }

    fun deleteKoupenaJizdenkaById(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_KOUPENA_JIZDENKA, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun insertInitialSpojeniData() {

        val db = this.writableDatabase
        onCreate(db)

        val spojeniData = arrayOf(
            SpojeniData("Praha", "Brno", "2023-12-11 10:00", "2023-11-21 12:00","Tram1", 250.0),
            SpojeniData("semilasso", "husitska", "2023-11-22 09:30", "2023-11-22 11:15","Tram1", 180.0),
            SpojeniData("Ostrava", "Olomouc", "2024-11-22 09:30", "2024-11-22 11:15","Tram1", 180.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 22:30", "2023-12-12 24:00","Tram1", 180.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 19:47", "2023-12-12 24:00","Tram1", 80.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 17:47", "2023-12-12 24:00","Tram1", 70.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 15:47", "2023-12-12 24:00","Tram1", 60.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 13:47", "2023-12-12 24:00","Tram1", 50.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 11:47", "2023-12-12 24:00","Tram1", 40.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 10:47", "2023-12-12 24:00","Tram1", 30.0),
            SpojeniData("Hlavní nádraží", "Semilasso", "2024-12-12 5:47", "2023-12-12 24:00","Tram1", 20.0),

            SpojeniData("Plzeň", "České Budějovice", "2023-11-23 15:45", "2023-11-23 18:30","Tram1", 300.00)
            // Přidávejte další počáteční data podle potřeby
        )

        for (data in spojeniData) {
            val contentValues = ContentValues().apply {
                put(COL_ODKUD, data.odkud)
                put(COL_KAM, data.kam)
                put(COL_CAS_OD, data.casOd)
                put(COL_CAS_DO, data.casDo)
                put(COL_VEHICLE, data.vehicle)
                put(COL_CENA, data.cena)
            }
            db.insert(TABLE_SPOJENI, null, contentValues)
        }

        db.close()
    }

    fun getSpojeniById(spojeniId: Long): Cursor {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SPOJENI WHERE $COL_ID = ?"
        return db.rawQuery(query, arrayOf(spojeniId.toString()))
    }

    fun getSpojeniByOdkudKam(odkud: String, kam: String): Cursor {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SPOJENI WHERE $COL_ODKUD = ? AND $COL_KAM = ?"
        return db.rawQuery(query, arrayOf(odkud, kam))
    }

    fun deleteAllSpojeni() {
        val db = this.writableDatabase
        db.delete(TABLE_SPOJENI, null, null)
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_KOUPENA_JIZDENKA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SPOJENI")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CREDITS")
        onCreate(db)
    }

    fun resetDatabase() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KOUPENA_JIZDENKA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SPOJENI")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CREDITS")
        onCreate(db)
        db.close()
    }

    // Insert a new credit amount
    //xpolia05
    fun insertCredit(creditAmount: Double): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_CREDIT_AMOUNT, creditAmount)
        }
        val id = db.insert(TABLE_CREDITS, null, contentValues)
        Log.d(TAG, "Inserted new credit with ID: $id and amount: $creditAmount")
        db.close()
        return id
    }

    // Update the credit amount
    fun updateCredit(creditAmount: Double): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_CREDIT_AMOUNT, creditAmount)
        }
        val success = db.update(TABLE_CREDITS, contentValues, "$COL_ID = ?", arrayOf("1"))
        if (success == 1) {
            Log.d(TAG, "Successfully updated credit with new amount: $creditAmount")
        } else {
            Log.e(TAG, "Failed to update credit. No rows affected.")
        }
        db.close()
        return success
    }

    // Get the total credit amount
    @SuppressLint("Range")
    fun getTotalCredit(): Double {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_CREDITS, arrayOf(COL_CREDIT_AMOUNT), null, null, null, null, null)
        var totalCredit = 0.0
        if (cursor.moveToFirst()) {
            do {
                totalCredit += cursor.getDouble(cursor.getColumnIndex(COL_CREDIT_AMOUNT))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        Log.d(TAG, "Fetched total credit: $totalCredit")
        return totalCredit
    }
    //konec xpolia05
}