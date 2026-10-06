package com.example.conectasenas

import com.google.firebase.firestore.FirebaseFirestore

// CRUD de usuarios en Cloud Firestore.
// Los datos quedan en la colección "usuarios", un documento por cada uid.
class UsuarioServicio {

    private val baseDeDatos: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // CREATE
    fun crearUsuario(usuario: Usuario, alTerminar: (Boolean) -> Unit) {
        val datos = hashMapOf(
            "nombre" to usuario.nombre,
            "correo" to usuario.correo,
            "region" to usuario.region,
            "medioComunicacion" to usuario.medioComunicacion
        )
        baseDeDatos.collection("usuarios").document(usuario.uid)
            .set(datos)
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }

    // READ
    fun obtenerUsuario(uid: String, alTerminar: (Usuario?) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    val usuario = Usuario(
                        uid = uid,
                        nombre = documento.getString("nombre") ?: "",
                        correo = documento.getString("correo") ?: "",
                        region = documento.getString("region") ?: "",
                        medioComunicacion = documento.getString("medioComunicacion") ?: ""
                    )
                    alTerminar(usuario)
                } else {
                    alTerminar(null)
                }
            }
            .addOnFailureListener { alTerminar(null) }
    }

    // UPDATE
    fun actualizarUsuario(usuario: Usuario, alTerminar: (Boolean) -> Unit) {
        val cambios = hashMapOf<String, Any>(
            "nombre" to usuario.nombre,
            "region" to usuario.region,
            "medioComunicacion" to usuario.medioComunicacion
        )
        baseDeDatos.collection("usuarios").document(usuario.uid)
            .update(cambios)
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }

    // DELETE
    fun eliminarUsuario(uid: String, alTerminar: (Boolean) -> Unit) {
        baseDeDatos.collection("usuarios").document(uid)
            .delete()
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }
}
