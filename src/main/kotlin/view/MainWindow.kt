package view

import controller.EmpleadoController
import controller.FichajeController
import model.Empleado
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Ventana principal de la aplicación que contiene todas las vistas
 * Ahora diferencia entre roles Root y empleados normales
 */
class MainWindow(private val empleado: Empleado) : JFrame() {

    private val fichajeController = FichajeController()
    private val empleadoController = EmpleadoController()
    private val fichajeView = FichajeView(empleado, fichajeController)
    private val tabbedPane = JTabbedPane()

    init {
        title = "Sistema de Fichajes - ${empleado.nombre} ${empleado.apellido} [${empleado.rol}]"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(if (empleado.rol.equals("Root", ignoreCase = true)) 900 else 800, 600)
        setLocationRelativeTo(null)

        // Configurar panel principal
        val mainPanel = JPanel(BorderLayout())
        mainPanel.border = EmptyBorder(10, 10, 10, 10)

        // Panel de cabecera
        val headerPanel = JPanel(BorderLayout())
        val logoLabel = JLabel("Sistema de Fichajes", JLabel.LEFT)
        logoLabel.font = Font(logoLabel.font.name, Font.BOLD, 18)

        // Mostrar rol si es Root
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(logoLabel)
        if (empleado.rol.equals("Root", ignoreCase = true)) {
            val rolLabel = JLabel("[ADMINISTRADOR]")
            rolLabel.font = Font(rolLabel.font.name, Font.BOLD, 12)
            rolLabel.foreground = Color.RED
            titlePanel.add(rolLabel)
        }

        val logoutButton = JButton("Cerrar Sesión")
        logoutButton.addActionListener { cerrarSesion() }

        headerPanel.add(titlePanel, BorderLayout.WEST)
        headerPanel.add(logoutButton, BorderLayout.EAST)
        headerPanel.border = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)
        headerPanel.add(Box.createVerticalStrut(30), BorderLayout.SOUTH)

        // Configurar pestañas
        tabbedPane.addTab("Fichajes", JPanel(BorderLayout()).apply { add(fichajeView, BorderLayout.CENTER) })

        // Agregar pestaña de calendario para todos los usuarios
        val calendarioView = CalendarioView(empleado, fichajeController)
        tabbedPane.addTab("📅 Calendario", JPanel(BorderLayout()).apply { add(calendarioView, BorderLayout.CENTER) })

        // Si el empleado tiene rol Root, mostrar panel de administración
        if (empleado.rol.equals("Root", ignoreCase = true)) {
            tabbedPane.addTab("Administración", crearPanelAdmin())
        }

        // Agregar componentes al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH)
        mainPanel.add(tabbedPane, BorderLayout.CENTER)

        // Establecer panel principal como contenido del frame
        contentPane = mainPanel
    }

    private fun crearPanelAdmin(): JPanel {
        val panel = JPanel(BorderLayout(10, 10))
        panel.border = EmptyBorder(20, 20, 20, 20)

        // Crear un panel con pestañas para las diferentes opciones de administración
        val adminTabs = JTabbedPane()

        // Pestaña de gestión de empleados
        //adminTabs.addTab("Empleados", crearPanelEmpleados())

        // Pestaña de informes de fichajes
        //adminTabs.addTab("Informes", crearPanelInformes())

        // Pestaña adicional para Root: Gestión de roles
        adminTabs.addTab("Gestión Roles", crearPanelGestionRoles())

        panel.add(adminTabs, BorderLayout.CENTER)
        return panel
    }

    private fun crearPanelGestionRoles(): JPanel {
        val panel = JPanel(BorderLayout(10, 10))
        panel.border = EmptyBorder(10, 10, 10, 10)

        // Panel superior con información
        val infoPanel = JPanel()
        val infoLabel = JLabel("Gestión de Roles de Usuario")
        infoLabel.font = Font(infoLabel.font.name, Font.BOLD, 14)
        infoPanel.add(infoLabel)

        // Panel central con lista de empleados y sus roles
        val empleadosRolesModel = DefaultListModel<String>()
        val listaEmpleadosRoles = JList(empleadosRolesModel)

        // Cargar empleados con sus roles
        val empleados = empleadoController.getAllEmpleados()
        for (emp in empleados) {
            empleadosRolesModel.addElement("${emp.dni} - ${emp.nombre} ${emp.apellido} - Rol: ${emp.rol}")
        }

        // Panel de botones para cambiar roles
        val buttonPanel = JPanel(FlowLayout())
        val btnCambiarRol = JButton("Cambiar Rol")
        btnCambiarRol.addActionListener {
            val selectedIndex = listaEmpleadosRoles.selectedIndex
            if (selectedIndex != -1) {
                val roles = arrayOf("Root", "Empleado", "Supervisor")
                val nuevoRol = JOptionPane.showInputDialog(
                    this@MainWindow,
                    "Seleccione el nuevo rol:",
                    "Cambiar Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    roles,
                    roles[1]
                ) as String?

                if (nuevoRol != null) {
                    JOptionPane.showMessageDialog(this@MainWindow, "Rol cambiado a: $nuevoRol\n(Funcionalidad no implementada)")
                    // Aquí iría la lógica para actualizar el rol en la BD
                }
            } else {
                JOptionPane.showMessageDialog(this@MainWindow, "Seleccione un empleado")
            }
        }
        buttonPanel.add(btnCambiarRol)

        panel.add(infoPanel, BorderLayout.NORTH)
        panel.add(JScrollPane(listaEmpleadosRoles), BorderLayout.CENTER)
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }

    private fun cerrarSesion() {
        // Detener temporizadores y liberar recursos
        fichajeView.detener()

        // Cerrar ventana actual y mostrar login
        dispose()
        val loginView = LoginView(empleadoController)
        loginView.isVisible = true
    }

    override fun dispose() {
        fichajeView.detener()
        super.dispose()
    }
}