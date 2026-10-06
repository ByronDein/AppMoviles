package com.example.conectasenas.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.conectasenas.Mensaje
import com.example.conectasenas.MensajeServicio
import com.example.conectasenas.Validaciones
import java.util.Locale

// Pantalla para escribir un mensaje y que el celular lo diga en voz alta.
// Los mensajes se guardan en Firebase (CRUD completo).
@Composable
fun EscribirScreen(
    uid: String,
    mensajeServicio: MensajeServicio,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current

    var textoEscrito by rememberSaveable { mutableStateOf("") }
    var idMensajeEditando by rememberSaveable { mutableStateOf("") }
    var mensajeEstado by rememberSaveable { mutableStateOf("") }
    var listaMensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }

    // Motor que convierte el texto en voz
    var motorVoz by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var motorCreado: TextToSpeech? = null
        motorCreado = TextToSpeech(contexto) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                motorCreado?.language = Locale.forLanguageTag("es-CL")
            }
        }
        motorVoz = motorCreado

        onDispose {
            motorCreado?.stop()
            motorCreado?.shutdown()
        }
    }

    fun cargarMensajes() {
        mensajeServicio.obtenerMensajes(uid, "escrito") { lista ->
            listaMensajes = lista
        }
    }

    fun decirEnVozAlta(texto: String) {
        motorVoz?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "mensaje_escrito")
    }

    LaunchedEffect(Unit) {
        cargarMensajes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Escribir",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Escribe lo que quieres decir y presiona \"Decir en voz alta\".")

        OutlinedTextField(
            value = textoEscrito,
            onValueChange = { textoNuevo ->
                textoEscrito = textoNuevo
            },
            label = { Text("Tu mensaje") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (Validaciones.textoNoVacio(textoEscrito)) {
                    decirEnVozAlta(textoEscrito)
                } else {
                    mensajeEstado = "Escribe un mensaje primero."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Decir en voz alta")
        }

        // Si hay un id es porque se está editando un mensaje que ya existe
        if (idMensajeEditando.isEmpty()) {
            OutlinedButton(
                onClick = {
                    if (!Validaciones.textoNoVacio(textoEscrito)) {
                        mensajeEstado = "Escribe un mensaje primero."
                    } else {
                        val mensajeNuevo = Mensaje(
                            texto = textoEscrito.trim(),
                            tipo = "escrito",
                            fecha = System.currentTimeMillis()
                        )
                        mensajeServicio.crearMensaje(uid, mensajeNuevo) { guardado ->
                            if (guardado) {
                                mensajeEstado = "Mensaje guardado."
                                textoEscrito = ""
                                cargarMensajes()
                            } else {
                                mensajeEstado = "No se pudo guardar el mensaje."
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar mensaje")
            }
        } else {
            OutlinedButton(
                onClick = {
                    if (!Validaciones.textoNoVacio(textoEscrito)) {
                        mensajeEstado = "El mensaje no puede quedar vacío."
                    } else {
                        val mensajeEditado = Mensaje(
                            id = idMensajeEditando,
                            texto = textoEscrito.trim(),
                            tipo = "escrito",
                            fecha = System.currentTimeMillis()
                        )
                        mensajeServicio.actualizarMensaje(uid, mensajeEditado) { guardado ->
                            if (guardado) {
                                mensajeEstado = "Mensaje actualizado."
                                textoEscrito = ""
                                idMensajeEditando = ""
                                cargarMensajes()
                            } else {
                                mensajeEstado = "No se pudo actualizar el mensaje."
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            TextButton(
                onClick = {
                    textoEscrito = ""
                    idMensajeEditando = ""
                }
            ) {
                Text("Cancelar edición")
            }
        }

        if (mensajeEstado.isNotBlank()) {
            Text(
                text = mensajeEstado,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Mensajes guardados",
            style = MaterialTheme.typography.titleLarge
        )

        if (listaMensajes.isEmpty()) {
            Text("Todavía no hay mensajes guardados.")
        }

        listaMensajes.forEach { mensajeActual ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = mensajeActual.texto,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { decirEnVozAlta(mensajeActual.texto) }) {
                            Text("Decir")
                        }

                        TextButton(
                            onClick = {
                                textoEscrito = mensajeActual.texto
                                idMensajeEditando = mensajeActual.id
                            }
                        ) {
                            Text("Editar")
                        }

                        TextButton(
                            onClick = {
                                mensajeServicio.eliminarMensaje(uid, mensajeActual.id) { eliminado ->
                                    if (eliminado) {
                                        mensajeEstado = "Mensaje eliminado."
                                        cargarMensajes()
                                    } else {
                                        mensajeEstado = "No se pudo eliminar el mensaje."
                                    }
                                }
                            }
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }

        TextButton(onClick = alVolver) {
            Text("Volver al menú")
        }
    }
}
