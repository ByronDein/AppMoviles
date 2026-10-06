package com.example.conectasenas

// Un mensaje que el usuario escribió o que se escuchó con el micrófono.
// tipo puede ser "escrito" o "hablado".
data class Mensaje(
    val id: String = "",
    val texto: String,
    val tipo: String,
    val fecha: Long
)
