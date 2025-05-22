package db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DBConnection {
    // Cambiado para apuntar a tu base de datos local
    private const val DB_URL = "jdbc:postgresql://db.ibrusfgcyblkfzcttujk.supabase.co:5432/postgres?user=postgres&password=Alpiste05%2F"
    private var connection: Connection? = null

        fun connect(): Connection {
            if (connection == null || connection!!.isClosed) {
                try {
                    // Cargar el driver de PostgreSQL
                    Class.forName("org.postgresql.Driver")
                    connection = DriverManager.getConnection(DB_URL)
                    println("Conexión a la base de datos establecida correctamente")

                    connection?.autoCommit = true
                } catch (e: ClassNotFoundException) {
                    System.err.println("Error al cargar el driver de PostgreSQL: ${e.message}")
                    throw e
                } catch (e: SQLException) {
                    System.err.println("Error al conectar con la base de datos: ${e.message}")
                    throw e
                }
            }
            return connection!!
        }

        fun closeConnection() {
            try {
                connection?.let {
                    if (!it.isClosed) {
                        it.close()
                        println("Conexión a la base de datos cerrada correctamente")
                    }
                }
                connection = null
            } catch (e: SQLException) {
                System.err.println("Error al cerrar la conexión: ${e.message}")
            }
        }

        fun isConnected(): Boolean {
            return try {
                connection != null && !connection!!.isClosed
            } catch (e: SQLException) {
                false
            }
        }
    }

