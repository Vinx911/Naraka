package com.vinx911.naraka

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.vinx911.compose.R

class NarakaDefaultErrorActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a = obtainStyledAttributes(androidx.appcompat.R.styleable.AppCompatTheme)
        if (!a.hasValue(androidx.appcompat.R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar)
        }
        a.recycle()

        setContentView(R.layout.naraka_default_error_activity)

        val errorDetails = Naraka.getErrorDetailsFromIntent(this, intent)

        val restartButton = findViewById<Button>(R.id.btn_restart)
        restartButton.setOnClickListener {
            Naraka.restartApplication(this@NarakaDefaultErrorActivity)
        }

        val moreInfoButton = findViewById<Button>(R.id.btn_more_info)
        moreInfoButton.setOnClickListener {
            showErrorDetailsDialog(errorDetails)
        }
    }


    private fun showErrorDetailsDialog(errorDetails: String) {
        AlertDialog.Builder(this@NarakaDefaultErrorActivity).apply {
            this.setTitle(R.string.naraka_error_details)
            this.setMessage(errorDetails)
            this.setPositiveButton(R.string.naraka_close, null)
            this.setNeutralButton(R.string.naraka_copy_to_clipboard) { _, _ ->
                copyErrorToClipboard(errorDetails)
            }
        }.show()
    }

    private fun copyErrorToClipboard(errorDetails: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.naraka_error_details), errorDetails)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, R.string.naraka_copy_to_clipboard, Toast.LENGTH_SHORT).show()
    }

}