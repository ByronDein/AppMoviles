package com.example.conectasenas

// La contraseña no se guarda aquí, esa la guarda Firebase Authentication.
data class Usuario(
    val uid: String = "",
    val nombre: String,
    val correo: String,
    val region: String,
    val medioComunicacion: String
)
