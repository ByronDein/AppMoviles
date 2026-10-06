package com.example.conectasenas

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

// Pruebas con Mockito: los servicios de Firebase se reemplazan por objetos falsos (mock)
// para probar solo la lógica del repositorio, sin internet.
class UsuarioRepositoryTest {

    private lateinit var authFalso: AuthServicio
    private lateinit var usuarioServicioFalso: UsuarioServicio
    private lateinit var repositorio: UsuarioRepository

    @Before
    fun preparar() {
        authFalso = mock()
        usuarioServicioFalso = mock()
        repositorio = UsuarioRepository(authFalso, usuarioServicioFalso)
    }

    // ---------- Registro ----------

    @Test
    fun registrar_conDatosVacios_devuelveDatosIncompletos() {
        var resultado: ResultadoRegistro? = null

        repositorio.registrar("", "", "", "Biobío", "Texto") { resultado = it }

        assertEquals(ResultadoRegistro.DATOS_INCOMPLETOS, resultado)
        verify(authFalso, never()).registrar(any(), any(), any())
    }

    @Test
    fun registrar_conCorreoInvalido_devuelveCorreoInvalido() {
        var resultado: ResultadoRegistro? = null

        repositorio.registrar("Ana", "correo-malo", "123456", "Biobío", "Texto") { resultado = it }

        assertEquals(ResultadoRegistro.CORREO_INVALIDO, resultado)
        verify(authFalso, never()).registrar(any(), any(), any())
    }

    @Test
    fun registrar_conContrasenaCorta_devuelveContrasenaCorta() {
        var resultado: ResultadoRegistro? = null

        repositorio.registrar("Ana", "ana@correo.cl", "123", "Biobío", "Texto") { resultado = it }

        assertEquals(ResultadoRegistro.CONTRASENA_CORTA, resultado)
        verify(authFalso, never()).registrar(any(), any(), any())
    }

    @Test
    fun registrar_cuandoTodoSaleBien_guardaElUsuarioYDevuelveCorrecto() {
        whenever(authFalso.registrar(eq("ana@correo.cl"), eq("123456"), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(CodigoAuth, String?) -> Unit>(2)
            respuesta(CodigoAuth.OK, "uid-ana")
            null
        }
        whenever(usuarioServicioFalso.crearUsuario(any(), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(Boolean) -> Unit>(1)
            respuesta(true)
            null
        }

        var resultado: ResultadoRegistro? = null
        repositorio.registrar("Ana Pérez", "ana@correo.cl", "123456", "Biobío", "Texto") { resultado = it }

        assertEquals(ResultadoRegistro.REGISTRO_CORRECTO, resultado)
        val usuarioEsperado = Usuario(
            uid = "uid-ana",
            nombre = "Ana Pérez",
            correo = "ana@correo.cl",
            region = "Biobío",
            medioComunicacion = "Texto"
        )
        verify(usuarioServicioFalso).crearUsuario(eq(usuarioEsperado), any())
    }

    @Test
    fun registrar_conCorreoYaUsado_devuelveCorreoRepetido() {
        whenever(authFalso.registrar(any(), any(), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(CodigoAuth, String?) -> Unit>(2)
            respuesta(CodigoAuth.CORREO_REPETIDO, null)
            null
        }

        var resultado: ResultadoRegistro? = null
        repositorio.registrar("Ana", "ana@correo.cl", "123456", "Biobío", "Texto") { resultado = it }

        assertEquals(ResultadoRegistro.CORREO_REPETIDO, resultado)
        verify(usuarioServicioFalso, never()).crearUsuario(any(), any())
    }

    @Test
    fun registrar_cuandoFallaGuardarEnFirestore_devuelveError() {
        whenever(authFalso.registrar(any(), any(), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(CodigoAuth, String?) -> Unit>(2)
            respuesta(CodigoAuth.OK, "uid-ana")
            null
        }
        whenever(usuarioServicioFalso.crearUsuario(any(), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(Boolean) -> Unit>(1)
            respuesta(false)
            null
        }

        var resultado: ResultadoRegistro? = null
        repositorio.registrar("Ana", "ana@correo.cl", "123456", "Biobío", "Texto") { resultado = it }

        assertEquals(ResultadoRegistro.ERROR, resultado)
    }

    // ---------- Login ----------

    @Test
    fun iniciarSesion_conCamposVacios_devuelveDatosIncompletos() {
        var resultado: ResultadoLogin? = null

        repositorio.iniciarSesion("", "") { resultado = it }

        assertEquals(ResultadoLogin.DATOS_INCOMPLETOS, resultado)
        verify(authFalso, never()).iniciarSesion(any(), any(), any())
    }

    @Test
    fun iniciarSesion_conDatosCorrectos_devuelveLoginCorrecto() {
        whenever(authFalso.iniciarSesion(eq("ana@correo.cl"), eq("123456"), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(CodigoAuth, String?) -> Unit>(2)
            respuesta(CodigoAuth.OK, "uid-ana")
            null
        }

        var resultado: ResultadoLogin? = null
        repositorio.iniciarSesion("ana@correo.cl", "123456") { resultado = it }

        assertEquals(ResultadoLogin.LOGIN_CORRECTO, resultado)
    }

    @Test
    fun iniciarSesion_conContrasenaMala_devuelveCredencialesIncorrectas() {
        whenever(authFalso.iniciarSesion(any(), any(), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(CodigoAuth, String?) -> Unit>(2)
            respuesta(CodigoAuth.CREDENCIALES_INVALIDAS, null)
            null
        }

        var resultado: ResultadoLogin? = null
        repositorio.iniciarSesion("ana@correo.cl", "mala") { resultado = it }

        assertEquals(ResultadoLogin.CREDENCIALES_INCORRECTAS, resultado)
    }

    // ---------- Recuperar contraseña ----------

    @Test
    fun recuperarContrasena_conCorreoInvalido_devuelveCorreoInvalido() {
        var resultado: ResultadoRecuperar? = null

        repositorio.recuperarContrasena("hola") { resultado = it }

        assertEquals(ResultadoRecuperar.CORREO_INVALIDO, resultado)
        verify(authFalso, never()).recuperarContrasena(any(), any())
    }

    @Test
    fun recuperarContrasena_conCorreoValido_devuelveSolicitudEnviada() {
        whenever(authFalso.recuperarContrasena(eq("ana@correo.cl"), any())).thenAnswer { invocacion ->
            val respuesta = invocacion.getArgument<(CodigoAuth) -> Unit>(1)
            respuesta(CodigoAuth.OK)
            null
        }

        var resultado: ResultadoRecuperar? = null
        repositorio.recuperarContrasena("ana@correo.cl") { resultado = it }

        assertEquals(ResultadoRecuperar.SOLICITUD_ENVIADA, resultado)
    }

    // ---------- Eliminar cuenta ----------

    @Test
    fun eliminarCuenta_sinSesion_devuelveFalse() {
        whenever(authFalso.uidActual()).thenReturn(null)

        var resultado: Boolean? = null
        repositorio.eliminarCuenta { resultado = it }

        assertEquals(false, resultado)
        verify(usuarioServicioFalso, never()).eliminarUsuario(any(), any())
    }
}
