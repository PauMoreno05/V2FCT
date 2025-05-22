package view

import controller.EmpleadoController
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*

class LoginView(private val empleadoController: EmpleadoController) : JFrame() {

    private val txtDNI = JTextField(15)
    private val txtPassword = JPasswordField(15)  // Campo de contraseña añadido
    private val btnLogin = JButton("Iniciar Sesión")
    private val btnSalir = JButton("Salir")

    init {
        title = "Sistema de Fichajes - Login"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 250)  // Aumentado el tamaño para el nuevo campo
        setLocationRelativeTo(null)

        // Panel principal
        val mainPanel = JPanel(BorderLayout(10, 10))
        mainPanel.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)

        // Panel de formulario
        val formPanel = JPanel(GridLayout(2, 2, 10, 10))  // Cambiado a 2 filas
        formPanel.add(JLabel("DNI:"))
        formPanel.add(txtDNI)
        formPanel.add(JLabel("Contraseña:"))
        formPanel.add(txtPassword)

        // Panel de botones
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttonPanel.add(btnLogin)
        buttonPanel.add(btnSalir)

        // Agregar componentes al panel principal
        mainPanel.add(JLabel("Bienvenido al Sistema de Fichajes", JLabel.CENTER), BorderLayout.NORTH)
        mainPanel.add(formPanel, BorderLayout.CENTER)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)

        // Agregar panel principal al frame
        contentPane = mainPanel

        // Configurar eventos
        btnLogin.addActionListener { iniciarSesion() }
        btnSalir.addActionListener { System.exit(0) }

        // Hacer que Enter funcione como clic en el botón login
        rootPane.defaultButton = btnLogin
    }

    private fun iniciarSesion() {
        val dni = txtDNI.text.trim()
        val password = String(txtPassword.password)  // Obtener la contraseña

        if (dni.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, introduzca su DNI y contraseña",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        val empleado = empleadoController.autenticarEmpleado(dni, password)

        if (empleado != null) {
            // Empleado autenticado, mostrar ventana principal
            dispose()
            val mainWindow = MainWindow(empleado)
            mainWindow.isVisible = true
        } else {
            JOptionPane.showMessageDialog(
                this,
                "DNI o contraseña incorrectos. Por favor, verifique sus datos.",
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE
            )
            txtPassword.text = ""  // Limpiar campo de contraseña
        }
    }
}