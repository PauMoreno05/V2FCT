package view

import controller.FichajeController
import model.Empleado
import java.awt.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.Timer
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel

/**
 * Vista principal para el registro de fichajes
 */
class FichajeView(private val empleado: Empleado, private val fichajeController: FichajeController) : JPanel() {

    private val lblEmpleado = JLabel()
    private val lblFecha = JLabel()
    private val lblHora = JLabel()
    private val btnEntrada = JButton("Registrar Entrada")
    private val btnSalida = JButton("Registrar Salida")
    private val tableModel = DefaultTableModel()
    private val tableFichajes = JTable(tableModel)
    private val timer = Timer(1000) { actualizarHora() }

    init {
        layout = BorderLayout(10, 10)
        border = EmptyBorder(20, 20, 20, 20)

        // Panel superior con información del empleado y fecha/hora
        val panelInfo = JPanel(GridLayout(3, 1, 5, 5))
        lblEmpleado.text = "Empleado: ${empleado.nombre} ${empleado.apellido} (${empleado.dni})"
        lblEmpleado.font = Font(lblEmpleado.font.name, Font.BOLD, 14)

        // Inicializar fecha y hora
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        lblFecha.text = "Fecha: ${dateFormat.format(Date())}"

        actualizarHora()

        panelInfo.add(lblEmpleado)
        panelInfo.add(lblFecha)
        panelInfo.add(lblHora)

        // Panel central con botones de fichaje
        val panelBotones = JPanel(FlowLayout(FlowLayout.CENTER, 20, 10))
        btnEntrada.preferredSize = Dimension(150, 40)
        btnSalida.preferredSize = Dimension(150, 40)
        panelBotones.add(btnEntrada)
        panelBotones.add(btnSalida)

        // Panel inferior con tabla de fichajes
        configurarTabla()
        val scrollPane = JScrollPane(tableFichajes)
        scrollPane.preferredSize = Dimension(600, 200)

        // Agregar componentes al panel principal
        add(panelInfo, BorderLayout.NORTH)
        add(panelBotones, BorderLayout.CENTER)
        add(scrollPane, BorderLayout.SOUTH)

        // Configurar eventos
        btnEntrada.addActionListener { registrarEntrada() }
        btnSalida.addActionListener { registrarSalida() }

        // Iniciar temporizador para actualizar la hora
        timer.start()

        // Cargar fichajes del empleado
        cargarFichajes()
    }

    private fun configurarTabla() {
        // Configurar columnas de la tabla
        tableModel.addColumn("Fecha")
        tableModel.addColumn("Hora Entrada")
        tableModel.addColumn("Hora Salida")

        tableFichajes.columnModel.getColumn(0).preferredWidth = 150
        tableFichajes.columnModel.getColumn(1).preferredWidth = 150
        tableFichajes.columnModel.getColumn(2).preferredWidth = 150

        tableFichajes.rowHeight = 25
        tableFichajes.font = Font(tableFichajes.font.name, Font.PLAIN, 12)
        tableFichajes.tableHeader.font = Font(tableFichajes.tableHeader.font.name, Font.BOLD, 12)
    }

    private fun actualizarHora() {
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        lblHora.text = "Hora: ${timeFormat.format(Date())}"
    }

    private fun registrarEntrada() {
        try {
            val fichaje = fichajeController.registrarEntrada(empleado.dni)
            JOptionPane.showMessageDialog(
                this,
                "Entrada registrada correctamente",
                "Fichaje Registrado",
                JOptionPane.INFORMATION_MESSAGE
            )
            cargarFichajes()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                this,
                "Error al registrar entrada: ${e.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun registrarSalida() {
        try {
            val fichaje = fichajeController.registrarSalida(empleado.dni)
            if (fichaje != null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Salida registrada correctamente",
                    "Fichaje Registrado",
                    JOptionPane.INFORMATION_MESSAGE
                )
                cargarFichajes()
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No hay entrada registrada para hoy",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
                )
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                this,
                "Error al registrar salida: ${e.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun cargarFichajes() {
        // Limpiar tabla
        tableModel.setRowCount(0)

        // Obtener fichajes del empleado
        val fichajes = fichajeController.getFichajesByEmpleado(empleado.dni)

        // Agregar fichajes a la tabla
        for (fichaje in fichajes) {
            tableModel.addRow(arrayOf(
                fichaje.fecha,
                fichaje.horaEntrada,
                fichaje.horaSalida
            ))
        }
    }

    fun detener() {
        timer.stop()
    }
}
