import controller.EmpleadoController
import controller.FichajeController
import view.LoginView
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Clase principal que inicia la aplicación
 */
fun main() {
    try {
        // Intentar establecer el look and feel del sistema
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // Inicializar controladores
    val empleadoController = EmpleadoController()
    val fichajeController = FichajeController()

    // Iniciar la aplicación en el hilo de eventos de Swing
    SwingUtilities.invokeLater {
        try {
            // Verificar la conexión a la base de datos
            val empleados = empleadoController.getAllEmpleados()
            println("Conexión exitosa. ${empleados.size} empleados encontrados.")

            // Mostrar la ventana de login
            val loginView = LoginView(empleadoController)
            loginView.isVisible = true
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al iniciar la aplicación: ${e.message}")
        }
    }
}