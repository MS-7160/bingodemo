package local.ms7160.bingodemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

/**
 * ConfigActivity - The configuration screen for changing login credentials
 * This activity allows users to update their username and password
 * Users must verify their existing credentials before making changes
 */
class ConfigActivity : AppCompatActivity() {

    // EditText for entering existing username (for verification)
    private lateinit var existingUsernameEditText: EditText
    
    // EditText for entering existing password (for verification)
    private lateinit var existingPasswordEditText: EditText
    
    // EditText for entering new username
    private lateinit var newUsernameEditText: EditText
    
    // EditText for entering new password
    private lateinit var newPasswordEditText: EditText
    
    // Button to confirm and save the changes
    private lateinit var confirmButton: Button

    /**
     * onCreate - Called when the activity is first created
     * Initializes the configuration UI and sets up the confirm button listener
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        // Connect UI elements to their corresponding views
        existingUsernameEditText = findViewById(R.id.existingUsernameEditText)
        existingPasswordEditText = findViewById(R.id.existingPasswordEditText)
        newUsernameEditText = findViewById(R.id.newUsernameEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmButton = findViewById(R.id.confirmButton)

        // Set up click listener for the Confirm button to process the credential update
        confirmButton.setOnClickListener {
            handleConfirm()
        }
    }

    /**
     * handleConfirm - Validates and processes the credential update request
     * 
     * Performs the following checks:
     * 1. Verifies that existing credentials match the saved credentials
     * 2. Checks that new credentials are not empty
     * 3. Updates SharedPreferences with new credentials if all checks pass
     * 4. Shows appropriate dialog messages based on validation results
     */
    private fun handleConfirm() {
        // Get SharedPreferences instance with name "login.xml"
        val sharedPreferences = getSharedPreferences("login.xml", MODE_PRIVATE)
        
        // Retrieve entered values from all EditText fields
        val existingUsername = existingUsernameEditText.text.toString()
        val existingPassword = existingPasswordEditText.text.toString()
        val newUsername = newUsernameEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()

        // Retrieve saved credentials from SharedPreferences for verification
        val savedUsername = sharedPreferences.getString("username", "User")
        val savedPassword = sharedPreferences.getString("password", "password")

        // Check if existing credentials are correct
        if (existingUsername != savedUsername || existingPassword != savedPassword) {
            // Case A: Existing credentials don't match - show error
            showDialog("Existing username/password is not correct!")
        } else if (newUsername.isEmpty() || newPassword.isEmpty()) {
            // Case B: Existing credentials are correct but new credentials are empty
            showDialog("New username/password is not enter! Data is remind unchanged")
        } else {
            // Case C: All validations passed - save new credentials
            sharedPreferences.edit {
                putString("username", newUsername)
                putString("password", newPassword)
            }
            // Show success message and return to MainActivity after 2 seconds
            showDialogAndReturn()
        }
    }

    /**
     * showDialog - Displays a dialog with the specified message
     * Used for showing error or informational messages
     * 
     * @param message The message to display in the dialog
     */
    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Configuration")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    /**
     * showDialogAndReturn - Displays a success dialog and returns to MainActivity
     * Shows the success message, then waits 2 seconds before navigating back
     * This is called when credentials are successfully updated
     */
    private fun showDialogAndReturn() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Configuration")
        builder.setMessage("New username/password is confirmed! Data is updated")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // Wait 2 seconds before returning to MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
        builder.create().show()
    }
}
