package com.example.conectasenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conectasenas.screens.LoginScreen
import com.example.conectasenas.screens.InicioScreen
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
    val navController = rememberNavController()
    val repositorioUsuarios = remember { UsuarioRepository() }

    NavHost(
        navController = navController,
        startDestination = Destino.Login.ruta
    ) {
        composable(Destino.Login.ruta) {
            LoginScreen(
                alIniciarSesion = { correo, contrasena ->
                    repositorioUsuarios.iniciarSesion(correo, contrasena)
                },
                alIniciarSesionCorrectamente = {
                    navController.navigate(Destino.Inicio.ruta)
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
                listaUsuarios = repositorioUsuarios.listaUsuarios,
                alVolverAlLogin = {
                    navController.popBackStack()
                },
                alRegistrarUsuario = { usuarioNuevo ->
                    repositorioUsuarios.registrar(usuarioNuevo)
                }
            )
        }

        composable(Destino.Recuperar.ruta) {
            RecuperarScreen(
                existeCorreo = { correo ->
                    repositorioUsuarios.existeCorreo(correo)
                },
                alVolverAlLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destino.Inicio.ruta) {
            InicioScreen(
                listaUsuariosRegistrados = repositorioUsuarios.listaUsuarios,
                alCerrarSesion = {
                    navController.popBackStack()
                }
            )
        }
    }
}
