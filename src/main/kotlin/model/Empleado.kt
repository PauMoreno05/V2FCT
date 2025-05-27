package model

/**
 * Clase de datos que representa un empleado de la empresa
 */
data class Empleado(
    val dni: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val idDepartamento: Int,
    val telefono: Int,
    val contraseña: String,
    val rol: String
)