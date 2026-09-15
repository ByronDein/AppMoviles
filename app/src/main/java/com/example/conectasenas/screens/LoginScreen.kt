package com.example.conectasenas.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    alIniciarSesion: (String, String) -> Boolean,
    alIniciarSesionCorrectamente: () -> Unit,
    alPresionarCrearCuenta: () -> Unit,
    alPresionarRecuperarContrasena: () -> Unit
) {
    var correoEscrito by rememberSaveable { mutableStateOf("") }
    var contrasenaEscrita by rememberSaveable { mutableStateOf("") }
    var recordarSesion by rememberSaveable { mutableStateOf(false) }
    var mensajeLogin by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ConectaSeñas",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Comunicación accesible, clara y visual.")

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = correoEscrito,
            onValueChange = { correoNuevo ->
                correoEscrito = correoNuevo
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasenaEscrita,
            onValueChange = { contrasenaNueva ->
                contrasenaEscrita = contrasenaNueva
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = recordarSesion,
                onCheckedChange = { valorCheckbox ->
                    recordarSesion = valorCheckbox
                }
            )

            Text("Recordar sesión")
        }

        Button(
            onClick = {
                if (alIniciarSesion(correoEscrito, contrasenaEscrita)) {
                    mensajeLogin = "Inicio de sesión correcto."
                    alIniciarSesionCorrectamente()
                } else {
                    mensajeLogin = "Correo o contraseña incorrectos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        if (mensajeLogin.isNotBlank()) {
            Text(
                text = mensajeLogin,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        TextButton(onClick = alPresionarRecuperarContrasena) {
            Text("¿Olvidaste tu contraseña?")
        }

        TextButton(onClick = alPresionarCrearCuenta) {
            Text("Crear una cuenta")
        }
    }
}
