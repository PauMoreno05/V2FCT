package controller

import dao.FichajeDAO
import dao.EmpleadoDAO
import model.Fichaje
import model.Empleado
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Controlador para la gestión de fichajes
 */
class FichajeController {
    private val fichajeDAO = FichajeDAO()
    private val empleadoDAO = EmpleadoDAO()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    /**
     * Obtiene todos los fichajes
     * @return Lista de fichajes
     */
    fun getAllFichajes(): List<Fichaje> {
        return fichajeDAO.getAll()
    }

    /**
     * Obtiene un fichaje por su ID
     * @param id ID del fichaje
     * @return Fichaje encontrado o null si no existe
     */
    fun getFichajeById(id: Int): Fichaje? {
        return fichajeDAO.getById(id)
    }

    /**
     * Obtiene los fichajes de un empleado
     * @param dni DNI del empleado
     * @return Lista de fichajes del empleado
     */
    fun getFichajesByEmpleado(dni: String): List<Fichaje> {
        // Verificar que el empleado existe
        val empleado = empleadoDAO.getByDni(dni)
        if (empleado == null) {
            return emptyList()
        }

        return fichajeDAO.getByEmpleado(dni)
    }

    /**
     * Obtiene los fichajes de una fecha específica
     * @param fecha Fecha en formato yyyy-MM-dd
     * @return Lista de fichajes de esa fecha
     */
    fun getFichajesByFecha(fecha: String): List<Fichaje> {
        return fichajeDAO.getByFecha(fecha)
    }

    /**
     * Obtiene los fichajes de la fecha actual
     * @return Lista de fichajes de hoy
     */
    fun getFichajesHoy(): List<Fichaje> {
        val fechaHoy = dateFormat.format(Date())
        return fichajeDAO.getByFecha(fechaHoy)
    }

    /**
     * Registra la entrada de un empleado
     * @param dni DNI del empleado
     * @return ID del fichaje creado o -1 si falla
     */
    fun registrarEntrada(dni: String): Int {
        // Verificar que el empleado existe
        val empleado = empleadoDAO.getByDni(dni)
        if (empleado == null) {
            return -1
        }

        return fichajeDAO.registrarEntrada(dni)
    }

    /**
     * Registra la salida de un empleado
     * @param dni DNI del empleado
     * @return true si se registra correctamente, false en caso contrario
     */
    fun registrarSalida(dni: String): Boolean {
        // Verificar que el empleado existe
        val empleado = empleadoDAO.getByDni(dni)
        if (empleado == null) {
            return false
        }

        return fichajeDAO.registrarSalida(dni)
    }

    /**
     * Verifica si un empleado tiene una entrada sin salida registrada
     * @param dni DNI del empleado
     * @return true si tiene entrada pendiente, false si no
     */
    fun tieneEntradaPendiente(dni: String): Boolean {
        val fechaHoy = dateFormat.format(Date())
        val fichajesHoy = fichajeDAO.getByFecha(fechaHoy)

        return fichajesHoy.any { it.dniEmpleado == dni && it.horaSalida.isBlank() }
    }

    /**
     * Elimina un fichaje
     * @param id ID del fichaje a eliminar
     * @return true si se elimina correctamente, false en caso contrario
     */
    fun deleteFichaje(id: Int): Boolean {
        return fichajeDAO.delete(id)
    }

    /**
     * Obtiene información resumida de los fichajes de un empleado
     * @param dni DNI del empleado
     * @param fechaInicio Fecha inicial en formato yyyy-MM-dd
     * @param fechaFin Fecha final en formato yyyy-MM-dd
     * @return Map con información resumida
     */
    fun getResumenFichajes(dni: String, fechaInicio: String, fechaFin: String): Map<String, Any> {
        val empleado = empleadoDAO.getByDni(dni) ?: return emptyMap()

        // Obtener todos los fichajes del empleado
        val fichajes = fichajeDAO.getByEmpleado(dni)

        // Filtrar por rango de fechas
        val fichajesFiltrados = fichajes.filter { fichaje ->
            fichaje.fecha >= fechaInicio && fichaje.fecha <= fechaFin
        }

        // Calcular horas totales trabajadas
        var horasTotales = 0.0
        var diasTrabajados = 0

        fichajesFiltrados.forEach { fichaje ->
            if (fichaje.horaSalida.isNotBlank()) {
                // Calcular horas trabajadas en este fichaje
                val horaEntrada = fichaje.horaEntrada.split(":").map { it.toInt() }
                val horaSalida = fichaje.horaSalida.split(":").map { it.toInt() }

                val minEntrada = horaEntrada[0] * 60 + horaEntrada[1]
                val minSalida = horaSalida[0] * 60 + horaSalida[1]

                // Calcular diferencia en horas
                val diferenciaMin = minSalida - minEntrada
                val horas = diferenciaMin / 60.0

                horasTotales += horas
                diasTrabajados++
            }
        }

        return mapOf(
            "empleado" to empleado,
            "horasTotales" to horasTotales,
            "diasTrabajados" to diasTrabajados,
            "fichajes" to fichajesFiltrados
        )
    }
}

