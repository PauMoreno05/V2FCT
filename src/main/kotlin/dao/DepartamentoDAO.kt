package dao

import db.DBConnection
import model.Departamento
import java.sql.SQLException

/**
 * Data Access Object para la entidad Departamento
 */
class DepartamentoDAO {

    /**
     * Obtiene todos los departamentos de la base de datos
     * @return Lista de departamentos
     */
    fun getAll(): List<Departamento> {
        val departamentos = mutableListOf<Departamento>()
        val connection = DBConnection.connect()

        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM Departamentos")

            while (resultSet.next()) {
                val departamento = Departamento(
                    id = resultSet.getInt("ID_Departamento"),
                    nombre = resultSet.getString("Nombre_Departamento")
                )
                departamentos.add(departamento)
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            println("Error al obtener los departamentos: ${e.message}")
        }

        return departamentos
    }

    /**
     * Obtiene un departamento por su ID
     * @param id ID del departamento a buscar
     * @return Departamento encontrado o null si no existe
     */
    fun getById(id: Int): Departamento? {
        val connection = DBConnection.connect()

        try {
            val query = "SELECT * FROM Departamentos WHERE ID_Departamento = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, id)

            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                return Departamento(
                    id = resultSet.getInt("ID_Departamento"),
                    nombre = resultSet.getString("Nombre_Departamento")
                )
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error al obtener el departamento con ID $id: ${e.message}")
        }

        return null
    }

    /**
     * Inserta un nuevo departamento en la base de datos
     * @param departamento Objeto Departamento a insertar
     * @return ID del departamento insertado o -1 si falla
     */
    fun insert(departamento: Departamento): Int {
        val connection = DBConnection.connect()

        try {
            val query = "INSERT INTO Departamentos (Nombre_Departamento) VALUES (?)"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, departamento.nombre)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            if (rowsAffected > 0) {
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT last_insert_rowid()")

                if (resultSet.next()) {
                    return resultSet.getInt(1)
                }

                resultSet.close()
                statement.close()
            }
        } catch (e: SQLException) {
            println("Error al insertar el departamento: ${e.message}")
        }

        return -1
    }

    /**
     * Actualiza un departamento existente en la base de datos
     * @param departamento Objeto Departamento con los nuevos datos
     * @return true si se actualiza correctamente, false en caso contrario
     */
    fun update(departamento: Departamento): Boolean {
        val connection = DBConnection.connect()

        try {
            val query = "UPDATE Departamentos SET Nombre_Departamento = ? WHERE ID_Departamento = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, departamento.nombre)
            preparedStatement.setInt(2, departamento.id)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al actualizar el departamento: ${e.message}")
        }

        return false
    }

    /**
     * Elimina un departamento de la base de datos
     * @param id ID del departamento a eliminar
     * @return true si se elimina correctamente, false en caso contrario
     */
    fun delete(id: Int): Boolean {
        val connection = DBConnection.connect()

        try {
            val query = "DELETE FROM Departamentos WHERE ID_Departamento = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            preparedStatement.close()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Error al eliminar el departamento: ${e.message}")
        }

        return false
    }
}