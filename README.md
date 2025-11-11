# Bingo Game Android Application

A fully-featured Android Bingo game application with user authentication, SQLite database integration, and interactive gameplay.

## 📱 Features

### 1. User Authentication System
- **Login Screen**: Secure user authentication with username and password
- **Default Credentials**: 
  - Username: `User`
  - Password: `password`
- **Credential Management**: Change username and password through the configuration screen
- **SharedPreferences**: User credentials are stored locally using Android SharedPreferences

### 2. Interactive Bingo Game
- **5x5 Bingo Card**: Standard Bingo grid following traditional B-I-N-G-O column rules
- **Column Number Ranges**:
  - **B** (Column 1): Numbers 1-15
  - **I** (Column 2): Numbers 16-30
  - **N** (Column 3): Numbers 31-45 (with FREE space in center)
  - **G** (Column 4): Numbers 46-60
  - **O** (Column 5): Numbers 61-75
- **FREE Space**: The center cell (row 2, column 2) is always marked as "FREE"
- **Interactive Cells**: Tap any cell to toggle between selected/unselected states
- **Visual Feedback**: Selected cells display a highlighted background
- **Random Generation**: Generate new random cards with the "Generate New Card" button

### 3. SQLite Database Integration
The application implements a comprehensive SQLite database to track game history with the following schema:

#### Database Table: `bingo_records`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER PRIMARY KEY | Auto-incrementing unique identifier |
| `username` | TEXT | Player's username |
| `round_number` | INTEGER | Sequential game round number (per user) |
| `number1` | INTEGER | First drawn Bingo number |
| `number2` | INTEGER | Second drawn Bingo number |
| `number3` | INTEGER | Third drawn Bingo number |
| `number4` | INTEGER | Fourth drawn Bingo number |
| `number5` | INTEGER | Fifth drawn Bingo number |
| `system_time` | TEXT | Timestamp in "yyyy-MM-dd HH:mm:ss" format |

#### Database Operations
- **Automatic Saving**: Each new Bingo card is automatically saved to the database
- **Round Tracking**: Round numbers increment automatically for each player
- **Five Bingo Numbers**: Five representative numbers (one from each column) are saved per game
- **Timestamp Recording**: System time is recorded for every game session
- **User-Specific Records**: Each player's game history is tracked separately

## 🏗️ Project Structure

### Activities

#### 1. LoginActivity (`LoginActivity.kt`)
- **Purpose**: First screen shown when app launches; handles user authentication
- **Features**:
  - Username and password input fields
  - Credential verification against SharedPreferences
  - Error dialog for incorrect credentials
  - Navigation to MainActivity on successful login
- **Layout**: `activity_login.xml`

#### 2. MainActivity (`MainActivity.kt`)
- **Purpose**: Main game screen displaying the interactive Bingo card
- **Features**:
  - 5x5 Bingo grid with 25 interactive cells
  - Player info and round number display
  - Cell selection/deselection with visual feedback
  - New card generation with database integration
  - SQLite database operations for saving game records
  - Configuration button to access settings
- **Layout**: `activity_main.xml`
- **Key Methods**:
  - `generateBingoCard()`: Creates random Bingo card and saves to database
  - `toggleCell()`: Handles cell selection/deselection
  - `clearSelections()`: Resets all cell selections
  - `updateRoundInfo()`: Updates player and round number display

#### 3. ConfigActivity (`ConfigActivity.kt`)
- **Purpose**: Configuration screen for changing login credentials
- **Features**:
  - Verification of existing credentials
  - Input fields for new username and password
  - Validation logic with error messages
  - SharedPreferences update on successful change
  - Automatic return to MainActivity after 2 seconds
- **Layout**: `activity_config.xml`
- **Validation Rules**:
  - Existing credentials must match saved values
  - New credentials cannot be empty
  - Appropriate dialogs for each validation scenario

### Database Helper

#### BingoDatabaseHelper (`BingoDatabaseHelper.kt`)
- **Purpose**: Manages all SQLite database operations for the Bingo game
- **Key Methods**:
  - `onCreate()`: Creates the database table structure
  - `insertBingoRecord()`: Saves a new game record with all required attributes
  - `getLatestRoundNumber()`: Retrieves the most recent round number for a user
  - `getAllRecords()`: Fetches all game records from the database
  - `getRecordsByUsername()`: Retrieves game history for a specific player
  - `getCurrentTimestamp()`: Generates formatted timestamp strings

#### BingoRecord (Data Class)
- **Purpose**: Represents a single Bingo game record
- **Properties**: id, username, roundNumber, number1-5, systemTime
- **Usage**: Encapsulates database query results for easy data manipulation

## 📐 Layout Files

### activity_main.xml
- **Structure**: ConstraintLayout with nested LinearLayouts for grid organization
- **Components**:
  - MaterialToolbar: App title bar
  - Title TextView: "BINGO CARD" heading
  - Round Info TextView: Displays "Player: [name] | Round: [number]"
  - Bingo Grid: 6 rows (1 header + 5 data rows) × 5 columns
  - Action Button: "Generate New Card" button
  - Config Button: "Change Password" button (purple background)
- **Cell IDs**: Pattern `cell_[row]_[col]` (e.g., `cell_0_0`, `cell_2_3`)

### activity_login.xml
- **Structure**: Vertical LinearLayout with centered content
- **Components**:
  - Welcome message TextView
  - Username label and EditText (with autofill hints)
  - Password label and EditText (masked input with autofill hints)
  - Enter button for credential submission

### activity_config.xml
- **Structure**: Vertical LinearLayout with form-style layout
- **Components**:
  - Configuration title TextView
  - Existing username/password fields (for verification)
  - New username/password fields (for updates)
  - Confirm button to save changes

## 🎮 How to Use

### First Time Setup
1. **Launch the app** - LoginActivity appears
2. **Enter default credentials**:
   - Username: `User`
   - Password: `password`
3. **Tap "Enter"** to proceed to the main game

### Playing Bingo
1. **View your card** - A random Bingo card is automatically generated
2. **Check round info** - See your username and current round number at the top
3. **Mark cells** - Tap any cell to mark it (except the FREE space)
4. **Generate new card** - Tap "Generate New Card" to create a fresh card
   - Previous selections are cleared
   - New card is saved to database
   - Round number increments
5. **All game data is automatically saved** to the SQLite database

### Changing Credentials
1. **Tap "Change Password"** button on main screen
2. **Enter existing credentials** for verification
3. **Enter new username and password**
4. **Tap "Confirm"** to save changes
5. **Wait 2 seconds** - Automatically returns to MainActivity
6. **Next login** will require the new credentials

## 💾 Database Records

Each time you generate a new Bingo card, the following information is saved:

- **Username**: Your current player name
- **Round Number**: Sequential counter (1, 2, 3, ...)
- **Five Bingo Numbers**: Representative numbers from each column (B-I-N-G-O)
- **System Time**: Exact timestamp when the card was created

Example record:
```
Username: User
Round Number: 5
Numbers: 7, 23, 38, 52, 69
System Time: 2025-11-11 14:30:45
```

## 🔧 Technical Details

### Technologies Used
- **Language**: Kotlin
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 36
- **Architecture**: Activity-based with SQLite database
- **UI Components**: Material Design components, ConstraintLayout, LinearLayout
- **Data Persistence**: SQLite database and SharedPreferences

### Dependencies
- AndroidX Core KTX
- AndroidX AppCompat
- Material Components for Android
- AndroidX Activity
- AndroidX ConstraintLayout

### Code Style
- **Comprehensive Comments**: Every class, method, and complex logic block is documented
- **KDoc Format**: Professional documentation following Kotlin standards
- **Inline Comments**: Explaining key logic steps and decisions
- **XML Comments**: Detailed layout structure and component descriptions

## 📊 Game Flow

```
LoginActivity (Authentication)
       ↓
   [Success]
       ↓
MainActivity (Bingo Game)
       ↓
   [Generate Card] → Save to SQLite Database
       ↓
   [Change Password] → ConfigActivity
       ↓
   [Update Credentials] → Return to MainActivity
```

## 🗄️ Data Persistence

### SharedPreferences (`login.xml`)
- **Purpose**: Store user login credentials
- **Keys**: `username`, `password`
- **Access Mode**: MODE_PRIVATE (app-only access)

### SQLite Database (`BingoGame.db`)
- **Purpose**: Store complete game history
- **Table**: `bingo_records`
- **Location**: App's private database directory
- **Persistence**: Data survives app restarts and updates

## 🎯 Key Features Summary

✅ User authentication with customizable credentials  
✅ Interactive 5x5 Bingo card with traditional column rules  
✅ FREE space in center (always marked)  
✅ Cell selection/deselection with visual feedback  
✅ Random card generation following Bingo standards  
✅ SQLite database integration for game history  
✅ Round number tracking per player  
✅ Five Bingo numbers saved per game  
✅ Timestamp recording for each session  
✅ Comprehensive code comments throughout  
✅ Material Design UI with edge-to-edge display  
✅ Secure credential storage with SharedPreferences  

## 👨‍💻 Developer Notes

### Code Organization
- Each activity has a single responsibility
- Database operations are encapsulated in `BingoDatabaseHelper`
- Data classes are used for type-safe database records
- All strings are externalized to `strings.xml` for localization
- Dimensions are defined in `dimens.xml` for consistent styling

### Comment Style
Following the style established in the original `MainActivity.kt`:
- **Class-level comments**: Describe purpose and key features
- **Method-level comments**: Explain what the method does, parameters, and return values
- **Inline comments**: Clarify complex logic and important steps
- **XML comments**: Document layout structure and component purposes

### Database Design Decisions
- **Five numbers instead of all 25**: Represents "drawn" numbers for game verification
- **Round number per user**: Allows multiple players on same device
- **Timestamp as TEXT**: Human-readable format for easy debugging
- **Auto-increment ID**: Ensures unique record identification

## 📝 License

This project is part of an Android development educational assignment demonstrating:
- SQLite database integration
- User authentication systems
- Interactive UI components
- Data persistence strategies
- Professional code documentation practices

---

**Developed with ❤️ for Android Application Development**
