package com.example.conectasenas

enum class ResultadoRegistro {
    REGISTRO_CORRECTO,
    DATOS_INCOMPLETOS,
    CORREO_INVALIDO,
    CONTRASENA_CORTA,
    CORREO_REPETIDO,
    ERROR
}

enum class ResultadoLogin {
    LOGIN_CORRECTO,
    DATOS_INCOMPLETOS,
    CREDENCIALES_INCORRECTAS,
    ERROR
}

enum class ResultadoRecuperar {
    SOLICITUD_ENVIADA,
    CORREO_INVALIDO,
    CORREO_NO_REGISTRADO,
    ERROR
}

// Esta clase junta Firebase Authentication con Firestore.
// Primero se revisan los datos y después se llama a los servicios.
class UsuarioRepository(
    private val authServicio: AuthServicio,
    private val usuarioServicio: UsuarioServicio
) {

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        region: String,
        medioComunicacion: String,
        alTerminar: (ResultadoRegistro) -> Unit
    ) {
        if (!Validaciones.textoNoVacio(nombre) || !Validaciones.textoNoVacio(correo) || contrasena.isEmpty()) {
            alTerminar(ResultadoRegistro.DATOS_INCOMPLETOS)
            return
        }
        if (!Validaciones.correoEsValido(correo)) {
            alTerminar(ResultadoRegistro.CORREO_INVALIDO)
            return
        }
        if (!Validaciones.contrasenaEsValida(contrasena)) {
            alTerminar(ResultadoRegistro.CONTRASENA_CORTA)
            return
        }

        authServicio.registrar(correo.trim(), contrasena) { codigo, uid ->
            if (codigo == CodigoAuth.OK && uid != null) {
                val usuarioNuevo = Usuario(
                    uid = uid,
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    region = region,
                    medioComunicacion = medioComunicacion
                )
                usuarioServicio.crearUsuario(usuarioNuevo) { guardado ->
                    if (guardado) {
                        alTerminar(ResultadoRegistro.REGISTRO_CORRECTO)
                    } else {
                        alTerminar(ResultadoRegistro.ERROR)
                    }
                }
            } else if (codigo == CodigoAuth.CORREO_REPETIDO) {
                alTerminar(ResultadoRegistro.CORREO_REPETIDO)
            } else {
                alTerminar(ResultadoRegistro.ERROR)
            }
        }
    }

    fun iniciarSesion(
        correo: String,
        contrasena: String,
        alTerminar: (ResultadoLogin) -> Unit
    ) {
        if (!Validaciones.textoNoVacio(correo) || contrasena.isEmpty()) {
            alTerminar(ResultadoLogin.DATOS_INCOMPLETOS)
            return
        }

        authServicio.iniciarSesion(correo.trim(), contrasena) { codigo, _ ->
            if (codigo == CodigoAuth.OK) {
                alTerminar(ResultadoLogin.LOGIN_CORRECTO)
            } else if (codigo == CodigoAuth.CREDENCIALES_INVALIDAS) {
                alTerminar(ResultadoLogin.CREDENCIALES_INCORRECTAS)
            } else {
                alTerminar(ResultadoLogin.ERROR)
            }
        }
    }

    fun recuperarContrasena(correo: String, alTerminar: (ResultadoRecuperar) -> Unit) {
        if (!Validaciones.correoEsValido(correo)) {
            alTerminar(ResultadoRecuperar.CORREO_INVALIDO)
            return
        }

        authServicio.recuperarContrasena(correo.trim()) { codigo ->
            if (codigo == CodigoAuth.OK) {
                alTerminar(ResultadoRecuperar.SOLICITUD_ENVIADA)
            } else if (codigo == CodigoAuth.CREDENCIALES_INVALIDAS) {
                alTerminar(ResultadoRecuperar.CORREO_NO_REGISTRADO)
            } else {
                alTerminar(ResultadoRecuperar.ERROR)
            }
        }
    }

    fun uidActual(): String? {
        return authServicio.uidActual()
    }

    fun cerrarSesion() {
        authServicio.cerrarSesion()
    }

    fun obtenerUsuarioActual(alTerminar: (Usuario?) -> Unit) {
        val uid = authServicio.uidActual()
        if (uid == null) {
            alTerminar(null)
        } else {
            usuarioServicio.obtenerUsuario(uid, alTerminar)
        }
    }

    fun actualizarUsuario(usuario: Usuario, alTerminar: (Boolean) -> Unit) {
        if (!Validaciones.textoNoVacio(usuario.nombre)) {
            alTerminar(false)
            return
        }
        usuarioServicio.actualizarUsuario(usuario, alTerminar)
    }

    // Borra primero los datos y después la cuenta
    fun eliminarCuenta(alTerminar: (Boolean) -> Unit) {
        val uid = authServicio.uidActual()
        if (uid == null) {
            alTerminar(false)
            return
        }
        usuarioServicio.eliminarUsuario(uid) { datosBorrados ->
            if (datosBorrados) {
                authServicio.eliminarCuenta(alTerminar)
            } else {
                alTerminar(false)
            }
        }
    }
}
