package com.example.conectasenas.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.conectasenas.Dispositivo
import com.example.conectasenas.DispositivoServicio
import com.example.conectasenas.Validaciones
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// Pantalla para guardar la ubicación de un dispositivo (por ejemplo un audífono)
// y poder volver a encontrarlo después. Usa la geolocalización del celular.
@SuppressLint("MissingPermission")
@Composable
fun BuscarDispositivoScreen(
    uid: String,
    dispositivoServicio: DispositivoServicio,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current

    var nombreDispositivo by rememberSaveable { mutableStateOf("") }
    var latitudActual by rememberSaveable { mutableStateOf(0.0) }
    var longitudActual by rememberSaveable { mutableStateOf(0.0) }
    var tengoUbicacion by rememberSaveable { mutableStateOf(false) }
    var mensajeEstado by rememberSaveable { mutableStateOf("") }
    var listaDispositivos by remember { mutableStateOf<List<Dispositivo>>(emptyList()) }

    fun cargarDispositivos() {
        dispositivoServicio.obtenerDispositivos(uid) { lista ->
            listaDispositivos = lista
        }
    }

    LaunchedEffect(Unit) {
        cargarDispositivos()
    }

    fun obtenerUbicacion() {
        mensajeEstado = "Buscando tu ubicación..."
        val clienteUbicacion = LocationServices.getFusedLocationProviderClient(contexto)

        clienteUbicacion.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion ->
                if (ubicacion != null) {
                    latitudActual = ubicacion.latitude
                    longitudActual = ubicacion.longitude
                    tengoUbicacion = true
                    mensajeEstado = "Ubicación obtenida."
                } else {
                    mensajeEstado = "No se pudo obtener la ubicación. Activa el GPS."
                }
            }
            .addOnFailureListener {
                mensajeEstado = "Error al obtener la ubicación."
            }
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            obtenerUbicacion()
        } else {
            mensajeEstado = "Sin permiso de ubicación no se puede buscar el dispositivo."
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
            text = "Buscar dispositivo",
            style = MaterialTheme.typography.headlineLarge
        )

        Text("Guarda dónde dejaste un dispositivo para encontrarlo después.")

        Button(
            onClick = {
                val permiso = ContextCompat.checkSelfPermission(
                    contexto,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (permiso == PackageManager.PERMISSION_GRANTED) {
                    obtenerUbicacion()
                } else {
                    lanzadorPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obtener mi ubicación")
        }

        if (tengoUbicacion) {
            Text("Latitud: $latitudActual")
            Text("Longitud: $longitudActual")
        }

        OutlinedTextField(
            value = nombreDispositivo,
            onValueChange = { nombreNuevo ->
                nombreDispositivo = nombreNuevo
            },
            label = { Text("Nombre del dispositivo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = {
                if (!Validaciones.textoNoVacio(nombreDispositivo)) {
                    mensajeEstado = "Escribe el nombre del dispositivo."
                } else if (!tengoUbicacion) {
                    mensajeEstado = "Primero obtén tu ubicación."
                } else {
                    val dispositivoNuevo = Dispositivo(
                        nombre = nombreDispositivo.trim(),
                        latitud = latitudActual,
                        longitud = longitudActual,
                        fecha = System.currentTimeMillis()
                    )
                    dispositivoServicio.crearDispositivo(uid, dispositivoNuevo) { guardado ->
                        if (guardado) {
                            mensajeEstado = "Dispositivo guardado."
                            nombreDispositivo = ""
                            cargarDispositivos()
                        } else {
                            mensajeEstado = "No se pudo guardar el dispositivo."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar dispositivo")
        }

        if (mensajeEstado.isNotBlank()) {
            Text(
                text = mensajeEstado,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Dispositivos guardados",
            style = MaterialTheme.typography.titleLarge
        )

        if (listaDispositivos.isEmpty()) {
            Text("Todavía no hay dispositivos guardados.")
        }

        listaDispositivos.forEach { dispositivoActual ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = dispositivoActual.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Latitud: ${dispositivoActual.latitud}")
                    Text("Longitud: ${dispositivoActual.longitud}")

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = {
                                val direccion = Uri.parse(
                                    "geo:${dispositivoActual.latitud},${dispositivoActual.longitud}" +
                                        "?q=${dispositivoActual.latitud},${dispositivoActual.longitud}" +
                                        "(${Uri.encode(dispositivoActual.nombre)})"
                                )
                                try {
                                    contexto.startActivity(Intent(Intent.ACTION_VIEW, direccion))
                                } catch (error: ActivityNotFoundException) {
                                    mensajeEstado = "No hay una app de mapas instalada."
                                }
                            }
                        ) {
                            Text("Ver en mapa")
                        }

                        TextButton(
                            onClick = {
                                if (!tengoUbicacion) {
                                    mensajeEstado = "Primero obtén tu ubicación."
                                } else {
                                    val dispositivoActualizado = Dispositivo(
                                        id = dispositivoActual.id,
                                        nombre = dispositivoActual.nombre,
                                        latitud = latitudActual,
                                        longitud = longitudActual,
                                        fecha = System.currentTimeMillis()
                                    )
                                    dispositivoServicio.actualizarUbicacion(uid, dispositivoActualizado) { guardado ->
                                        if (guardado) {
                                            mensajeEstado = "Ubicación actualizada."
                                            cargarDispositivos()
                                        } else {
                                            mensajeEstado = "No se pudo actualizar."
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Actualizar")
                        }

                        TextButton(
                            onClick = {
                                dispositivoServicio.eliminarDispositivo(uid, dispositivoActual.id) { eliminado ->
                                    if (eliminado) {
                                        mensajeEstado = "Dispositivo eliminado."
                                        cargarDispositivos()
                                    } else {
                                        mensajeEstado = "No se pudo eliminar."
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
