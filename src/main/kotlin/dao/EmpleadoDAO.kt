package dao

import db.DBConnection
import model.Empleado
import java.sql.SQLException

/**
 * Data Access Object para la entidad Empleado
 */
class EmpleadoDAO {

    /**
     * Obtiene todos los empleados de la base de datos
     * @return Lista de empleados
     */
    fun getAll(): List<Empleado> {
        val empleados = mutableListOf<Empleado>()
        val connection = DBConnection.connect()

        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("""SELECT * FROM "Empleados"""")

            while (resultSet.next()) {
                val empleado = Empleado(
                    dni = resultSet.getString("DNI_Empleado") ?: "",
                    nombre = resultSet.getString("Nombre_Empleado") ?: "",
                    apellido = resultSet.getString("Apellido_Empleado") ?: "",
                    correo = resultSet.getString("Correo_Empleado") ?: "",
                    idDepartamento = resultSet.getInt("ID_Departamento"),
                    telefono = resultSet.getInt("Telefono_Empleado"),
                    contraseña = resultSet.getString("Contraseña") ?: "",
                    rol = resultSet.getString("Rol") ?: ""
                )
                empleados.add(empleado)
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            println("Error al obtener los empleados: ${e.message}")
            e.printStackTrace()
        } finally {
            DBConnection.closeConnection()
        }

        return empleados
    }

    fun getByDni(dni: String): Empleado? {
        val connection = DBConnection.connect()

        try {
            val query = """SELECT * FROM "Empleados" WHERE "DNI_Empleado" = ?"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, dni)

            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                return Empleado(
                    dni = resultSet.getString("DNI_Empleado"),
                    nombre = resultSet.getString("Nombre_Empleado"),
                    apellido = resultSet.getString("Apellido_Empleado"),
                    correo = resultSet.getString("Correo_Empleado"),
                    idDepartamento = resultSet.getInt("ID_Departamento"),
                    telefono = resultSet.getInt("Telefono_Empleado"),
                    contraseña = resultSet.getString("Contraseña"),
                    rol = resultSet.getString("Rol")
                )
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener el empleado con DNI $dni: ${e.message}")
        }

        return null
    }

    fun getByDepartamento(idDepartamento: Int): List<Empleado> {
        val empleados = mutableListOf<Empleado>()
        val connection = DBConnection.connect()

        try {
            val query = """SELECT * FROM "Empleados" WHERE "ID_Departamento" = ?"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, idDepartamento)

            val resultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val empleado = Empleado(
                    dni = resultSet.getString("DNI_Empleado"),
                    nombre = resultSet.getString("Nombre_Empleado"),
                    apellido = resultSet.getString("Apellido_Empleado"),
                    correo = resultSet.getString("Correo_Empleado"),
                    idDepartamento = resultSet.getInt("ID_Departamento"),
                    telefono = resultSet.getInt("Telefono_Empleado"),
                    contraseña = resultSet.getString("Contraseña"),
                    rol = resultSet.getString("Rol")
                )
                empleados.add(empleado)
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener los empleados del departamento $idDepartamento: ${e.message}")
        }

        return empleados
    }

    fun getByDniAndPassword(dni: String, password: String): Empleado? {
        val connection = DBConnection.connect()

        try {
            val query = """SELECT * FROM "Empleados" WHERE "DNI_Empleado" = ? AND "Contraseña" = ?"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, dni)
            preparedStatement.setString(2, password)

            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                return Empleado(
                    dni = resultSet.getString("DNI_Empleado"),
                    nombre = resultSet.getString("Nombre_Empleado"),
                    apellido = resultSet.getString("Apellido_Empleado"),
                    correo = resultSet.getString("Correo_Empleado"),
                    idDepartamento = resultSet.getInt("ID_Departamento"),
                    telefono = resultSet.getInt("Telefono_Empleado"),
                    contraseña = resultSet.getString("Contraseña"),
                    rol = resultSet.getString("Rol")
                )
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener el empleado: ${e.message}")
        }

        return null
    }

    fun insert(empleado: Empleado): Boolean {
        val connection = DBConnection.connect()

        try {
            val query = """
                INSERT INTO "Empleados" (
                    DNI_Empleado, Nombre_Empleado, Apellido_Empleado, 
                    Correo_Empleado, ID_Departamento, Telefono_Empleado, Contraseña, Rol
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, empleado.dni)
            preparedStatement.setString(2, empleado.nombre)
            preparedStatement.setString(3, empleado.apellido)
            preparedStatement.setString(4, empleado.correo)
            preparedStatement.setInt(5, empleado.idDepartamento)
            preparedStatement.setInt(6, empleado.telefono)
            preparedStatement.setString(7, empleado.contraseña)
            preparedStatement.setString(8, empleado.rol)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al insertar el empleado: ${e.message}")
        }

        return false
    }

    fun update(empleado: Empleado): Boolean {
        val connection = DBConnection.connect()

        try {
            val query = """
                UPDATE "Empleados" SET 
                    Nombre_Empleado = ?, 
                    Apellido_Empleado = ?, 
                    Correo_Empleado = ?, 
                    ID_Departamento = ?, 
                    Telefono_Empleado = ?,
                    Contraseña = ?,
                    Rol = ? 
                WHERE DNI_Empleado = ?
            """
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, empleado.nombre)
            preparedStatement.setString(2, empleado.apellido)
            preparedStatement.setString(3, empleado.correo)
            preparedStatement.setInt(4, empleado.idDepartamento)
            preparedStatement.setInt(5, empleado.telefono)
            preparedStatement.setString(6, empleado.contraseña)
            preparedStatement.setString(7, empleado.dni)
            preparedStatement.setString(8, empleado.rol)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al actualizar el empleado: ${e.message}")
        }

        return false
    }

    fun delete(dni: String): Boolean {
        val connection = DBConnection.connect()

        try {
            val query = """DELETE FROM "Empleados" WHERE "DNI_Empleado" = ?"""
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, dni)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al eliminar el empleado: ${e.message}")
        }

        return false
    }
}
