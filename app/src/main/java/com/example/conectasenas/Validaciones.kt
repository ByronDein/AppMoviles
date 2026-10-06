package com.example.conectasenas

// Funciones simples para revisar lo que escribe el usuario.
// Están separadas de las pantallas para poder probarlas con JUnit.
object Validaciones {

    fun correoEsValido(correo: String): Boolean {
        val correoLimpio = correo.trim()
        if (correoLimpio.isEmpty()) {
            return false
        }
        // Revisa que tenga algo@algo.algo
        val partes = correoLimpio.split("@")
        if (partes.size != 2) {
            return false
        }
        if (partes[0].isEmpty() || partes[1].isEmpty()) {
            return false
        }
        if (!partes[1].contains(".") || partes[1].startsWith(".") || partes[1].endsWith(".")) {
            return false
        }
        return true
    }

    // Firebase pide mínimo 6 caracteres
    fun contrasenaEsValida(contrasena: String): Boolean {
        return contrasena.length >= 6
    }

    fun textoNoVacio(texto: String): Boolean {
        return texto.trim().isNotEmpty()
    }
}
