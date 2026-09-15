package com.example.conectasenas

// Esta clase reúne los nombres de las pantallas en un solo lugar.
sealed class Destino(val ruta: String) {
    data object Login : Destino("login")
    data object Registro : Destino("registro")
    data object Recuperar : Destino("recuperar")
    data object Inicio : Destino("inicio")
}
