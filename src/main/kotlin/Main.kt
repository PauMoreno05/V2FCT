import controller.EmpleadoController
import controller.FichajeController
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.Serializable
import view.LoginView
import javax.swing.SwingUtilities
import javax.swing.UIManager

val supabase = createSupabaseClient(
    supabaseUrl = "https://ibrusfgcyblkfzcttujk.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlicnVzZmdjeWJsa2Z6Y3R0dWprIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc4MTk3NzgsImV4cCI6MjA2MzM5NTc3OH0.IIaLAF3OcMHz9mO7g7VYpoBHie92-7oWu8j1CgFCF3A"
) {
    install(Postgrest)
}


@Serializable
data class Empleados(
    val DNI_Empleado: String,
    val Nombre_Empleado: String,
    val Apellido_Empleado: String,
    val Correo_Empleado: String,
    var ID_Departamento: Int,
    var Telefono_Empleado: String,
    var Contrasesña: String,
)

@Serializable
data class Ficahjes(
    val ID_Fichaje: Int,
    val DNI_Empleado: String,
    val Fecha: String,
    val Hora_Entrada: String,
    var Hora_Salida: String,
)

@Serializable
data class Departamentos(
    val ID_Departamento: Int,
    val Nombre_Departamento: String,
)


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