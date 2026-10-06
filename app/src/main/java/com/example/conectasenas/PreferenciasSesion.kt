package com.example.conectasenas

import android.content.Context

// Guarda en el celular el correo cuando el usuario marca "Recordar sesión".
class PreferenciasSesion(context: Context) {

    private val preferencias =
        context.getSharedPreferences("sesion_conectasenas", Context.MODE_PRIVATE)

    fun guardarCorreo(correo: String) {
        preferencias.edit()
            .putString("correo_recordado", correo)
            .apply()
    }

    fun obtenerCorreo(): String {
        return preferencias.getString("correo_recordado", "") ?: ""
    }

    fun borrarCorreo() {
        preferencias.edit()
            .remove("correo_recordado")
            .apply()
    }
}
