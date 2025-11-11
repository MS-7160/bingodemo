package local.ms7160.bingodemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * BingoDatabaseHelper - SQLite database management class for Bingo game records
 * 
 * This class handles all database operations for storing Bingo game history.
 * Each game round is saved with the following information:
 * - Username: Player who generated the card
 * - Round Number: Sequential counter for each game session
 * - Five Bingo Numbers: The drawn numbers for verification
 * - System Time: Timestamp when the card was generated
 * 
 * Database Schema:
 * Table Name: bingo_records
 * Columns:
 *   - id (INTEGER PRIMARY KEY AUTOINCREMENT): Unique identifier for each record
 *   - username (TEXT): Name of the player
 *   - round_number (INTEGER): Game round number (starts from 1, increments per user)
 *   - number1 to number5 (INTEGER): Five randomly selected Bingo numbers
 *   - system_time (TEXT): Timestamp in "yyyy-MM-dd HH:mm:ss" format
 */
class BingoDatabaseHelper(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database configuration constants
        private const val DATABASE_NAME = "BingoGame.db"  // Name of the SQLite database file
        private const val DATABASE_VERSION = 1             // Database schema version number
        
        // Table and column names for bingo_records table
        private const val TABLE_NAME = "bingo_records"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_ROUND_NUMBER = "round_number"
        private const val COLUMN_NUMBER1 = "number1"
        private const val COLUMN_NUMBER2 = "number2"
        private const val COLUMN_NUMBER3 = "number3"
        private const val COLUMN_NUMBER4 = "number4"
        private const val COLUMN_NUMBER5 = "number5"
        private const val COLUMN_SYSTEM_TIME = "system_time"
    }

    /**
     * onCreate - Called when the database is created for the first time
     * Creates the bingo_records table with all required columns
     * 
     * @param db The database instance to create tables in
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // SQL statement to create the bingo_records table
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_ROUND_NUMBER INTEGER NOT NULL,
                $COLUMN_NUMBER1 INTEGER NOT NULL,
                $COLUMN_NUMBER2 INTEGER NOT NULL,
                $COLUMN_NUMBER3 INTEGER NOT NULL,
                $COLUMN_NUMBER4 INTEGER NOT NULL,
                $COLUMN_NUMBER5 INTEGER NOT NULL,
                $COLUMN_SYSTEM_TIME TEXT NOT NULL
            )
        """.trimIndent()
        
        // Execute the SQL statement to create the table
        db?.execSQL(createTableQuery)
    }

    /**
     * onUpgrade - Called when the database version is upgraded
     * Drops the existing table and recreates it with the new schema
     * 
     * @param db The database instance
     * @param oldVersion Previous version number
     * @param newVersion New version number
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop the old table if it exists
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        // Recreate the table with the new schema
        onCreate(db)
    }

    /**
     * insertBingoRecord - Saves a new Bingo game record to the database
     * 
     * This method stores all relevant game information including the player's username,
     * the current round number, five randomly selected Bingo numbers, and the current
     * system timestamp.
     * 
     * @param username Name of the player
     * @param roundNumber Current game round number
     * @param numbers List of 5 integers representing the Bingo numbers for this round
     * @return Row ID of the newly inserted record, or -1 if insertion failed
     */
    fun insertBingoRecord(username: String, roundNumber: Int, numbers: List<Int>): Long {
        // Get writable database instance for insert operations
        val db = this.writableDatabase
        
        // Create ContentValues object to hold column-value pairs
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_ROUND_NUMBER, roundNumber)
            put(COLUMN_NUMBER1, numbers[0])
            put(COLUMN_NUMBER2, numbers[1])
            put(COLUMN_NUMBER3, numbers[2])
            put(COLUMN_NUMBER4, numbers[3])
            put(COLUMN_NUMBER5, numbers[4])
            // Format current time as "yyyy-MM-dd HH:mm:ss" and store as text
            put(COLUMN_SYSTEM_TIME, getCurrentTimestamp())
        }
        
        // Insert the record into the database and return the row ID
        return db.insert(TABLE_NAME, null, values)
    }

    /**
     * getLatestRoundNumber - Retrieves the most recent round number for a specific user
     * 
     * This method queries the database to find the highest round number associated
     * with the given username. Used to determine the next round number for a new game.
     * 
     * @param username Name of the player to query
     * @return The latest round number for this user, or 0 if no records exist
     */
    fun getLatestRoundNumber(username: String): Int {
        // Get readable database instance for query operations
        val db = this.readableDatabase
        var latestRound = 0
        
        // SQL query to get the maximum round number for the specified username
        val query = """
            SELECT MAX($COLUMN_ROUND_NUMBER) as max_round 
            FROM $TABLE_NAME 
            WHERE $COLUMN_USERNAME = ?
        """.trimIndent()
        
        // Execute the query with username as parameter to prevent SQL injection
        val cursor: Cursor = db.rawQuery(query, arrayOf(username))
        
        // Process the query result
        cursor.use {
            if (it.moveToFirst()) {
                // Get the maximum round number from the result
                // Returns 0 if NULL (no records found)
                latestRound = it.getInt(0)
            }
        }
        
        return latestRound
    }

    /**
     * getAllRecords - Retrieves all Bingo game records from the database
     * 
     * Returns a list of all game records stored in the database, ordered by
     * the record ID in descending order (newest first).
     * 
     * @return List of BingoRecord objects containing all stored game data
     */
    @Suppress("unused")
    fun getAllRecords(): List<BingoRecord> {
        val records = mutableListOf<BingoRecord>()
        val db = this.readableDatabase
        
        // Query all records, ordered by ID descending (newest first)
        val cursor = db.query(
            TABLE_NAME,
            null,  // Select all columns
            null,  // No WHERE clause
            null,  // No WHERE arguments
            null,  // No GROUP BY
            null,  // No HAVING
            "$COLUMN_ID DESC"  // ORDER BY clause
        )
        
        // Iterate through all rows in the result set
        cursor.use {
            while (it.moveToNext()) {
                // Extract data from each column and create BingoRecord object
                val record = BingoRecord(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    username = it.getString(it.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    roundNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_ROUND_NUMBER)),
                    number1 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER1)),
                    number2 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER2)),
                    number3 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER3)),
                    number4 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER4)),
                    number5 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER5)),
                    systemTime = it.getString(it.getColumnIndexOrThrow(COLUMN_SYSTEM_TIME))
                )
                records.add(record)
            }
        }
        
        return records
    }

    /**
     * getRecordsByUsername - Retrieves all game records for a specific user
     * 
     * @param username Name of the player to query
     * @return List of BingoRecord objects for the specified user, ordered by round number descending
     */
    @Suppress("unused")
    fun getRecordsByUsername(username: String): List<BingoRecord> {
        val records = mutableListOf<BingoRecord>()
        val db = this.readableDatabase
        
        // Query records filtered by username
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_USERNAME = ?",  // WHERE clause
            arrayOf(username),        // WHERE arguments
            null,
            null,
            "$COLUMN_ROUND_NUMBER DESC"  // ORDER BY clause
        )
        
        // Process query results
        cursor.use {
            while (it.moveToNext()) {
                val record = BingoRecord(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    username = it.getString(it.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    roundNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_ROUND_NUMBER)),
                    number1 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER1)),
                    number2 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER2)),
                    number3 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER3)),
                    number4 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER4)),
                    number5 = it.getInt(it.getColumnIndexOrThrow(COLUMN_NUMBER5)),
                    systemTime = it.getString(it.getColumnIndexOrThrow(COLUMN_SYSTEM_TIME))
                )
                records.add(record)
            }
        }
        
        return records
    }

    /**
     * getCurrentTimestamp - Generates a formatted timestamp string for the current date/time
     * 
     * @return Current date and time in "yyyy-MM-dd HH:mm:ss" format
     */
    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

/**
 * BingoRecord - Data class representing a single Bingo game record
 * 
 * This class encapsulates all information about one game session:
 * - Database record ID
 * - Player username
 * - Game round number
 * - Five Bingo numbers drawn in that round
 * - Timestamp when the record was created
 * 
 * @property id Unique database record identifier
 * @property username Player name
 * @property roundNumber Game round sequence number
 * @property number1 First Bingo number
 * @property number2 Second Bingo number
 * @property number3 Third Bingo number
 * @property number4 Fourth Bingo number
 * @property number5 Fifth Bingo number
 * @property systemTime Timestamp in "yyyy-MM-dd HH:mm:ss" format
 */
data class BingoRecord(
    val id: Int,
    val username: String,
    val roundNumber: Int,
    val number1: Int,
    val number2: Int,
    val number3: Int,
    val number4: Int,
    val number5: Int,
    val systemTime: String
)
