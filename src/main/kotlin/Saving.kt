import Database.Database
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

fun downloadAndSaveImages(gameCards: List<GameCardModel>) {
    val directory = File("Games")
    directory.mkdirs()

    gameCards.forEach { game ->
        val gameDirectory = File(directory, game.title)
        gameDirectory.mkdirs()

        val mainImageFile = File(gameDirectory, "main_image.jpg")
        val iconFile = File(gameDirectory, "icon.jpg")

        try {
            downloadImage(game.image, gameDirectory, "main_image.jpg")
            downloadImage(game.icon, gameDirectory, "icon.jpg")
        } catch (e: IOException) {
            println("Ошибка скачивания картинки: ${e.message}")
        }

        // Обновляем записи в базе данных
        Database.removeDuplicates() // Удаляем дубликаты
        val connection = Database.getConnection()?: return
        val query = "UPDATE game_cards SET image =?, icon =? WHERE title =?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, mainImageFile.absolutePath)
        statement.setString(2, iconFile.absolutePath)
        statement.setString(3, game.title)
        statement.executeUpdate()
        connection.close()
    }
}

fun downloadImage(url: String, directory: File, filename: String): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    try {
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Failed to download image: $url")
        }
        val body = response.body
        val bytes = body!!.bytes()

        val file = File(directory, filename)
        file.parentFile.mkdirs()
        file.writeBytes(bytes)

        return true
    } catch (e: IOException) {
        println("Ошибка скачивания картинки: ${e.message}")
        return false
    }
}