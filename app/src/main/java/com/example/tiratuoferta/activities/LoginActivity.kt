package com.example.tiratuoferta.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tiratuoferta.ui.theme.TiraTuOfertaTheme
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Usuario ya autenticado, redirigir al HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Mostrar pantalla de login
            setContent {
                TiraTuOfertaTheme {
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
