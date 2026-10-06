package com.example.conectasenas.screens

import android.content.ActivityNotFoundException
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conectasenas.Mensaje
import com.example.conectasenas.MensajeServicio
import com.example.conectasenas.Validaciones

// Pantalla para escuchar a otra persona y mostrar lo que dice como texto grande.
// Usa el reconocimiento de voz de Google que viene en el celular.
@Composable
fun HablarScreen(
    uid: String,
    mensajeServicio: MensajeServicio,
    alVolver: () -> Unit
) {
    var textoReconocido by rememberSaveable { mutableStateOf("") }
    var idMensajeEditando by rememberSaveable { mutableStateOf("") }
    var mensajeEstado by rememberSaveable { mutableStateOf("") }
    var listaMensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }

    fun cargarMensajes() {
        mensajeServicio.obtenerMensajes(uid, "hablado") { lista ->
            listaMensajes = lista
        }
    }

    LaunchedEffect(Unit) {
        cargarMensajes()
    }

    // Aquí llega lo que el reconocimiento de voz entendió
    val lanzadorVoz = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val textos = resultado.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val primerTexto = textos?.firstOrNull()
            if (primerTexto != null) {
                textoReconocido = primerTexto
                idMensajeEditando = ""
                mensajeEstado = ""
            } else {
                mensajeEstado = "No se entendió nada, intenta otra vez."
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
            text = "Hablar",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Presiona el botón y acerca el celular a quien está hablando.")

        Button(
            onClick = {
                val intentVoz = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intentVoz.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intentVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
                intentVoz.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora")

                try {
                    lanzadorVoz.launch(intentVoz)
                } catch (error: ActivityNotFoundException) {
                    mensajeEstado = "Este celular no tiene reconocimiento de voz."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Escuchar")
        }

        // Texto grande para leerlo fácilmente
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Lo que se escuchó:",
                    style = MaterialTheme.typography.titleMedium
                )

                if (textoReconocido.isEmpty()) {
                    Text("Aquí aparecerá el texto.")
                } else {
                    Text(
                        text = textoReconocido,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        // Se puede corregir el texto antes de guardarlo
        OutlinedTextField(
            value = textoReconocido,
            onValueChange = { textoNuevo ->
                textoReconocido = textoNuevo
            },
            label = { Text("Corregir texto") },
            modifier = Modifier.fillMaxWidth()
        )

        if (idMensajeEditando.isEmpty()) {
            OutlinedButton(
                onClick = {
                    if (!Validaciones.textoNoVacio(textoReconocido)) {
                        mensajeEstado = "No hay texto para guardar."
                    } else {
                        val mensajeNuevo = Mensaje(
                            texto = textoReconocido.trim(),
                            tipo = "hablado",
                            fecha = System.currentTimeMillis()
                        )
                        mensajeServicio.crearMensaje(uid, mensajeNuevo) { guardado ->
                            if (guardado) {
                                mensajeEstado = "Texto guardado."
                                textoReconocido = ""
                                cargarMensajes()
                            } else {
                                mensajeEstado = "No se pudo guardar el texto."
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar texto")
            }
        } else {
            OutlinedButton(
                onClick = {
                    if (!Validaciones.textoNoVacio(textoReconocido)) {
                        mensajeEstado = "El texto no puede quedar vacío."
                    } else {
                        val mensajeEditado = Mensaje(
                            id = idMensajeEditando,
                            texto = textoReconocido.trim(),
                            tipo = "hablado",
                            fecha = System.currentTimeMillis()
                        )
                        mensajeServicio.actualizarMensaje(uid, mensajeEditado) { guardado ->
                            if (guardado) {
                                mensajeEstado = "Texto actualizado."
                                textoReconocido = ""
                                idMensajeEditando = ""
                                cargarMensajes()
                            } else {
                                mensajeEstado = "No se pudo actualizar el texto."
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
                    textoReconocido = ""
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
            text = "Textos guardados",
            style = MaterialTheme.typography.titleLarge
        )

        if (listaMensajes.isEmpty()) {
            Text("Todavía no hay textos guardados.")
        }

        listaMensajes.forEach { mensajeActual ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = mensajeActual.texto,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = {
                                textoReconocido = mensajeActual.texto
                                idMensajeEditando = mensajeActual.id
                            }
                        ) {
                            Text("Editar")
                        }

                        TextButton(
                            onClick = {
                                mensajeServicio.eliminarMensaje(uid, mensajeActual.id) { eliminado ->
                                    if (eliminado) {
                                        mensajeEstado = "Texto eliminado."
                                        cargarMensajes()
                                    } else {
                                        mensajeEstado = "No se pudo eliminar el texto."
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
