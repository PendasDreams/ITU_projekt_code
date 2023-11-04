import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.bottomnavyt.History_obj



val DATABASE_NAME = "MyDB"
val TABLE_NAME = "History"
val COL_CAS_OD = "CasOd"
val COL_CAS_DO = "CasDo"
val COL_MISTO_OD = "MistoOd"
val COL_MISTO_DO = "MistoDo"
val COL_CENA = "Cena"

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
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)

        // Zde můžete přidat kód pro vložení dat do nově vytvořené tabulky.
        if (newVersion > oldVersion) {
            insertInitialData(db)
        }
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
