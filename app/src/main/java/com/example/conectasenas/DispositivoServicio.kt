package com.example.conectasenas

import com.google.firebase.firestore.FirebaseFirestore

// CRUD de los dispositivos de la pantalla Buscar dispositivo.
// Se guardan en usuarios/{uid}/dispositivos
class DispositivoServicio {

    private val baseDeDatos: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // CREATE
    fun crearDispositivo(uid: String, dispositivo: Dispositivo, alTerminar: (Boolean) -> Unit) {
        val datos = hashMapOf(
            "nombre" to dispositivo.nombre,
            "latitud" to dispositivo.latitud,
            "longitud" to dispositivo.longitud,
            "fecha" to dispositivo.fecha
        )
        baseDeDatos.collection("usuarios").document(uid)
            .collection("dispositivos")
            .add(datos)
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }

    // READ
    fun obtenerDispositivos(uid: String, alTerminar: (List<Dispositivo>) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .collection("dispositivos")
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.map { documento ->
                    Dispositivo(
                        id = documento.id,
                        nombre = documento.getString("nombre") ?: "",
                        latitud = documento.getDouble("latitud") ?: 0.0,
                        longitud = documento.getDouble("longitud") ?: 0.0,
                        fecha = documento.getLong("fecha") ?: 0L
                    )
                }
                alTerminar(lista.sortedByDescending { it.fecha })
            }
            .addOnFailureListener { alTerminar(emptyList()) }
    }

    // UPDATE: cambia la ubicación guardada
    fun actualizarUbicacion(uid: String, dispositivo: Dispositivo, alTerminar: (Boolean) -> Unit) {
        val cambios = hashMapOf<String, Any>(
            "latitud" to dispositivo.latitud,
            "longitud" to dispositivo.longitud,
            "fecha" to dispositivo.fecha
        )
        baseDeDatos.collection("usuarios").document(uid)
            .collection("dispositivos").document(dispositivo.id)
            .update(cambios)
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }

    // DELETE
    fun eliminarDispositivo(uid: String, idDispositivo: String, alTerminar: (Boolean) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .collection("dispositivos").document(idDispositivo)
            .delete()
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }
}
