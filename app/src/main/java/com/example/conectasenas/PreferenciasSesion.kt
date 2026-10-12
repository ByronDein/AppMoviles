package com.example.conectasenas

import android.content.Context
import androidx.core.content.edit

// Guarda en el celular el correo cuando el usuario marca "Recordar sesión".
// Usa la extensión KTX edit { } en vez de edit()...apply().
class PreferenciasSesion(context: Context) {

    private val preferencias =
        context.getSharedPreferences("sesion_conectasenas", Context.MODE_PRIVATE)

    fun guardarCorreo(correo: String) {
        preferencias.edit {
            putString("correo_recordado", correo)
        }
    }

    fun obtenerCorreo(): String {
        return preferencias.getString("correo_recordado", "") ?: ""
    }

    fun borrarCorreo() {
        preferencias.edit {
            remove("correo_recordado")
        }
    }
}
