package com.example.conectasenas

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun correoEsValido_conCorreoNormal_devuelveTrue() {
        assertTrue(Validaciones.correoEsValido("ana@conectasenas.cl"))
    }

    @Test
    fun correoEsValido_conEspaciosAlRededor_devuelveTrue() {
        assertTrue(Validaciones.correoEsValido("  ana@conectasenas.cl  "))
    }

    @Test
    fun correoEsValido_sinArroba_devuelveFalse() {
        assertFalse(Validaciones.correoEsValido("anaconectasenas.cl"))
    }

    @Test
    fun correoEsValido_sinPunto_devuelveFalse() {
        assertFalse(Validaciones.correoEsValido("ana@conectasenas"))
    }

    @Test
    fun correoEsValido_sinNombre_devuelveFalse() {
        assertFalse(Validaciones.correoEsValido("@conectasenas.cl"))
    }

    @Test
    fun correoEsValido_vacio_devuelveFalse() {
        assertFalse(Validaciones.correoEsValido(""))
    }

    @Test
    fun contrasenaEsValida_conSeisCaracteres_devuelveTrue() {
        assertTrue(Validaciones.contrasenaEsValida("123456"))
    }

    @Test
    fun contrasenaEsValida_conCincoCaracteres_devuelveFalse() {
        assertFalse(Validaciones.contrasenaEsValida("12345"))
    }

    @Test
    fun textoNoVacio_conTexto_devuelveTrue() {
        assertTrue(Validaciones.textoNoVacio("hola"))
    }

    @Test
    fun textoNoVacio_conSoloEspacios_devuelveFalse() {
        assertFalse(Validaciones.textoNoVacio("   "))
    }
}
