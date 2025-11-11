package local.ms7160.bingodemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.appbar.MaterialToolbar

/**
 * MainActivity - The main screen of the Bingo game application
 * 
 * This activity creates and manages a 5x5 Bingo card with interactive cells.
 * Features:
 * - Generates random Bingo cards following standard Bingo rules (B-I-N-G-O columns)
 * - Allows users to mark/unmark cells by clicking
 * - Tracks game rounds and saves each card to SQLite database
 * - Stores username, round number, five Bingo numbers, and timestamp for each game
 * - Provides configuration button to change login credentials
 * 
 * Database Integration:
 * - Each time a new card is generated, it's saved to the database
 * - Five random numbers (one from each column) are selected as the "drawn" numbers
 * - Round numbers increment automatically for each user
 * - All game history is preserved in SQLite for future reference
 */
class MainActivity : AppCompatActivity() {
    
    // 2D array to hold references to all 25 TextViews that make up the Bingo grid
    private lateinit var bingoGrid: Array<Array<TextView?>>
    
    // Button to generate a new Bingo card
    private lateinit var actionButton: Button
    
    // Button for configuration
    private lateinit var configButton: Button
    
    // TextView to display current round number
    private lateinit var roundInfoTextView: TextView
    
    // Set to track which cells the user has marked/selected
    // Stores row,col pairs like (0,1), (2,3), etc.
    private val selectedCells = mutableSetOf<Pair<Int, Int>>()
    
    // Database helper instance for SQLite operations
    private lateinit var databaseHelper: BingoDatabaseHelper
    
    // Current username from SharedPreferences
    private var currentUsername: String = "User"
    
    // Current round number (starts at 1, increments with each new card)
    private var currentRound: Int = 1
    
    /**
     * onCreate - Called when the activity is first created
     * 
     * Initialization sequence:
     * 1. Sets up edge-to-edge display and status bar appearance
     * 2. Initializes the database helper
     * 3. Retrieves current username from SharedPreferences
     * 4. Loads the latest round number from database
     * 5. Sets up the toolbar and window insets
     * 6. Initializes the Bingo grid and click listeners
     * 7. Generates the first Bingo card
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display to draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContentView(R.layout.activity_main)
        
        // Configure status bar to show dark icons (for light background)
        // This makes status bar icons visible against the white background
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        
        // Initialize database helper for SQLite operations
        databaseHelper = BingoDatabaseHelper(this)
        
        // Get current username from SharedPreferences (set during login)
        val sharedPreferences = getSharedPreferences("login.xml", MODE_PRIVATE)
        currentUsername = sharedPreferences.getString("username", "User") ?: "User"
        
        // Load the latest round number from database and increment for new game
        currentRound = databaseHelper.getLatestRoundNumber(currentUsername) + 1
        
        // Set up the toolbar at the top of the screen
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        // Handle window insets to add padding for system bars (status bar and navigation bar)
        // This prevents content from being hidden behind system UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize the grid by connecting to the TextViews in the layout
        initializeGrid()
        
        // Set up click handlers for each cell and the action button
        setupClickListeners()
        
        // Generate random numbers for the first Bingo card and save to database
        generateBingoCard()
    }
    
    /**
     * initializeGrid - Creates a 5x5 array and links each element to a TextView from the layout
     * 
     * This method connects the logical grid structure (2D array) to the actual UI elements
     * (TextViews) defined in activity_main.xml. Each cell position (row, col) corresponds
     * to a TextView with ID pattern: cell_(row)_(col)
     * 
     * Also initializes:
     * - Action button for generating new cards
     * - Config button for changing credentials
     * - Round info text view for displaying current round number
     */
    private fun initializeGrid() {
        // Create a 5x5 array to hold TextView references
        bingoGrid = Array(5) { arrayOfNulls(5) }
        
        // Row 0 - Link each array position to its corresponding TextView in the layout
        bingoGrid[0][0] = findViewById(R.id.cell_0_0)
        bingoGrid[0][1] = findViewById(R.id.cell_0_1)
        bingoGrid[0][2] = findViewById(R.id.cell_0_2)
        bingoGrid[0][3] = findViewById(R.id.cell_0_3)
        bingoGrid[0][4] = findViewById(R.id.cell_0_4)
        
        // Row 1
        bingoGrid[1][0] = findViewById(R.id.cell_1_0)
        bingoGrid[1][1] = findViewById(R.id.cell_1_1)
        bingoGrid[1][2] = findViewById(R.id.cell_1_2)
        bingoGrid[1][3] = findViewById(R.id.cell_1_3)
        bingoGrid[1][4] = findViewById(R.id.cell_1_4)
        
        // Row 2 (middle row - contains the FREE space at position `[2][2]`)
        bingoGrid[2][0] = findViewById(R.id.cell_2_0)
        bingoGrid[2][1] = findViewById(R.id.cell_2_1)
        bingoGrid[2][2] = findViewById(R.id.cell_2_2)
        bingoGrid[2][3] = findViewById(R.id.cell_2_3)
        bingoGrid[2][4] = findViewById(R.id.cell_2_4)
        
        // Row 3
        bingoGrid[3][0] = findViewById(R.id.cell_3_0)
        bingoGrid[3][1] = findViewById(R.id.cell_3_1)
        bingoGrid[3][2] = findViewById(R.id.cell_3_2)
        bingoGrid[3][3] = findViewById(R.id.cell_3_3)
        bingoGrid[3][4] = findViewById(R.id.cell_3_4)
        
        // Row 4 (bottom row)
        bingoGrid[4][0] = findViewById(R.id.cell_4_0)
        bingoGrid[4][1] = findViewById(R.id.cell_4_1)
        bingoGrid[4][2] = findViewById(R.id.cell_4_2)
        bingoGrid[4][3] = findViewById(R.id.cell_4_3)
        bingoGrid[4][4] = findViewById(R.id.cell_4_4)
        
        // Get reference to the "New Card" button
        actionButton = findViewById(R.id.actionButton)
        
        // Get reference to the configuration button
        configButton = findViewById(R.id.configButton)
        
        // Get reference to the round info text view
        roundInfoTextView = findViewById(R.id.roundInfoTextView)
        
        // Update the round info display
        updateRoundInfo()
    }
    
    /**
     * setupClickListeners - Attaches click handlers to all cells and buttons
     * 
     * Cell Click Behavior:
     * - Each cell (except FREE space) toggles between selected/unselected when clicked
     * - Selected cells show a highlighted background
     * - Unselected cells show a normal border
     * 
     * Button Behaviors:
     * - Action Button: Generates a new card, saves to database, increments round number
     * - Config Button: Opens ConfigActivity for credential management
     */
    private fun setupClickListeners() {
        // Loop through all 25 cells in the grid
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                // Skip the center cell `[2][2]` - it's the FREE space and shouldn't be clickable
                if (row == 2 && col == 2) continue
                
                // Make each cell toggle its selection state when clicked
                bingoGrid[row][col]?.setOnClickListener {
                    toggleCell(row, col)
                }
            }
        }
        
        // Set up the "New Card" button to generate a fresh card and clear all marks
        // Also saves the new card to database and increments round number
        actionButton.setOnClickListener {
            generateBingoCard()
            clearSelections()
            
            // Show confirmation toast message
            Toast.makeText(
                this,
                "New card generated for Round $currentRound",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        // Set up the configuration button to navigate to ConfigActivity
        configButton.setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }
    }
    
    /**
     * toggleCell - Marks or unmarks a cell when the user clicks it
     * 
     * This method provides visual feedback for user interactions by changing
     * the cell's background drawable. Marked cells use bingo_cell_selected background,
     * while unmarked cells use bingo_cell_border background.
     * 
     * @param row Row index (0-4)
     * @param col Column index (0-4)
     */
    private fun toggleCell(row: Int, col: Int) {
        // Get the TextView for this cell, return if it doesn't exist
        val cell = bingoGrid[row][col] ?: return
        val position = Pair(row, col)
        
        // Check if this cell is already marked
        if (selectedCells.contains(position)) {
            // Cell is marked - unmark it
            selectedCells.remove(position)
            cell.setBackgroundResource(R.drawable.bingo_cell_border)
        } else {
            // Cell is not marked - mark it
            selectedCells.add(position)
            cell.setBackgroundResource(R.drawable.bingo_cell_selected)
        }
    }
    
    /**
     * clearSelections - Removes all marks from the Bingo card
     * 
     * Resets all cells (except FREE space) to their default unselected state.
     * Called when generating a new card to provide a clean slate.
     */
    private fun clearSelections() {
        // Clear the set that tracks selected cells
        selectedCells.clear()
        
        // Loop through all cells and reset their background
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                // Don't change the FREE space in the center
                if (row != 2 || col != 2) {
                    bingoGrid[row][col]?.setBackgroundResource(R.drawable.bingo_cell_border)
                }
            }
        }
    }
    
    /**
     * updateRoundInfo - Updates the round information display
     * 
     * Shows the current username and round number at the top of the screen.
     * Format: "Player: (username) | Round: (number)"
     */
    private fun updateRoundInfo() {
        roundInfoTextView.text = getString(R.string.round_info, currentUsername, currentRound)
    }
    
    /**
     * generateBingoCard - Creates a new random Bingo card following standard Bingo rules
     * 
     * Standard Bingo Column Rules:
     * - B (column 0): numbers 1-15
     * - I (column 1): numbers 16-30
     * - N (column 2): numbers 31-45
     * - G (column 3): numbers 46-60
     * - O (column 4): numbers 61-75
     * 
     * The center cell `[2][2]` is always FREE and displays "FREE"
     * 
     * Database Integration:
     * After generating the card, this method:
     * 1. Selects 5 random numbers (one from each column) as the "drawn" numbers
     * 2. Saves the game record to SQLite database with:
     *    - Current username
     *    - Current round number
     *    - The five drawn numbers
     *    - Current system timestamp
     * 3. Increments the round number for the next game
     * 4. Updates the round info display
     */
    private fun generateBingoCard() {
        // Define the number ranges for each column (B-I-N-G-O)
        val ranges = listOf(
            1..15,   // B column - leftmost
            16..30,  // I column
            31..45,  // N column - middle
            46..60,  // G column
            61..75   // O column - rightmost
        )
        
        // List to store the five "drawn" Bingo numbers (one per column)
        val drawnNumbers = mutableListOf<Int>()
        
        // Process each column
        for (col in 0 until 5) {
            // Shuffle the numbers in this column's range and pick 5 random ones
            val numbers = ranges[col].shuffled().take(5).toMutableList()
            
            // Select one number from this column as a "drawn" number for database
            // Use the middle row (index 2) to ensure variety
            drawnNumbers.add(numbers[2])
            
            // Fill each row in this column with the random numbers
            for (row in 0 until 5) {
                // Skip the center FREE space
                if (row == 2 && col == 2) {
                    continue
                }
                
                // Set the cell's text to the random number
                bingoGrid[row][col]?.text = numbers[row].toString()
            }
        }
        
        // Save this Bingo card to the database with all required information
        val recordId = databaseHelper.insertBingoRecord(
            username = currentUsername,
            roundNumber = currentRound,
            numbers = drawnNumbers
        )
        
        // Check if database insertion was successful
        if (recordId != -1L) {
            // Success - increment round number for next game
            currentRound++
            updateRoundInfo()
        } else {
            // Failure - show error message to user
            Toast.makeText(
                this,
                "Error saving game record to database",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}