package com.example.conectasenas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

// Resultados posibles de una operación de autenticación.
enum class CodigoAuth {
    OK,
    CORREO_REPETIDO,
    CREDENCIALES_INVALIDAS,
    ERROR
}

// Esta clase se conecta con Firebase Authentication.
// Aquí se hace el registro, el login y la recuperación de contraseña.
class AuthServicio {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun uidActual(): String? {
        return auth.currentUser?.uid
    }

    fun correoActual(): String? {
        return auth.currentUser?.email
    }

    // Si sale bien, el segundo valor que se entrega es el uid del usuario nuevo.
    fun registrar(correo: String, contrasena: String, alTerminar: (CodigoAuth, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { resultado ->
                alTerminar(CodigoAuth.OK, resultado.user?.uid)
            }
            .addOnFailureListener { error ->
                if (error is FirebaseAuthUserCollisionException) {
                    alTerminar(CodigoAuth.CORREO_REPETIDO, null)
                } else {
                    alTerminar(CodigoAuth.ERROR, null)
                }
            }
    }

    fun iniciarSesion(correo: String, contrasena: String, alTerminar: (CodigoAuth, String?) -> Unit) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { resultado ->
                alTerminar(CodigoAuth.OK, resultado.user?.uid)
            }
            .addOnFailureListener { error ->
                if (error is FirebaseAuthInvalidCredentialsException ||
                    error is FirebaseAuthInvalidUserException
                ) {
                    alTerminar(CodigoAuth.CREDENCIALES_INVALIDAS, null)
                } else {
                    alTerminar(CodigoAuth.ERROR, null)
                }
            }
    }

    fun recuperarContrasena(correo: String, alTerminar: (CodigoAuth) -> Unit) {
        auth.sendPasswordResetEmail(correo)
            .addOnSuccessListener {
                alTerminar(CodigoAuth.OK)
            }
            .addOnFailureListener { error ->
                if (error is FirebaseAuthInvalidUserException) {
                    alTerminar(CodigoAuth.CREDENCIALES_INVALIDAS)
                } else {
                    alTerminar(CodigoAuth.ERROR)
                }
            }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    // Borra la cuenta de Authentication del usuario que tiene la sesión abierta
    fun eliminarCuenta(alTerminar: (Boolean) -> Unit) {
        val usuarioActual = auth.currentUser
        if (usuarioActual == null) {
            alTerminar(false)
            return
        }
        usuarioActual.delete()
            .addOnSuccessListener { alTerminar(true) }
            .addOnFailureListener { alTerminar(false) }
    }
}
