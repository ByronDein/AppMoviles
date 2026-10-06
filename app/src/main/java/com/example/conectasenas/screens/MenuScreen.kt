package com.example.conectasenas.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    alIrAEscribir: () -> Unit,
    alIrAHablar: () -> Unit,
    alIrABuscarDispositivo: () -> Unit,
    alIrAPerfil: () -> Unit,
    alCerrarSesion: () -> Unit
) {
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

        Text("¿Qué quieres hacer hoy?")

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
