package com.example.conectasenas

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.conectasenas.screens.LoginScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// Prueba de interfaz (Espresso/Compose) que se ejecuta en un celular o emulador.
// También sirve para subirla a Firebase Test Lab.
class LoginScreenTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private lateinit var preferencias: PreferenciasSesion

    @Before
    fun preparar() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        preferencias = PreferenciasSesion(contexto)
        preferencias.borrarCorreo()
    }

    @Test
    fun login_muestraLosElementosPrincipales() {
        reglaCompose.setContent {
            LoginScreen(
                preferencias = preferencias,
                alIniciarSesion = { _, _, _ -> },
                alIniciarSesionCorrectamente = {},
                alPresionarCrearCuenta = {},
                alPresionarRecuperarContrasena = {}
            )
        }

        reglaCompose.onNodeWithText("ConectaSeñas").assertIsDisplayed()
        reglaCompose.onNodeWithText("Correo electrónico").assertIsDisplayed()
        reglaCompose.onNodeWithText("Contraseña").assertIsDisplayed()
        reglaCompose.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        reglaCompose.onNodeWithText("Crear una cuenta").assertIsDisplayed()
    }

    @Test
    fun login_conCredencialesIncorrectas_muestraMensajeDeError() {
        reglaCompose.setContent {
            LoginScreen(
                preferencias = preferencias,
                alIniciarSesion = { _, _, alTerminar ->
                    alTerminar(ResultadoLogin.CREDENCIALES_INCORRECTAS)
                },
                alIniciarSesionCorrectamente = {},
                alPresionarCrearCuenta = {},
                alPresionarRecuperarContrasena = {}
            )
        }

        reglaCompose.onNodeWithText("Correo electrónico").performTextInput("ana@correo.cl")
        reglaCompose.onNodeWithText("Contraseña").performTextInput("malaclave")
        reglaCompose.onNodeWithText("Iniciar sesión").performClick()

        reglaCompose.onNodeWithText("Correo o contraseña incorrectos.").assertIsDisplayed()
    }

    @Test
    fun login_conCredencialesCorrectas_navegaAlMenu() {
        var navegoAlMenu = false

        reglaCompose.setContent {
            LoginScreen(
                preferencias = preferencias,
                alIniciarSesion = { _, _, alTerminar ->
                    alTerminar(ResultadoLogin.LOGIN_CORRECTO)
                },
                alIniciarSesionCorrectamente = { navegoAlMenu = true },
                alPresionarCrearCuenta = {},
                alPresionarRecuperarContrasena = {}
            )
        }

        reglaCompose.onNodeWithText("Correo electrónico").performTextInput("ana@correo.cl")
        reglaCompose.onNodeWithText("Contraseña").performTextInput("123456")
        reglaCompose.onNodeWithText("Iniciar sesión").performClick()

        reglaCompose.waitForIdle()
        assert(navegoAlMenu)
    }
}
