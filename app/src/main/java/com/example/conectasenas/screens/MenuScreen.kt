package com.example.conectasenas.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    nombreUsuario: String,
    alIrAEscribir: () -> Unit,
    alIrAHablar: () -> Unit,
    alIrABuscarDispositivo: () -> Unit,
    alIrAPerfil: () -> Unit,
    alIrAAyuda: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    // Cuando se abre la pantalla, el contenido aparece con una animación
    var contenidoVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contenidoVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Menú principal",
            style = MaterialTheme.typography.headlineLarge
        )

        // Saludo con el nombre del usuario
        AnimatedVisibility(
            visible = contenidoVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            if (nombreUsuario.isBlank()) {
                Text(
                    text = "¡Hola! ¿Qué quieres hacer hoy?",
                    style = MaterialTheme.typography.titleLarge
                )
            } else {
                Text(
                    text = "¡Hola, $nombreUsuario! ¿Qué quieres hacer hoy?",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        AnimatedVisibility(
            visible = contenidoVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = alIrAEscribir,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                ) {
                    Text("Escribir (la app lo dice en voz alta)")
                }

                Button(
                    onClick = alIrAHablar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                ) {
                    Text("Hablar (la app convierte la voz en texto)")
                }

                Button(
                    onClick = alIrABuscarDispositivo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                ) {
                    Text("Buscar dispositivo")
                }

                OutlinedButton(
                    onClick = alIrAAyuda,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ayuda (tutorial de uso)")
                }

                OutlinedButton(
                    onClick = alIrAPerfil,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mi perfil")
                }

                OutlinedButton(
                    onClick = alCerrarSesion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
