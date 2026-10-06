package com.example.conectasenas

// Esta clase reúne los nombres de las pantallas en un solo lugar.
sealed class Destino(val ruta: String) {
    data object Login : Destino("login")
    data object Registro : Destino("registro")
    data object Recuperar : Destino("recuperar")
    data object Menu : Destino("menu")
    data object Escribir : Destino("escribir")
    data object Hablar : Destino("hablar")
    data object BuscarDispositivo : Destino("buscar_dispositivo")
    data object Perfil : Destino("perfil")
}
