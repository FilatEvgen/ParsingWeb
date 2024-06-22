package Database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {

    private val url = System.getenv("URL")
    private val username = System.getenv("USERNAME")
    private  val password = System.getenv("PASSWORD")

    fun getConnection(): Connection? {
        try {
            Class.forName("org.postgresql.Driver")
            return DriverManager.getConnection(url, username, password)
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
