package view

import controller.EmpleadoController
import controller.FichajeController
import model.Empleado
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Ventana principal de la aplicación que contiene todas las vistas
 */
class MainWindow(private val empleado: Empleado) : JFrame() {

    private val fichajeController = FichajeController()
    private val empleadoController = EmpleadoController()
    private val fichajeView = FichajeView(empleado, fichajeController)
    private val tabbedPane = JTabbedPane()

    init {
        title = "Sistema de Fichajes - ${empleado.nombre} ${empleado.apellido}"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 600)
        setLocationRelativeTo(null)

        // Configurar panel principal
        val mainPanel = JPanel(BorderLayout())
        mainPanel.border = EmptyBorder(10, 10, 10, 10)

        // Panel de cabecera
        val headerPanel = JPanel(BorderLayout())
        val logoLabel = JLabel("Sistema de Fichajes", JLabel.LEFT)
        logoLabel.font = Font(logoLabel.font.name, Font.BOLD, 18)

        val logoutButton = JButton("Cerrar Sesión")
        logoutButton.addActionListener { cerrarSesion() }

        headerPanel.add(logoLabel, BorderLayout.WEST)
        headerPanel.add(logoutButton, BorderLayout.EAST)
        headerPanel.border = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)
        headerPanel.add(Box.createVerticalStrut(30), BorderLayout.SOUTH)

        // Configurar pestañas
        tabbedPane.addTab("Fichajes", JPanel(BorderLayout()).apply { add(fichajeView, BorderLayout.CENTER) })

        // Si el empleado pertenece a RRHH (asumimos idDepartamento = 1 para RRHH)
        if (empleado.idDepartamento == 1) {
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
        adminTabs.addTab("Empleados", crearPanelEmpleados())

        // Pestaña de informes de fichajes
        adminTabs.addTab("Informes", crearPanelInformes())

        panel.add(adminTabs, BorderLayout.CENTER)
        return panel
    }

    private fun crearPanelEmpleados(): JPanel {
        val panel = JPanel(BorderLayout(10, 10))
        panel.border = EmptyBorder(10, 10, 10, 10)

        // Implementación básica, podría expandirse en una clase separada
        val tableModel = DefaultListModel<String>()
        val listaEmpleados = JList(tableModel)

        // Botones de acción
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        buttonPanel.add(JButton("Nuevo").apply {
            addActionListener { JOptionPane.showMessageDialog(this@MainWindow, "Funcionalidad no implementada") }
        })
        buttonPanel.add(JButton("Editar").apply {
            addActionListener { JOptionPane.showMessageDialog(this@MainWindow, "Funcionalidad no implementada") }
        })
        buttonPanel.add(JButton("Eliminar").apply {
            addActionListener { JOptionPane.showMessageDialog(this@MainWindow, "Funcionalidad no implementada") }
        })

        // Cargar empleados
        val empleados = empleadoController.getAllEmpleados()
        for (emp in empleados) {
            tableModel.addElement("${emp.dni} - ${emp.nombre} ${emp.apellido}")
        }

        panel.add(JScrollPane(listaEmpleados), BorderLayout.CENTER)
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }

    private fun crearPanelInformes(): JPanel {
        val panel = JPanel(BorderLayout(10, 10))
        panel.border = EmptyBorder(10, 10, 10, 10)

        // Panel de filtros
        val filtrosPanel = JPanel(GridLayout(3, 2, 10, 10))
        filtrosPanel.add(JLabel("Fecha inicio:"))
        filtrosPanel.add(JTextField())
        filtrosPanel.add(JLabel("Fecha fin:"))
        filtrosPanel.add(JTextField())
        filtrosPanel.add(JLabel("Empleado:"))
        filtrosPanel.add(JComboBox<String>().apply {
            addItem("Todos")
            val empleados = empleadoController.getAllEmpleados()
            for (emp in empleados) {
                addItem("${emp.nombre} ${emp.apellido}")
            }
        })

        // Botón de generar informe
        val btnGenerar = JButton("Generar Informe")
        btnGenerar.addActionListener {
            JOptionPane.showMessageDialog(this, "Funcionalidad no implementada")
        }

        // Área de resultados
        val resultadosArea = JTextArea()
        resultadosArea.isEditable = false

        // Composición del panel
        val topPanel = JPanel(BorderLayout())
        topPanel.add(filtrosPanel, BorderLayout.CENTER)
        topPanel.add(btnGenerar, BorderLayout.SOUTH)

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(JScrollPane(resultadosArea), BorderLayout.CENTER)

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