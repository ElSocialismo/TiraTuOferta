package com.example.tiratuoferta.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiraTuOfertaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TiraTuOfertaLogin(
                        onRegisterClicked = {
                            val intent = Intent(this, RegisterActivity::class.java)
                            startActivity(intent)
                        },
                        onLoginSuccess = {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Finalizar MainActivity para evitar volver con el botón de retroceso
                        }
                    )
                }
            }
        }
    }
}
