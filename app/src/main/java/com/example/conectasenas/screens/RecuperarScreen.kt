package com.example.conectasenas.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conectasenas.ResultadoRecuperar

@Composable
fun RecuperarScreen(
    alRecuperar: (String, (ResultadoRecuperar) -> Unit) -> Unit,
    alVolverAlLogin: () -> Unit
) {
    var correoEscrito by rememberSaveable { mutableStateOf("") }
    var mensajeRecuperacion by rememberSaveable { mutableStateOf("") }
    var cargando by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Ingresa tu correo y te enviaremos un enlace para crear una contraseña nueva.")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = correoEscrito,
            onValueChange = { correoNuevo ->
                correoEscrito = correoNuevo
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                cargando = true
                mensajeRecuperacion = "Enviando solicitud..."

                alRecuperar(correoEscrito) { resultado ->
                    cargando = false

                    when (resultado) {
                        ResultadoRecuperar.SOLICITUD_ENVIADA -> {
                            mensajeRecuperacion =
                                "Te enviamos un correo para recuperar tu contraseña."
                        }
                        ResultadoRecuperar.CORREO_INVALIDO -> {
                            mensajeRecuperacion = "Escribe un correo válido."
                        }
                        ResultadoRecuperar.CORREO_NO_REGISTRADO -> {
                            mensajeRecuperacion =
                                "No existe un usuario registrado con ese correo."
                        }
                        ResultadoRecuperar.ERROR -> {
                            mensajeRecuperacion = "No se pudo enviar. Revisa tu internet."
                        }
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar solicitud")
        }

        if (mensajeRecuperacion.isNotBlank()) {
            Text(
                text = mensajeRecuperacion,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        TextButton(onClick = alVolverAlLogin) {
            Text("Volver a iniciar sesión")
        }
    }
}
