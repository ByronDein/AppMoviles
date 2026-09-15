package com.example.conectasenas

// Esta clase se encarga de guardar y buscar usuarios.
// Por ahora los usuarios viven solo mientras la aplicación está abierta.
class UsuarioRepository {
    // Arreglo de cinco usuarios que se cargan al abrir la aplicación.
    // arrayOf se usa para cumplir con el requisito de almacenar cinco usuarios.
    private val usuarios = arrayOf(
        Usuario(
            nombre = "Ana Pérez",
            correo = "ana@conectasenas.cl",
            contrasena = "ana123",
            region = "Metropolitana",
            medioComunicacion = "Texto"
        ),
        Usuario(
            nombre = "Bruno Soto",
            correo = "bruno@conectasenas.cl",
            contrasena = "bruno123",
            region = "Valparaíso",
            medioComunicacion = "Videollamada"
        ),
        Usuario(
            nombre = "Carla Muñoz",
            correo = "carla@conectasenas.cl",
            contrasena = "carla123",
            region = "Biobío",
            medioComunicacion = "Vibración"
        ),
        Usuario(
            nombre = "Diego Rojas",
            correo = "diego@conectasenas.cl",
            contrasena = "diego123",
            region = "Metropolitana",
            medioComunicacion = "Texto"
        ),
        Usuario(
            nombre = "Elena Díaz",
            correo = "elena@conectasenas.cl",
            contrasena = "elena123",
            region = "Otra",
            medioComunicacion = "Mensaje de texto"
        )
    )

    val listaUsuarios: List<Usuario>
        get() = usuarios.toList()

    fun registrar(usuarioNuevo: Usuario): ResultadoRegistro {
        return when {
            usuarios.any { it.correo.equals(usuarioNuevo.correo, ignoreCase = true) } -> {
                ResultadoRegistro.CORREO_REPETIDO
            }
            // El arreglo tiene cinco posiciones y ya se encuentra completo.
            else -> ResultadoRegistro.LIMITE_ALCANZADO
        }
    }

    fun iniciarSesion(correo: String, contrasena: String): Boolean {
        return usuarios.any { usuario ->
            usuario.correo.equals(correo, ignoreCase = true) &&
                usuario.contrasena == contrasena
        }
    }

    fun existeCorreo(correo: String): Boolean {
        return usuarios.any { usuario ->
            usuario.correo.equals(correo, ignoreCase = true)
        }
    }
}

enum class ResultadoRegistro {
    REGISTRO_CORRECTO,
    LIMITE_ALCANZADO,
    CORREO_REPETIDO
}
