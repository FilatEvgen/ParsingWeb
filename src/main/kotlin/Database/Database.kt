package Database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {

    private const val URL = "jdbc:postgresql://localhost:5432/game_list"
    private const val USERNAME = "postgres"
    private const val PASSWORD = "12345q"

    fun getConnection(): Connection? {
        try {
            Class.forName("org.postgresql.Driver")
            return DriverManager.getConnection(URL, USERNAME, PASSWORD)
        } catch (e: SQLException) {
            println("Error connecting to database: $e")
            return null
        }
    }
    fun removeDuplicates() {
        val connection = getConnection() ?: return
        val query = "DELETE FROM game_cards WHERE id NOT IN (SELECT MIN(id) FROM game_cards GROUP BY title)"
        try {
            val statement = connection.prepareStatement(query)
            statement.executeUpdate()
        } catch (e: SQLException) {
            println("Error removing duplicates: $e")
        } finally {
            connection.close()
        }
    }
}
