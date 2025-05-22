package model

/**
 * Clase de datos que representa un registro de fichaje de un empleado
 */
data class Fichaje(
    val id: Int = 0,
    val dniEmpleado: String,
    val fecha: String,
    val horaEntrada: String,
    val horaSalida: String = ""  // Puede estar vacío si aún no se ha registrado la salida
)
