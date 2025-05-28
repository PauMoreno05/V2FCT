package dao

import db.DBConnection
import model.Fichaje
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Data Access Object para la entidad Fichaje
 */
class FichajeDAO {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val timeFormat = SimpleDateFormat("HH:mm:ss")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Obtiene todos los fichajes de la base de datos
     * @return Lista de fichajes
     */
    fun getAll(): List<Fichaje> {
        val fichajes = mutableListOf<Fichaje>()
        val connection = DBConnection.connect()

        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("""SELECT * FROM "Fichajes"""")

            while (resultSet.next()) {
                val fichaje = Fichaje(
                    id = resultSet.getInt("ID_Fichaje"),
                    dniEmpleado = resultSet.getString("DNI_Empleado"),
                    fecha = resultSet.getString("Fecha"),
                    horaEntrada = resultSet.getString("Hora_Entrada"),
                    horaSalida = resultSet.getString("Hora_Salida"),
                    ubicacion = resultSet.getString("Ubicacion")
                )
                fichajes.add(fichaje)
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            println("Error al obtener los fichajes: ${e.message}")
        }

        return fichajes
    }

    /**
     * Obtiene un fichaje por su ID
     * @param id ID del fichaje a buscar
     * @return Fichaje encontrado o null si no existe
     */
    fun getById(id: Int): Fichaje? {
        val connection = DBConnection.connect()

        try {
            val query = """SELECT * FROM "Fichajes" WHERE "ID_Fichaje" = ?"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, id)

            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                return Fichaje(
                    id = resultSet.getInt("ID_Fichaje"),
                    dniEmpleado = resultSet.getString("DNI_Empleado"),
                    fecha = resultSet.getString("Fecha"),
                    horaEntrada = resultSet.getString("Hora_Entrada"),
                    horaSalida = resultSet.getString("Hora_Salida"),
                    ubicacion = resultSet.getString("Ubicacion")
                )
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener el fichaje con ID $id: ${e.message}")
        }

        return null
    }

    /**
     * Obtiene los fichajes de un empleado
     * @param dniEmpleado DNI del empleado
     * @return Lista de fichajes del empleado
     */
    fun getByEmpleado(dniEmpleado: String): List<Fichaje> {
        val fichajes = mutableListOf<Fichaje>()
        val connection = DBConnection.connect()

        try {
            val query = """SELECT * FROM "Fichajes" WHERE "DNI_Empleado" = ? ORDER BY "Fecha" DESC, "Hora_Entrada" DESC"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, dniEmpleado)

            val resultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val fichaje = Fichaje(
                    id = resultSet.getInt("ID_Fichaje"),
                    dniEmpleado = resultSet.getString("DNI_Empleado"),
                    fecha = resultSet.getString("Fecha"),
                    horaEntrada = resultSet.getString("Hora_Entrada"),
                    horaSalida = resultSet.getString("Hora_Salida"),
                    ubicacion = resultSet.getString("Ubicacion")
                )
                fichajes.add(fichaje)
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener los fichajes del empleado $dniEmpleado: ${e.message}")
        }

        return fichajes
    }

    /**
     * Obtiene los fichajes por fecha
     * @param fecha Fecha de los fichajes (formato: YYYY-MM-DD)
     * @return Lista de fichajes de la fecha especificada
     */
    fun getByFecha(fecha: String): List<Fichaje> {
        val fichajes = mutableListOf<Fichaje>()
        val connection = DBConnection.connect()

        try {
            val query = """SELECT * FROM "Fichajes" WHERE "Fecha" = ? ORDER BY "Hora_Entrada""""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, fecha)

            val resultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val fichaje = Fichaje(
                    id = resultSet.getInt("ID_Fichaje"),
                    dniEmpleado = resultSet.getString("DNI_Empleado"),
                    fecha = resultSet.getString("Fecha"),
                    horaEntrada = resultSet.getString("Hora_Entrada"),
                    horaSalida = resultSet.getString("Hora_Salida"),
                    ubicacion = resultSet.getString("Ubicacion")
                )
                fichajes.add(fichaje)
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener los fichajes de la fecha $fecha: ${e.message}")
        }

        return fichajes
    }

    /**
     * Registra la entrada de un empleado
     * @param dniEmpleado DNI del empleado
     * @return ID del fichaje creado o -1 si falla
     */
    fun registrarEntrada(dniEmpleado: String): Int {
        val connection = DBConnection.connect()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm")

        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        try {
            // Verificar si ya existe un fichaje sin salida para este empleado
            val checkQuery = """
            SELECT "ID_Fichaje" FROM "Fichajes" 
            WHERE "DNI_Empleado" = ? AND "Fecha" = ? AND "Hora_Salida" = ''
        """
            val checkStatement = connection.prepareStatement(checkQuery)
            checkStatement.setString(1, dniEmpleado)
            checkStatement.setString(2, currentDate)

            val checkResult = checkStatement.executeQuery()
            if (checkResult.next()) {
                println("El empleado ya tiene un fichaje sin registrar salida")
                checkResult.close()
                checkStatement.close()
                return -1
            }
            checkResult.close()
            checkStatement.close()

            // Insertar nuevo fichaje y devolver el ID generado
            val query = """
            INSERT INTO "Fichajes" ("DNI_Empleado", "Fecha", "Hora_Entrada", "Hora_Salida", "Ubicacion") 
            VALUES (?, ?, ?, '','') RETURNING "ID_Fichaje"
        """
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, dniEmpleado)
            preparedStatement.setString(2, currentDate)
            preparedStatement.setString(3, currentTime)

            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                val id = resultSet.getInt("ID_Fichaje")
                resultSet.close()
                preparedStatement.close()
                return id
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al registrar la entrada: ${e.message}")
        }

        return -1
    }


    /**
     * Registra la salida de un empleado
     * @param dniEmpleado DNI del empleado
     * @return true si se registra correctamente, false en caso contrario
     */
    fun registrarSalida(dniEmpleado: String): Boolean {
        val connection = DBConnection.connect()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm")

        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        try {
            // Buscar el fichaje sin salida para este empleado
            val query = """
                UPDATE "Fichajes" 
                SET "Hora_Salida" = ? 
                WHERE "DNI_Empleado" = ? AND "Fecha" = ? AND "Hora_Salida" = ''
            """
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, currentTime)
            preparedStatement.setString(2, dniEmpleado)
            preparedStatement.setString(3, currentDate)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al registrar la salida: ${e.message}")
        }

        return false
    }

    /**
     * Elimina un fichaje de la base de datos
     * @param id ID del fichaje a eliminar
     * @return true si se elimina correctamente, false en caso contrario
     */
    fun delete(id: Int): Boolean {
        val connection = DBConnection.connect()

        try {
            val query = """DELETE FROM "Fichajes" WHERE "ID_Fichaje" = ?"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al eliminar el fichaje: ${e.message}")
        }

        return false
    }

    fun getByEmpleadoAndDate(dni: String, fecha: String): Fichaje? {
        DBConnection.connect().use { conn ->
            val sql = """
                SELECT * FROM "Fichajes" 
                WHERE "DNI_Empleado" = ? AND "Fecha" = ?
                LIMIT 1
            """
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, dni)
                stmt.setString(2, fecha)
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) {
                        Fichaje(
                            id = rs.getInt("ID_Fichaje"),
                            dniEmpleado = rs.getString("DNI_Empleado"),
                            fecha = rs.getString("Fecha"),
                            horaEntrada = rs.getString("Hora_Entrada"),
                            horaSalida = rs.getString("Hora_Salida") ?: "",
                            ubicacion = rs.getString("Ubicacion")
                        )
                    } else null
                }
            }
        }
    }

    fun insert(fichaje: Fichaje): Fichaje? {
        DBConnection.connect().use { conn ->
            val sql = """
                INSERT INTO "Fichajes" (DNI_Empleado, Fecha, Hora_Entrada, Hora_Salida, Ubicacion)
                VALUES (?, ?, ?, ?, ?)
            """
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, fichaje.dniEmpleado)
                stmt.setString(2, fichaje.fecha)
                stmt.setString(3, fichaje.horaEntrada)
                stmt.setString(4, fichaje.horaSalida)
                stmt.setString(4, fichaje.ubicacion)

                stmt.executeUpdate()

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return fichaje.copy(id = keys.getInt(1))
                    }
                }
            }
        }
        return null
    }
    /**
     * Actualiza un fichaje existente en la base de datos
     * @param fichaje Objeto Fichaje con los datos actualizados
     */
    fun update(fichaje: Fichaje): Fichaje? {
        DBConnection.connect().use { conn ->
            try {
                val query = """
                    UPDATE Fichajes SET 
                        DNI_Empleado = ?,
                        Fecha = ?,
                        Hora_Entrada = ?,
                        Hora_Salida = ?,
                        Ubicacion = ?
                    WHERE ID_Fichaje = ?
                """
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, fichaje.dniEmpleado)
                    stmt.setString(2, fichaje.fecha)
                    stmt.setString(3, fichaje.horaEntrada)
                    stmt.setString(4, fichaje.horaSalida)
                    stmt.setString(4, fichaje.ubicacion)
                    stmt.setInt(5, fichaje.id)

                    stmt.executeUpdate()
                }
            } catch (e: SQLException) {
                System.err.println("Error al actualizar el fichaje: ${e.message}")
            }
        }
        System.err.println("Conexion existosa")
        return null
    }

    // Versión alternativa que devuelve el fichaje actualizado
    fun updateAndReturn(fichaje: Fichaje): Fichaje? {
        DBConnection.connect().use { conn ->
            try {
                val query = """
                    UPDATE Fichajes SET 
                        DNI_Empleado = ?,
                        Fecha = ?,
                        Hora_Entrada = ?,
                        Hora_Salida = ?,
                        Ubicacion = ?
                    WHERE ID_Fichaje = ?
                """
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, fichaje.dniEmpleado)
                    stmt.setString(2, fichaje.fecha)
                    stmt.setString(3, fichaje.horaEntrada)
                    stmt.setString(4, fichaje.horaSalida)
                    stmt.setString(4, fichaje.ubicacion)
                    stmt.setInt(5, fichaje.id)

                    val rowsAffected = stmt.executeUpdate()

                    if (rowsAffected > 0) {
                        return getById(fichaje.id) // Devuelve el fichaje actualizado
                    }
                    return null
                }
            } catch (e: SQLException) {
                System.err.println("Error al actualizar el fichaje: ${e.message}")
                return null
            }
        }
    }
}