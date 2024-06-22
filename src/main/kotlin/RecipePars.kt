import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File
import java.net.URL

const val BASE_URL = "https://www.taptap.io"
const val IMAGES_DIR = "/home/user/IdeaProjects/parsingWeb/src/main/kotlin/Games"

fun main() {
    val gameList = parseGameList()
    val gameCardRepository = GameCardRepository()
    gameCardRepository.createTableIfNotExists()
    gameCardRepository.saveGameCards(gameList)

    println("Game List:")
    println("---------")
    gameList.forEachIndexed { index, game ->
        println("Game #${index + 1}:")
        println("  Title: ${game.title}")
        println("  Link: ${game.link}")
        println("  Image: ${game.image}")
        println("  Icon: ${game.icon}")
        println("  Rating: ${game.rating}")
        println()
    }
}

fun parseGameList(): MutableList<GameCardModel> {
    val directory = File("/home/user/IdeaProjects/parsingWeb/src/main/kotlin/html")
    val htmlFile = File("${directory.absoluteFile}/GameList.html")
    val doc = Jsoup.parse(htmlFile, "UTF-8")

    val gameList: Elements = doc.select("div.game-card")
    val finalGameList: MutableList<GameCardModel> = mutableListOf()
    gameList.forEach { gameElement ->
        val title = gameElement.select("span.tap-app-title__title").text()
        val href = gameElement.select("div.tap-row-card__contents > a").attr("href")
        val link = BASE_URL + href
        val ratingText = gameElement.select("div.app-rating__number").text()
        val rating = if (ratingText.isNotEmpty()) ratingText.toFloat() else 0.0f

        val imageSrc = gameElement.select("div.tap-image-wrapper > img ").attr("src")
        val imageWrapper = if (imageSrc.contains("http")) imageSrc else "Ссылка отсутсвует"
        val localImagePath = downloadAndSaveImage(imageWrapper, IMAGES_DIR)

        val iconSrc = gameElement.select("div.lazy-image > img").attr("src")
        val icon = if (iconSrc.contains("http")) iconSrc else "Ссылка отсутсвует"
        val localIconPath = downloadAndSaveImage(icon, IMAGES_DIR)

        val gameCard = GameCardModel(localImagePath, title, link, localIconPath, rating)
        finalGameList.add(gameCard)
    }
    return finalGameList
}

fun downloadAndSaveImage(url: String, dir: String): String {
    val filename = url.substring(url.lastIndexOf('/') + 1)
    val file = File("$dir/$filename")
    if (!file.exists()) {
        val connection = URL(url).openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()
        val outputStream = file.outputStream()
        inputStream.copyTo(outputStream)
        outputStream.close()
    }
    return file.absolutePath
}