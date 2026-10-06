package com.example.conectasenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conectasenas.screens.BuscarDispositivoScreen
import com.example.conectasenas.screens.EscribirScreen
import com.example.conectasenas.screens.HablarScreen
import com.example.conectasenas.screens.LoginScreen
import com.example.conectasenas.screens.MenuScreen
import com.example.conectasenas.screens.PerfilScreen
import com.example.conectasenas.screens.RecuperarScreen
import com.example.conectasenas.screens.RegistroScreen
import com.example.conectasenas.ui.theme.ConectasenasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ConectasenasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AplicacionConectaSenas()
                }
            }
        }
    }
}

@Composable
fun AplicacionConectaSenas() {
    val contexto = LocalContext.current
    val navController = rememberNavController()

    // Servicios que hablan con Firebase
    val authServicio = remember { AuthServicio() }
    val usuarioServicio = remember { UsuarioServicio() }
    val mensajeServicio = remember { MensajeServicio() }
    val dispositivoServicio = remember { DispositivoServicio() }

    val repositorioUsuarios = remember { UsuarioRepository(authServicio, usuarioServicio) }
    val preferencias = remember { PreferenciasSesion(contexto) }

    // Si Firebase ya tiene una sesión abierta se entra directo al menú
    val pantallaInicial = if (repositorioUsuarios.uidActual() != null) {
        Destino.Menu.ruta
    } else {
        Destino.Login.ruta
    }

    NavHost(
        navController = navController,
        startDestination = pantallaInicial
    ) {
        composable(Destino.Login.ruta) {
            LoginScreen(
                preferencias = preferencias,
                alIniciarSesion = { correo, contrasena, alTerminar ->
                    repositorioUsuarios.iniciarSesion(correo, contrasena, alTerminar)
                },
                alIniciarSesionCorrectamente = {
                    navController.navigate(Destino.Menu.ruta) {
                        popUpTo(Destino.Login.ruta) { inclusive = true }
                    }
                },
                alPresionarCrearCuenta = {
                    navController.navigate(Destino.Registro.ruta)
                },
                alPresionarRecuperarContrasena = {
                    navController.navigate(Destino.Recuperar.ruta)
                }
            )
        }

        composable(Destino.Registro.ruta) {
            RegistroScreen(
                alVolverAlLogin = {
                    navController.popBackStack()
                },
                alRegistrarUsuario = { nombre, correo, contrasena, region, medio, alTerminar ->
                    repositorioUsuarios.registrar(nombre, correo, contrasena, region, medio) { resultado ->
                        // Firebase deja la sesión abierta al registrar, la cerramos
                        // para que el usuario inicie sesión de forma normal.
                        if (resultado == ResultadoRegistro.REGISTRO_CORRECTO) {
                            repositorioUsuarios.cerrarSesion()
                        }
                        alTerminar(resultado)
                    }
                }
            )
        }

        composable(Destino.Recuperar.ruta) {
            RecuperarScreen(
                alRecuperar = { correo, alTerminar ->
                    repositorioUsuarios.recuperarContrasena(correo, alTerminar)
                },
                alVolverAlLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destino.Menu.ruta) {
            MenuScreen(
                alIrAEscribir = {
                    navController.navigate(Destino.Escribir.ruta)
                },
                alIrAHablar = {
                    navController.navigate(Destino.Hablar.ruta)
                },
                alIrABuscarDispositivo = {
                    navController.navigate(Destino.BuscarDispositivo.ruta)
                },
                alIrAPerfil = {
                    navController.navigate(Destino.Perfil.ruta)
                },
                alCerrarSesion = {
                    repositorioUsuarios.cerrarSesion()
                    navController.navigate(Destino.Login.ruta) {
                        popUpTo(Destino.Menu.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Destino.Escribir.ruta) {
            EscribirScreen(
                uid = repositorioUsuarios.uidActual() ?: "",
                mensajeServicio = mensajeServicio,
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destino.Hablar.ruta) {
            HablarScreen(
                uid = repositorioUsuarios.uidActual() ?: "",
                mensajeServicio = mensajeServicio,
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destino.BuscarDispositivo.ruta) {
            BuscarDispositivoScreen(
                uid = repositorioUsuarios.uidActual() ?: "",
                dispositivoServicio = dispositivoServicio,
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destino.Perfil.ruta) {
            PerfilScreen(
                repositorioUsuarios = repositorioUsuarios,
                alVolver = {
                    navController.popBackStack()
                },
                alEliminarCuenta = {
                    navController.navigate(Destino.Login.ruta) {
                        popUpTo(Destino.Menu.ruta) { inclusive = true }
                    }
                }
            )
        }
    }
}
