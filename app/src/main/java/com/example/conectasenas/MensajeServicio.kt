package com.example.conectasenas

import com.google.firebase.firestore.FirebaseFirestore

// CRUD de los mensajes de las pantallas Escribir y Hablar.
// Se guardan en usuarios/{uid}/mensajes
class MensajeServicio {

    private val baseDeDatos: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // CREATE
    fun crearMensaje(uid: String, mensaje: Mensaje, alTerminar: (Boolean) -> Unit) {
        val datos = hashMapOf(
            "texto" to mensaje.texto,
            "tipo" to mensaje.tipo,
            "fecha" to mensaje.fecha
        )
        baseDeDatos.collection("usuarios").document(uid)
            .collection("mensajes")
            .add(datos)
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }

    // READ: trae los mensajes de un tipo ("escrito" o "hablado")
    fun obtenerMensajes(uid: String, tipo: String, alTerminar: (List<Mensaje>) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .collection("mensajes")
            .whereEqualTo("tipo", tipo)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.map { documento ->
                    Mensaje(
                        id = documento.id,
                        texto = documento.getString("texto") ?: "",
                        tipo = documento.getString("tipo") ?: "",
                        fecha = documento.getLong("fecha") ?: 0L
                    )
                }
                // el más nuevo primero
                alTerminar(lista.sortedByDescending { it.fecha })
            }
            .addOnFailureListener { alTerminar(emptyList()) }
    }

    // UPDATE
    fun actualizarMensaje(uid: String, mensaje: Mensaje, alTerminar: (Boolean) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .collection("mensajes").document(mensaje.id)
            .update("texto", mensaje.texto)
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }

    // DELETE
    fun eliminarMensaje(uid: String, idMensaje: String, alTerminar: (Boolean) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .collection("mensajes").document(idMensaje)
            .delete()
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }
}
