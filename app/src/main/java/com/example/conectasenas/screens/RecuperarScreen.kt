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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conectasenas.Usuario

@Composable
fun RecuperarScreen(
    listaUsuarios: List<Usuario>,
    alVolverAlLogin: () -> Unit
) {
    var correoEscrito by rememberSaveable { mutableStateOf("") }
    var metodoSeleccionado by rememberSaveable {
        mutableStateOf("Correo electrónico")
    }
    var mensajeRecuperacion by rememberSaveable { mutableStateOf("") }

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

        Text("Ingresa tu correo para solicitar una recuperación.")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = correoEscrito,
            onValueChange = { correoNuevo ->
                correoEscrito = correoNuevo
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = metodoSeleccionado == "Correo electrónico",
                onClick = {
                    metodoSeleccionado = "Correo electrónico"
                }
            )
            Text("Correo electrónico")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = metodoSeleccionado == "Mensaje de texto",
                onClick = {
                    metodoSeleccionado = "Mensaje de texto"
                }
            )
            Text("Mensaje de texto")
        }

        Button(
            onClick = {
                val usuarioEncontrado = listaUsuarios.find { usuario ->
                    usuario.correo == correoEscrito
                }

                if (usuarioEncontrado != null) {
                    mensajeRecuperacion =
                        "Solicitud enviada por $metodoSeleccionado."
                } else {
                    mensajeRecuperacion =
                        "No existe un usuario registrado con ese correo."
                }
            },
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