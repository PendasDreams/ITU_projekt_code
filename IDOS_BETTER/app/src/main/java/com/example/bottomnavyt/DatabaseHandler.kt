import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.bottomnavyt.History_obj
import android.database.Cursor


const val TAG = "DataBaseHandler"

val DATABASE_NAME = "MyDB"
val TABLE_NAME = "History"
val COL_CAS_OD = "CasOd"
val COL_CAS_DO = "CasDo"
val COL_MISTO_OD = "MistoOd"
val COL_MISTO_DO = "MistoDo"
val COL_CENA = "Cena"

val TABLE_CREDITS = "Credits"
val COL_CREDIT_AMOUNT = "CreditAmount"
val COL_ID = "ID"

val TABLE_SPOJENI = "Spojeni"
val COL_ODKUD = "Odkud"
val COL_KAM = "Kam"

class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COL_CAS_OD DATETIME," +
                    "$COL_CAS_DO DATETIME," +
                    "$COL_MISTO_OD VARCHAR(255)," +
                    "$COL_MISTO_DO VARCHAR(255)," +
                    "$COL_CENA DECIMAL(10,2))"
        db?.execSQL(createTable)

        val createSpojeniTable =
            "CREATE TABLE $TABLE_SPOJENI (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_ODKUD VARCHAR(255)," +
                    "$COL_KAM VARCHAR(255))"
        db?.execSQL(createSpojeniTable)

        val createCreditsTable =
            "CREATE TABLE $TABLE_CREDITS (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_CREDIT_AMOUNT DECIMAL(10,2))"
        db?.execSQL(createCreditsTable)

        val initialValues = ContentValues()
        initialValues.put(COL_CREDIT_AMOUNT, 0.0)
        db?.insert(TABLE_CREDITS, null, initialValues)
    }

    fun insertSpojeni(odkud: String, kam: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_ODKUD, odkud)
            put(COL_KAM, kam)
        }
        val id = db.insert(TABLE_SPOJENI, null, contentValues)
        db.close()
        return id
    }

    fun getAllSpojeni(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_SPOJENI, null, null, null, null, null, null)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CREDITS")
        onCreate(db)

        // Zde můžete přidat kód pro vložení dat do nově vytvořené tabulky.
        if (newVersion > oldVersion) {
            insertInitialData(db)
        }
    }

    // Insert a new credit amount
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

    private fun insertInitialData(db: SQLiteDatabase?) {
        val contentValues = ContentValues()
        contentValues.put(COL_CAS_OD, "DatumCasOd")
        contentValues.put(COL_CAS_DO, "DatumCasDo")
        contentValues.put(COL_MISTO_OD, "MistoOd")
        contentValues.put(COL_MISTO_DO, "MistoDo")
        contentValues.put(COL_CENA, 100.00) // Příklad ceny

        db?.insert(TABLE_NAME, null, contentValues)
    }

    // Metoda pro vkládání dat do databáze
    fun insertData(data: History_obj): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CAS_OD, data.casOd?.toString()) // Předpokládáme, že casOd je String
        contentValues.put(COL_CAS_DO, data.casDo?.toString()) // Předpokládáme, že casDo je String
        contentValues.put(COL_MISTO_OD, data.mistoOd)
        contentValues.put(COL_MISTO_DO, data.mistoDo)
        contentValues.put(COL_CENA, data.cena.toDouble()) // Převést cenu na Double

        val insertedId = db.insert(TABLE_NAME, null, contentValues)
        db.close()

        return insertedId
    }
}