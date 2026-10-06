package com.example.conectasenas

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Prueba con Robolectric: simula Android en el computador para probar SharedPreferences.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PreferenciasSesionTest {

    private lateinit var preferencias: PreferenciasSesion

    @Before
    fun preparar() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        preferencias = PreferenciasSesion(contexto)
        preferencias.borrarCorreo()
    }

    @Test
    fun obtenerCorreo_alInicio_devuelveTextoVacio() {
        assertEquals("", preferencias.obtenerCorreo())
    }

    @Test
    fun guardarCorreo_luegoSePuedeLeer() {
        preferencias.guardarCorreo("ana@conectasenas.cl")

        assertEquals("ana@conectasenas.cl", preferencias.obtenerCorreo())
    }

    @Test
    fun borrarCorreo_dejaElCorreoVacio() {
        preferencias.guardarCorreo("ana@conectasenas.cl")

        preferencias.borrarCorreo()

        assertEquals("", preferencias.obtenerCorreo())
    }
}
