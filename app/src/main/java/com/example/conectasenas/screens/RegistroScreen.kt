package com.example.conectasenas.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.conectasenas.Usuario
import com.example.conectasenas.ResultadoRegistro

@Composable
fun RegistroScreen(
    listaUsuarios: List<Usuario>,
    alVolverAlLogin: () -> Unit,
    alRegistrarUsuario: (Usuario) -> ResultadoRegistro
) {
    var nombreEscrito by rememberSaveable { mutableStateOf("") }
    var correoEscrito by rememberSaveable { mutableStateOf("") }
    var contrasenaEscrita by rememberSaveable { mutableStateOf("") }

    var regionSeleccionada by rememberSaveable {
        mutableStateOf("Seleccionar región")
    }

    var medioComunicacionSeleccionado by rememberSaveable {
        mutableStateOf("Texto")
    }

    var menuRegionesAbierto by rememberSaveable { mutableStateOf(false) }

    var usarTextosClaros by rememberSaveable { mutableStateOf(true) }
    var usarAlertasVisuales by rememberSaveable { mutableStateOf(true) }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    var mensajeRegistro by rememberSaveable { mutableStateOf("") }

    val listaRegiones = listOf(
        "Metropolitana",
        "Valparaíso",
        "Biobío",
        "Otra"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Completa los datos para registrar un usuario.")

        OutlinedTextField(
            value = nombreEscrito,
            onValueChange = { nombreNuevo ->
                nombreEscrito = nombreNuevo
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correoEscrito,
            onValueChange = { correoNuevo ->
                correoEscrito = correoNuevo
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contrasenaEscrita,
            onValueChange = { contrasenaNueva ->
                contrasenaEscrita = contrasenaNueva
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Región (combo box)")

        Box {
            OutlinedButton(
                onClick = {
                    menuRegionesAbierto = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(regionSeleccionada)
            }

            DropdownMenu(
                expanded = menuRegionesAbierto,
                onDismissRequest = {
                    menuRegionesAbierto = false
                }
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = medioComunicacionSeleccionado == "Texto",
                onClick = {
                    medioComunicacionSeleccionado = "Texto"
                }
            )
            Text("Texto")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = medioComunicacionSeleccionado == "Videollamada",
                onClick = {
                    medioComunicacionSeleccionado = "Videollamada"
                }
            )
            Text("Videollamada")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = medioComunicacionSeleccionado == "Vibración",
                onClick = {
                    medioComunicacionSeleccionado = "Vibración"
                }
            )
            Text("Vibración")
        }

        Text("Checklist de accesibilidad")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = usarTextosClaros,
                onCheckedChange = { valorCheckbox ->
                    usarTextosClaros = valorCheckbox
                }
            )
            Text("Usar textos claros")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = usarAlertasVisuales,
                onCheckedChange = { valorCheckbox ->
                    usarAlertasVisuales = valorCheckbox
                }
            )
            Text("Usar alertas visuales")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { valorCheckbox ->
                    aceptaTerminos = valorCheckbox
                }
            )
            Text("Acepto términos y condiciones")
        }

        Text("Beneficios de la aplicación (grilla)")

        Row(modifier = Modifier.fillMaxWidth()) {
            Card(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mensajes\nclaros",
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Card(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alertas\nvisuales",
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Button(
            onClick = {
                if (nombreEscrito.isBlank() ||
                    correoEscrito.isBlank() ||
                    contrasenaEscrita.isBlank()
                ) {
                    mensajeRegistro = "Completa nombre, correo y contraseña."
                } else if (regionSeleccionada == "Seleccionar región") {
                    mensajeRegistro = "Selecciona una región."
                } else if (!aceptaTerminos) {
                    mensajeRegistro = "Debes aceptar los términos y condiciones."
                } else {
                    val usuarioNuevo = Usuario(
                        nombre = nombreEscrito,
                        correo = correoEscrito,
                        contrasena = contrasenaEscrita,
                        region = regionSeleccionada,
                        medioComunicacion = medioComunicacionSeleccionado
                    )

                    when (alRegistrarUsuario(usuarioNuevo)) {
                        ResultadoRegistro.REGISTRO_CORRECTO -> {
                            mensajeRegistro = "Usuario registrado correctamente."

                            nombreEscrito = ""
                            correoEscrito = ""
                            contrasenaEscrita = ""
                        }
                        ResultadoRegistro.LIMITE_ALCANZADO -> {
                            mensajeRegistro = "Solo se permite registrar 5 usuarios."
                        }
                        ResultadoRegistro.CORREO_REPETIDO -> {
                            mensajeRegistro = "Ese correo ya está registrado."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar usuario")
        }

        if (mensajeRegistro.isNotBlank()) {
            Text(
                text = mensajeRegistro,
                color = MaterialTheme.colorScheme.primary
            )
        }

        TablaDeUsuarios(listaUsuarios = listaUsuarios)

        TextButton(onClick = alVolverAlLogin) {
            Text("Volver a iniciar sesión")
        }
    }
}

@Composable
fun TablaDeUsuarios(listaUsuarios: List<Usuario>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Usuarios registrados: ${listaUsuarios.size}/5")

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nombre",
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Correo",
                    modifier = Modifier.weight(1f)
                )
            }

            listaUsuarios.forEach { usuarioActual ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = usuarioActual.nombre,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = usuarioActual.correo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
