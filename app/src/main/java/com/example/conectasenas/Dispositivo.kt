package com.example.conectasenas

// Un dispositivo guardado con la ubicación donde se encontró.
data class Dispositivo(
    val id: String = "",
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val fecha: Long
)
