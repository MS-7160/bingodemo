package local.ms7160.bingodemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

/**
 * LoginActivity - The login screen of the Bingo game application
 * This activity handles user authentication using SharedPreferences
 * Default credentials are username="User" and password="password"
 */
class LoginActivity : AppCompatActivity() {

    // EditText for entering username
    private lateinit var usernameEditText: EditText
    
    // EditText for entering password
    private lateinit var passwordEditText: EditText
    
    // Button to submit login credentials
    private lateinit var enterButton: Button

    /**
     * onCreate - Called when the activity is first created
     * Initializes the login UI and sets up SharedPreferences with default credentials
     * if they don't exist yet
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Connect UI elements to their corresponding views
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        enterButton = findViewById(R.id.enterButton)

        // Get SharedPreferences instance with name "login.xml"
        val sharedPreferences = getSharedPreferences("login.xml", MODE_PRIVATE)
        
        // If this is the first run, initialize with default credentials
        if (!sharedPreferences.contains("username")) {
            sharedPreferences.edit {
                putString("username", "User")
                putString("password", "password")
            }
        }

        // Set up click listener for the Enter button to validate credentials
        enterButton.setOnClickListener {
            // Get entered credentials from EditText fields
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            // Retrieve saved credentials from SharedPreferences
            val savedUsername = sharedPreferences.getString("username", "User")
            val savedPassword = sharedPreferences.getString("password", "password")

            // Check if entered credentials match saved credentials
            if (enteredUsername == savedUsername && enteredPassword == savedPassword) {
                // Login successful - navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Login failed - show error dialog
                showErrorDialog()
            }
        }
    }

    /**
     * showErrorDialog - Displays an error dialog when login credentials are incorrect
     * Shows the message "Your data is incorrect!! Enter again"
     */
    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Login Failed")
        builder.setMessage("Your data is incorrect!! Enter again")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}
