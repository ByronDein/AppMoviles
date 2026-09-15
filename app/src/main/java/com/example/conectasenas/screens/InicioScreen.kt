package com.example.conectasenas.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conectasenas.Usuario

@Composable
fun InicioScreen(
    listaUsuariosRegistrados: List<Usuario>,
    alCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Inicio de sesión correcto",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Estos son los usuarios registrados en ConectaSeñas.")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Usuarios registrados: ${listaUsuariosRegistrados.size}/5")

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Nombre", modifier = Modifier.weight(1f))
                    Text("Correo", modifier = Modifier.weight(1f))
                }

                listaUsuariosRegistrados.forEach { usuarioRegistrado ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = usuarioRegistrado.nombre,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = usuarioRegistrado.correo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Button(
            onClick = alCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
