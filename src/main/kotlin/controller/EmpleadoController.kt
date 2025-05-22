package controller

import dao.EmpleadoDAO
import dao.DepartamentoDAO
import model.Empleado
import model.Departamento

/**
 * Controlador para la gestión de empleados
 */
class EmpleadoController {
    private val empleadoDAO = EmpleadoDAO()
    private val departamentoDAO = DepartamentoDAO()

    /**
     * Obtiene todos los empleados
     * @return Lista de empleados
     */
    fun getAllEmpleados(): List<Empleado> {
        return empleadoDAO.getAll()
    }

    /**
     * Obtiene un empleado por su DNI
     * @param dni DNI del empleado
     * @return Empleado encontrado o null si no existe
     */
    fun getEmpleadoByDni(dni: String): Empleado? {
        return empleadoDAO.getByDni(dni)
    }

    /**
     * Obtiene empleados por departamento
     * @param idDepartamento ID del departamento
     * @return Lista de empleados del departamento
     */
    fun getEmpleadosByDepartamento(idDepartamento: Int): List<Empleado> {
        return empleadoDAO.getByDepartamento(idDepartamento)
    }

    /**
     * Crea un nuevo empleado
     * @param dni DNI del empleado
     * @param nombre Nombre del empleado
     * @param apellido Apellido del empleado
     * @param correo Correo electrónico del empleado
     * @param idDepartamento ID del departamento
     * @param telefono Número de teléfono del empleado
     * @return true si se crea correctamente, false en caso contrario
     */
    fun createEmpleado(
        dni: String,
        nombre: String,
        apellido: String,
        correo: String,
        idDepartamento: Int,
        telefono: Int,
        contraseña: String
    ): Boolean {
        // Validación básica de datos
        if (dni.isBlank() || nombre.isBlank() || apellido.isBlank() ||
            correo.isBlank() || contraseña.isBlank()) {
            return false
        }

        // Verificar que el departamento existe
        val departamento = departamentoDAO.getById(idDepartamento)
        if (departamento == null) {
            return false
        }

        // Verificar que el DNI no esté ya registrado
        if (empleadoDAO.getByDni(dni) != null) {
            return false
        }

        val empleado = Empleado(
            dni = dni,
            nombre = nombre,
            apellido = apellido,
            correo = correo,
            idDepartamento = idDepartamento,
            telefono = telefono,
            contraseña = contraseña
        )

        return empleadoDAO.insert(empleado)
    }

    /**
     * Actualiza un empleado existente
     * @param empleado Empleado con los nuevos datos
     * @return true si se actualiza correctamente, false en caso contrario
     */
    fun updateEmpleado(empleado: Empleado): Boolean {
        // Validación básica de datos
        if (empleado.dni.isBlank() || empleado.nombre.isBlank() || empleado.apellido.isBlank() || empleado.correo.isBlank()) {
            return false
        }

        // Verificar que el departamento existe
        val departamento = departamentoDAO.getById(empleado.idDepartamento)
        if (departamento == null) {
            return false
        }

        // Verificar que el empleado existe
        if (empleadoDAO.getByDni(empleado.dni) == null) {
            return false
        }

        return empleadoDAO.update(empleado)
    }

    /**
     * Elimina un empleado
     * @param dni DNI del empleado a eliminar
     * @return true si se elimina correctamente, false en caso contrario
     */
    fun deleteEmpleado(dni: String): Boolean {
        return empleadoDAO.delete(dni)
    }

    /**
     * Obtiene una lista de todos los departamentos
     * @return Lista de departamentos
     */
    fun getAllDepartamentos(): List<Departamento> {
        return departamentoDAO.getAll()
    }

    /**
     * Obtiene un departamento por su ID
     * @param id ID del departamento
     * @return Departamento encontrado o null si no existe
     */
    fun getDepartamentoById(id: Int): Departamento? {
        return departamentoDAO.getById(id)
    }
    /**
     * Autentica a un empleado por DNI y contraseña
     */
    fun autenticarEmpleado(dni: String, contraseña: String): Empleado? {
        return empleadoDAO.getByDniAndPassword(dni, contraseña)
    }
}

