package view

import controller.FichajeController
import model.Empleado
import model.Fichaje
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * Vista del calendario para consulta de fichajes
 * Permite navegar por fechas y ver detalles de los fichajes
 */
class CalendarioView(
    private val empleado: Empleado,
    private val fichajeController: FichajeController
) : JPanel() {

    private var fechaActual = LocalDate.now()
    private var fechaSeleccionada = LocalDate.now() // Nueva variable para tracking del día seleccionado
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Componentes de la interfaz
    private val labelMesAnio = JLabel()
    private val panelCalendario = JPanel()
    private val panelDetalles = JPanel()
    private val textAreaDetalles = JTextArea()
    private val labelResumen = JLabel()

    // Colores para el calendario
    private val colorDiaConFichajes = Color(144, 238, 144) // Verde claro
    private val colorDiaSeleccionado = Color(173, 216, 230) // Azul claro
    private val colorDiaHoy = Color(255, 215, 0) // Amarillo dorado para el día actual
    private val colorDiaSinFichajes = Color.WHITE
    private val colorDiaOtroMes = Color.LIGHT_GRAY

    init {
        layout = BorderLayout(10, 10)
        border = EmptyBorder(20, 20, 20, 20)

        crearInterfaz()
        actualizarCalendario()
        mostrarDetallesFecha(fechaSeleccionada)
    }

    private fun crearInterfaz() {
        // Panel superior con navegación
        val panelSuperior = crearPanelNavegacion()
        add(panelSuperior, BorderLayout.NORTH)

        // Panel central dividido
        val panelCentral = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        panelCentral.leftComponent = crearPanelCalendario()
        panelCentral.rightComponent = crearPanelDetalles()
        panelCentral.dividerLocation = 400
        panelCentral.resizeWeight = 0.6

        add(panelCentral, BorderLayout.CENTER)
    }

    private fun crearPanelNavegacion(): JPanel {
        val panel = JPanel(BorderLayout())

        // Botones de navegación
        val btnAnterior = JButton("◀ Mes Anterior")
        val btnSiguiente = JButton("Mes Siguiente ▶")
        val btnHoy = JButton("Hoy")

        btnAnterior.addActionListener { cambiarMes(-1) }
        btnSiguiente.addActionListener { cambiarMes(1) }
        btnHoy.addActionListener { irAHoy() }

        // Label del mes y año
        labelMesAnio.font = Font(labelMesAnio.font.name, Font.BOLD, 16)
        labelMesAnio.horizontalAlignment = SwingConstants.CENTER

        // Panel de botones
        val panelBotones = JPanel(FlowLayout())
        panelBotones.add(btnAnterior)
        panelBotones.add(btnHoy)
        panelBotones.add(btnSiguiente)

        panel.add(panelBotones, BorderLayout.WEST)
        panel.add(labelMesAnio, BorderLayout.CENTER)

        return panel
    }

    private fun crearPanelCalendario(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder("Calendario")

        // Panel para el calendario
        panelCalendario.layout = GridLayout(7, 7, 2, 2) // 7x7 para incluir cabeceras
        panel.add(panelCalendario, BorderLayout.CENTER)

        // Leyenda
        val leyenda = crearLeyenda()
        panel.add(leyenda, BorderLayout.SOUTH)

        return panel
    }

    private fun crearLeyenda(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.border = BorderFactory.createTitledBorder("Leyenda")

        panel.add(crearIndicadorColor("Con fichajes", colorDiaConFichajes))
        panel.add(Box.createHorizontalStrut(10))
        panel.add(crearIndicadorColor("Sin fichajes", colorDiaSinFichajes))
        panel.add(Box.createHorizontalStrut(10))
        panel.add(crearIndicadorColor("Hoy", colorDiaHoy))
        panel.add(Box.createHorizontalStrut(10))
        panel.add(crearIndicadorColor("Seleccionado", colorDiaSeleccionado))

        return panel
    }

    private fun crearIndicadorColor(texto: String, color: Color): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT, 2, 0))
        val colorLabel = JLabel("■")
        colorLabel.foreground = color
        colorLabel.font = Font(colorLabel.font.name, Font.BOLD, 12)
        panel.add(colorLabel)
        panel.add(JLabel(texto))
        return panel
    }

    private fun crearPanelDetalles(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder("Detalles del día")

        // Label de resumen
        labelResumen.font = Font(labelResumen.font.name, Font.BOLD, 12)
        labelResumen.border = EmptyBorder(5, 5, 10, 5)
        panel.add(labelResumen, BorderLayout.NORTH)

        // Área de texto para detalles
        textAreaDetalles.isEditable = false
        textAreaDetalles.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        textAreaDetalles.background = Color.WHITE

        val scrollPane = JScrollPane(textAreaDetalles)
        scrollPane.preferredSize = Dimension(300, 200)
        panel.add(scrollPane, BorderLayout.CENTER)

        // Panel de botones para generar informes
        val panelBotones = JPanel(FlowLayout())
        val btnInformeSemanal = JButton("Informe Semanal")
        val btnInformeMensual = JButton("Informe Mensual")

        btnInformeSemanal.addActionListener { generarInformeSemanal() }
        btnInformeMensual.addActionListener { generarInformeMensual() }

        panelBotones.add(btnInformeSemanal)
        panelBotones.add(btnInformeMensual)
        panel.add(panelBotones, BorderLayout.SOUTH)

        return panel
    }

    private fun actualizarCalendario() {
        panelCalendario.removeAll()

        // Actualizar label del mes y año
        val nombreMes = fechaActual.month.getDisplayName(TextStyle.FULL, Locale("es"))
        labelMesAnio.text = "${nombreMes.capitalize()} ${fechaActual.year}"

        // Agregar cabeceras de días de la semana
        val diasSemana = arrayOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        for (dia in diasSemana) {
            val label = JLabel(dia, SwingConstants.CENTER)
            label.font = Font(label.font.name, Font.BOLD, 10)
            label.border = BorderFactory.createEtchedBorder()
            panelCalendario.add(label)
        }

        // Obtener primer día del mes y número de días
        val primerDiaMes = fechaActual.withDayOfMonth(1)
        val ultimoDiaMes = fechaActual.withDayOfMonth(fechaActual.lengthOfMonth())
        val primerLunes = primerDiaMes.minusDays((primerDiaMes.dayOfWeek.value - 1).toLong())

        // Obtener fichajes del mes para optimizar consultas
        val fichajesDelMes = obtenerFichajesDelMes(fechaActual)

        // Generar calendario
        var fecha = primerLunes
        for (i in 0 until 42) { // 6 semanas máximo
            if (fecha.isAfter(ultimoDiaMes.plusWeeks(1))) break

            val btnFecha = crearBotonFecha(fecha, fichajesDelMes)
            panelCalendario.add(btnFecha)
            fecha = fecha.plusDays(1)
        }

        panelCalendario.revalidate()
        panelCalendario.repaint()
    }

    private fun crearBotonFecha(fecha: LocalDate, fichajesDelMes: List<Fichaje>): JButton {
        val btn = JButton(fecha.dayOfMonth.toString())
        btn.preferredSize = Dimension(50, 40)
        btn.font = Font(btn.font.name, Font.PLAIN, 10)

        // Determinar color de fondo basado en prioridades
        val tieneFichajes = fichajesDelMes.any { fichaje ->
            val fechaParsed = parsearFecha(fichaje.fecha)
            fechaParsed == fecha
        }

        val mesActual = YearMonth.from(fechaActual)
        val mesFecha = YearMonth.from(fecha)
        val esHoy = fecha == LocalDate.now()
        val esSeleccionado = fecha == fechaSeleccionada

        when {
            mesFecha != mesActual -> {
                btn.background = colorDiaOtroMes
                btn.isEnabled = false
            }
            esSeleccionado -> {
                btn.background = colorDiaSeleccionado
                btn.font = Font(btn.font.name, Font.BOLD, 11)
                btn.border = BorderFactory.createLineBorder(Color.BLUE, 2)
            }
            esHoy -> {
                btn.background = colorDiaHoy
                btn.font = Font(btn.font.name, Font.BOLD, 11)
                btn.border = BorderFactory.createLineBorder(Color.ORANGE, 2)
            }
            tieneFichajes -> {
                btn.background = colorDiaConFichajes
            }
            else -> {
                btn.background = colorDiaSinFichajes
            }
        }

        // Agregar listener para seleccionar fecha
        btn.addActionListener {
            seleccionarFecha(fecha)
        }

        return btn
    }

    private fun seleccionarFecha(fecha: LocalDate) {
        // Solo permitir seleccionar fechas del mes actual
        val mesActual = YearMonth.from(fechaActual)
        val mesFecha = YearMonth.from(fecha)

        if (mesFecha == mesActual) {
            fechaSeleccionada = fecha
            actualizarCalendario() // Refrescar calendario para mostrar nueva selección
            mostrarDetallesFecha(fecha)
        }
    }

    private fun obtenerFichajesDelMes(fecha: LocalDate): List<Fichaje> {
        val primerDia = fecha.withDayOfMonth(1).format(dateFormatter)
        val ultimoDia = fecha.withDayOfMonth(fecha.lengthOfMonth()).format(dateFormatter)

        var fichajes = fichajeController.getFichajesPorEmpleadoYRangoFechas(
            empleado.dni, primerDia, ultimoDia
        )

        // Si no encuentra fichajes con formato dd/MM/yyyy, intentar con yyyy-MM-dd
        if (fichajes.isEmpty()) {
            val primerDiaDb = fecha.withDayOfMonth(1).format(dbDateFormatter)
            val ultimoDiaDb = fecha.withDayOfMonth(fecha.lengthOfMonth()).format(dbDateFormatter)

            fichajes = fichajeController.getFichajesPorEmpleadoYRangoFechas(
                empleado.dni, primerDiaDb, ultimoDiaDb
            )
        }

        return fichajes
    }

    private fun parsearFecha(fechaStr: String): LocalDate? {
        return try {
            // Intentar primero con formato dd/MM/yyyy
            LocalDate.parse(fechaStr, dateFormatter)
        } catch (e: Exception) {
            try {
                // Si falla, intentar con formato yyyy-MM-dd
                LocalDate.parse(fechaStr, dbDateFormatter)
            } catch (e2: Exception) {
                try {
                    // Si también falla, intentar con formato ISO estándar
                    LocalDate.parse(fechaStr)
                } catch (e3: Exception) {
                    try {
                        // Intentar con formato dd-MM-yyyy (guiones)
                        val dashFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        LocalDate.parse(fechaStr, dashFormatter)
                    } catch (e4: Exception) {
                        try {
                            // Intentar con formato MM/dd/yyyy (formato americano)
                            val usFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                            LocalDate.parse(fechaStr, usFormatter)
                        } catch (e5: Exception) {
                            println("Error al parsear fecha: $fechaStr - Formatos intentados: dd/MM/yyyy, yyyy-MM-dd, ISO, dd-MM-yyyy, MM/dd/yyyy")
                            null
                        }
                    }
                }
            }
        }
    }

    private fun mostrarDetallesFecha(fecha: LocalDate) {
        val fechaStr = fecha.format(dateFormatter)

        // Debug: Intentar múltiples formatos de fecha
        var fichajes = fichajeController.getFichajesPorEmpleadoYFecha(empleado.dni, fechaStr)

        // Si no encuentra fichajes con formato dd/MM/yyyy, intentar con yyyy-MM-dd
        if (fichajes.isEmpty()) {
            val fechaDbFormat = fecha.format(dbDateFormatter)
            fichajes = fichajeController.getFichajesPorEmpleadoYFecha(empleado.dni, fechaDbFormat)
        }

        // Si aún no encuentra, intentar con formato ISO
        if (fichajes.isEmpty()) {
            val fechaIsoFormat = fecha.toString()
            fichajes = fichajeController.getFichajesPorEmpleadoYFecha(empleado.dni, fechaIsoFormat)
        }

        if (fichajes.isEmpty()) {
            labelResumen.text = "📅 ${fechaStr} - Sin fichajes registrados"
            textAreaDetalles.text = """
                Sin fichajes registrados para esta fecha.
                
                ${if (fecha == LocalDate.now()) "🔔 Es el día de hoy" else ""}
                ${if (fecha.dayOfWeek.value >= 6) "🎉 Es fin de semana" else ""}
            """.trimIndent()
            return
        }

        // Calcular resumen
        val totalHoras = calcularTotalHoras(fichajes)
        val estadoFichaje = if (fichajes.any { it.horaSalida.isEmpty() }) " (En curso)" else ""
        labelResumen.text = "📅 ${fechaStr} - Total: ${formatearDuracion(totalHoras)}$estadoFichaje"

        // Mostrar detalles mejorados
        val sb = StringBuilder()
        sb.append("FICHAJES DEL DÍA\n")
        sb.append("=".repeat(40)).append("\n\n")

        fichajes.sortedBy { it.horaEntrada }.forEachIndexed { index, fichaje ->
            sb.append("📍 FICHAJE ${index + 1}\n")
            sb.append("⏰ Entrada: ${fichaje.horaEntrada}\n")
            if (fichaje.horaSalida.isNotEmpty()) {
                sb.append("⏰ Salida:  ${fichaje.horaSalida}\n")
                val duracion = calcularDuracionFichaje(fichaje)
                sb.append("⌛ Duración: ${formatearDuracion(duracion)}\n")
            } else {
                sb.append("⏰ Salida:  -- (🔴 En curso)\n")
                sb.append("⌛ Tiempo actual: ${calcularTiempoTranscurrido(fichaje.horaEntrada)}\n")
            }
            sb.append("-".repeat(25)).append("\n")
        }

        // Agregar información adicional
        if (fichajes.size > 1) {
            sb.append("\n📊 RESUMEN:\n")
            sb.append("   • Número de fichajes: ${fichajes.size}\n")
            sb.append("   • Tiempo total trabajado: ${formatearDuracion(totalHoras)}\n")
        }

        textAreaDetalles.text = sb.toString()
    }

    private fun calcularTiempoTranscurrido(horaEntrada: String): String {
        return try {
            val ahora = java.time.LocalTime.now()
            val entrada = java.time.LocalTime.parse(horaEntrada)
            val duracion = java.time.Duration.between(entrada, ahora)
            formatearDuracion(duracion.toMinutes().toInt())
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun calcularTotalHoras(fichajes: List<Fichaje>): Int {
        return fichajes.sumOf { calcularDuracionFichaje(it) }
    }

    private fun calcularDuracionFichaje(fichaje: Fichaje): Int {
        if (fichaje.horaSalida.isEmpty()) return 0

        try {
            val entrada = fichaje.horaEntrada.split(":")
            val salida = fichaje.horaSalida.split(":")

            val minutosEntrada = entrada[0].toInt() * 60 + entrada[1].toInt()
            val minutosSalida = salida[0].toInt() * 60 + salida[1].toInt()

            return minutosSalida - minutosEntrada
        } catch (e: Exception) {
            return 0
        }
    }

    private fun formatearDuracion(minutos: Int): String {
        val horas = minutos / 60
        val mins = minutos % 60
        return String.format("%02d:%02d", horas, mins)
    }

    private fun cambiarMes(incremento: Int) {
        fechaActual = fechaActual.plusMonths(incremento.toLong())
        // Mantener la fecha seleccionada si está en el nuevo mes
        val mesNuevo = YearMonth.from(fechaActual)
        val mesSeleccionado = YearMonth.from(fechaSeleccionada)

        if (mesNuevo != mesSeleccionado) {
            fechaSeleccionada = fechaActual
        }

        actualizarCalendario()
        mostrarDetallesFecha(fechaSeleccionada)
    }

    private fun irAHoy() {
        fechaActual = LocalDate.now()
        fechaSeleccionada = LocalDate.now()
        actualizarCalendario()
        mostrarDetallesFecha(fechaSeleccionada)
    }

    private fun generarInformeSemanal() {
        val inicioSemana = fechaSeleccionada.minusDays((fechaSeleccionada.dayOfWeek.value - 1).toLong())
        val finSemana = inicioSemana.plusDays(6)

        val fichajes = fichajeController.getFichajesPorEmpleadoYRangoFechas(
            empleado.dni,
            inicioSemana.format(dateFormatter),
            finSemana.format(dateFormatter)
        )

        mostrarInforme("Informe Semanal", inicioSemana, finSemana, fichajes)
    }

    private fun generarInformeMensual() {
        val inicioMes = fechaActual.withDayOfMonth(1)
        val finMes = fechaActual.withDayOfMonth(fechaActual.lengthOfMonth())

        val fichajes = fichajeController.getFichajesPorEmpleadoYRangoFechas(
            empleado.dni,
            inicioMes.format(dateFormatter),
            finMes.format(dateFormatter)
        )

        mostrarInforme("Informe Mensual", inicioMes, finMes, fichajes)
    }

    private fun mostrarInforme(titulo: String, fechaInicio: LocalDate, fechaFin: LocalDate, fichajes: List<Fichaje>) {
        val dialog = JDialog(SwingUtilities.getWindowAncestor(this) as JFrame, titulo, true)
        dialog.setSize(600, 500)
        dialog.setLocationRelativeTo(this)

        val panel = JPanel(BorderLayout())

        // Cabecera del informe
        val cabecera = JPanel(BorderLayout())
        val tituloLabel = JLabel("$titulo - ${empleado.nombre} ${empleado.apellido}")
        tituloLabel.font = Font(tituloLabel.font.name, Font.BOLD, 16)
        tituloLabel.horizontalAlignment = SwingConstants.CENTER

        val periodoLabel = JLabel("Período: ${fechaInicio.format(dateFormatter)} - ${fechaFin.format(dateFormatter)}")
        periodoLabel.horizontalAlignment = SwingConstants.CENTER

        cabecera.add(tituloLabel, BorderLayout.NORTH)
        cabecera.add(periodoLabel, BorderLayout.SOUTH)

        // Contenido del informe
        val textArea = JTextArea()
        textArea.isEditable = false
        textArea.font = Font(Font.MONOSPACED, Font.PLAIN, 11)

        val sb = StringBuilder()
        sb.append("RESUMEN DEL PERÍODO\n")
        sb.append("=".repeat(50)).append("\n\n")

        // Agrupar fichajes por día
        val fichajesPorDia = fichajes.groupBy { it.fecha }
        var totalMinutos = 0
        var diasTrabajados = 0

        fichajesPorDia.toSortedMap().forEach { (fecha, fichajesDelDia) ->
            val totalDia = calcularTotalHoras(fichajesDelDia)
            if (totalDia > 0) {
                // Convertir fecha a formato de visualización si es necesario
                val fechaFormateada = parsearFecha(fecha)?.format(dateFormatter) ?: fecha
                sb.append("📅 $fechaFormateada - ${formatearDuracion(totalDia)}\n")
                totalMinutos += totalDia
                diasTrabajados++
            }
        }

        sb.append("\n").append("=".repeat(50)).append("\n")
        sb.append("📊 ESTADÍSTICAS:\n")
        sb.append("   • Días trabajados: $diasTrabajados\n")
        sb.append("   • Total de horas: ${formatearDuracion(totalMinutos)}\n")
        if (diasTrabajados > 0) {
            sb.append("   • Promedio diario: ${formatearDuracion(totalMinutos / diasTrabajados)}\n")
        }

        textArea.text = sb.toString()

        val scrollPane = JScrollPane(textArea)
        panel.add(cabecera, BorderLayout.NORTH)
        panel.add(scrollPane, BorderLayout.CENTER)

        // Botón de cerrar
        val btnCerrar = JButton("Cerrar")
        btnCerrar.addActionListener { dialog.dispose() }
        val panelBoton = JPanel()
        panelBoton.add(btnCerrar)
        panel.add(panelBoton, BorderLayout.SOUTH)

        dialog.contentPane = panel
        dialog.isVisible = true
    }
}