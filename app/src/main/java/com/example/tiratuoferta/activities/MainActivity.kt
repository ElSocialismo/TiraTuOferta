package com.example.tiratuoferta.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // Si el usuario ya está autenticado, redirige a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
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
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}
