package com.example.conectasenas.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conectasenas.Usuario
import com.example.conectasenas.UsuarioRepository

// Pantalla para ver, editar y eliminar los datos del usuario (R, U y D del CRUD).
@Composable
fun PerfilScreen(
    repositorioUsuarios: UsuarioRepository,
    alVolver: () -> Unit,
    alEliminarCuenta: () -> Unit
) {
    var uidUsuario by rememberSaveable { mutableStateOf("") }
    var correoUsuario by rememberSaveable { mutableStateOf("") }
    var nombreEscrito by rememberSaveable { mutableStateOf("") }
    var regionSeleccionada by rememberSaveable { mutableStateOf("Metropolitana") }
    var medioSeleccionado by rememberSaveable { mutableStateOf("Texto") }
    var menuRegionesAbierto by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    var mensajePerfil by rememberSaveable { mutableStateOf("Cargando datos...") }

    val listaRegiones = listOf("Metropolitana", "Valparaíso", "Biobío", "Otra")
    val listaMedios = listOf("Texto", "Videollamada", "Vibración")

    // Al abrir la pantalla se leen los datos desde Firebase
    LaunchedEffect(Unit) {
        repositorioUsuarios.obtenerUsuarioActual { usuario ->
            if (usuario != null) {
                uidUsuario = usuario.uid
                correoUsuario = usuario.correo
                nombreEscrito = usuario.nombre
                regionSeleccionada = usuario.region
                medioSeleccionado = usuario.medioComunicacion
                mensajePerfil = ""
            } else {
                mensajePerfil = "No se pudieron cargar tus datos."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Correo: $correoUsuario")

        OutlinedTextField(
            value = nombreEscrito,
            onValueChange = { nombreNuevo ->
                nombreEscrito = nombreNuevo
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Región")

        Box {
            OutlinedButton(
                onClick = { menuRegionesAbierto = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(regionSeleccionada)
            }

            DropdownMenu(
                expanded = menuRegionesAbierto,
                onDismissRequest = { menuRegionesAbierto = false }
            ) {
                listaRegiones.forEach { regionActual ->
                    DropdownMenuItem(
                        text = { Text(regionActual) },
                        onClick = {
                            regionSeleccionada = regionActual
                            menuRegionesAbierto = false
                        }
                    )
                }
            }
        }

        Text("Medio de comunicación preferido")

        listaMedios.forEach { medioActual ->
            OutlinedButton(
                onClick = { medioSeleccionado = medioActual },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (medioSeleccionado == medioActual) {
                    Text("✔ $medioActual")
                } else {
                    Text(medioActual)
                }
            }
        }

        Button(
            onClick = {
                val usuarioEditado = Usuario(
                    uid = uidUsuario,
                    nombre = nombreEscrito.trim(),
                    correo = correoUsuario,
                    region = regionSeleccionada,
                    medioComunicacion = medioSeleccionado
                )
                repositorioUsuarios.actualizarUsuario(usuarioEditado) { guardado ->
                    if (guardado) {
                        mensajePerfil = "Datos actualizados."
                    } else {
                        mensajePerfil = "No se pudieron guardar los cambios."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        OutlinedButton(
            onClick = { mostrarConfirmacion = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar mi cuenta")
        }

        if (mensajePerfil.isNotBlank()) {
            Text(
                text = mensajePerfil,
                color = MaterialTheme.colorScheme.primary
            )
        }

        TextButton(onClick = alVolver) {
            Text("Volver al menú")
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("Se borrarán tus datos y tu cuenta. ¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        repositorioUsuarios.eliminarCuenta { eliminada ->
                            if (eliminada) {
                                alEliminarCuenta()
                            } else {
                                mensajePerfil =
                                    "No se pudo eliminar. Cierra sesión, vuelve a entrar e intenta de nuevo."
                            }
                        }
                    }
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
