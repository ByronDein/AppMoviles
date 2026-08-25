package com.example.conectasenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.conectasenas.screens.LoginScreen
import com.example.conectasenas.screens.RecuperarScreen
import com.example.conectasenas.screens.RegistroScreen
import com.example.conectasenas.ui.theme.ConectasenasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ConectasenasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AplicacionConectaSenas()
                }
            }
        }
    }
}

@Composable
fun AplicacionConectaSenas() {
    var pantallaActual by rememberSaveable { mutableStateOf("login") }

    // Esta lista guarda los usuarios mientras la aplicación está abierta.
    val listaUsuarios = remember { mutableStateListOf<Usuario>() }

    when (pantallaActual) {
        "login" -> {
            LoginScreen(
                listaUsuarios = listaUsuarios,
                alPresionarCrearCuenta = {
                    pantallaActual = "registro"
                },
                alPresionarRecuperarContrasena = {
                    pantallaActual = "recuperar"
                }
            )
        }

        "registro" -> {
            RegistroScreen(
                listaUsuarios = listaUsuarios,
                alVolverAlLogin = {
                    pantallaActual = "login"
                },
                alRegistrarUsuario = { usuarioNuevo ->
                    if (listaUsuarios.size < 5) {
                        listaUsuarios.add(usuarioNuevo)
                        true
                    } else {
                        false
                    }
                }
            )
        }

        "recuperar" -> {
            RecuperarScreen(
                listaUsuarios = listaUsuarios,
                alVolverAlLogin = {
                    pantallaActual = "login"
                }
            )
        }
    }
}