package com.example.conectasenas.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Tutorial de uso. Cada paso se abre y se cierra al tocarlo.
@Composable
fun AyudaScreen(alVolver: () -> Unit) {
    var pasoAbierto by rememberSaveable { mutableStateOf(0) }

    val titulos = listOf(
        "1. Iniciar sesión",
        "2. Escribir",
        "3. Hablar",
        "4. Buscar dispositivo",
        "5. Mi perfil"
    )

    val explicaciones = listOf(
        "Escribe tu correo y tu contraseña. Si no tienes cuenta, toca \"Crear una cuenta\". " +
            "Si olvidaste la clave, toca \"¿Olvidaste tu contraseña?\" y te llegará un correo.",
        "Escribe lo que quieres decir y toca \"Decir en voz alta\". El celular lo dirá por ti. " +
            "Puedes guardar el mensaje, editarlo o eliminarlo.",
        "Toca \"Escuchar\" y acerca el celular a quien habla. Lo que diga aparecerá como texto " +
            "grande para que lo leas. También lo puedes guardar.",
        "Toca \"Obtener mi ubicación\", escribe el nombre del dispositivo y guárdalo. " +
            "Después puedes verlo en el mapa para encontrarlo.",
        "Aquí puedes cambiar tu nombre, tu región y tu medio de comunicación preferido, " +
            "o eliminar tu cuenta."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ayuda",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Toca cada paso para ver cómo se usa la aplicación.")

        titulos.forEachIndexed { posicion, titulo ->
            Card(
                onClick = {
                    // Si ya estaba abierto se cierra
                    pasoAbierto = if (pasoAbierto == posicion) -1 else posicion
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (pasoAbierto == posicion) {
                        Text(
                            text = explicaciones[posicion],
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        TextButton(onClick = alVolver) {
            Text("Volver al menú")
        }
    }
}
