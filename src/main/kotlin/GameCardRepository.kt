import Database.Database
import java.sql.PreparedStatement
import java.sql.SQLException

class GameCardRepository {
    fun createTableIfNotExists() {
        val connection = Database.getConnection()?: return
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS game_cards (
                id SERIAL,
                image VARCHAR,
                title VARCHAR,
                link VARCHAR,
                icon VARCHAR,
                rating DECIMAL(3, 1)
            )
        """.trimIndent()
        try {
            connection.createStatement().execute(createTableQuery)
        } catch (e: SQLException) {
            println("Error creating table: $e")
        } finally {
            connection.close()
        }
    }
    fun saveGameCards(gameCards: List<GameCardModel>) {
        val connection = Database.getConnection() ?: return
        val preparedStatement: PreparedStatement
        try {
            connection.autoCommit = false
            preparedStatement =
                connection.prepareStatement("INSERT INTO game_cards (image, title, link, icon, rating) VALUES (?,?,?,?,?)")
            gameCards.forEach { gameCard ->
                preparedStatement.setString(1, gameCard.image)
                preparedStatement.setString(2, gameCard.title)
                preparedStatement.setString(3, gameCard.link)
                preparedStatement.setString(4, gameCard.icon)
                preparedStatement.setFloat(5, gameCard.rating)
                preparedStatement.addBatch()
            }
            preparedStatement.executeBatch()
            connection.commit()
        } catch (e: SQLException) {
            println("Error saving game cards to database: $e")
            connection.rollback()
        } finally {
            connection.close()
        }
    }
}