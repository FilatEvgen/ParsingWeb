import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File

const val BASE_URL = "https://www.taptap.io"
fun main() {
    val directory = File("/home/user/IdeaProjects/parsingWeb/src/main/kotlin/html")
    val htmlFile = File("${directory.absoluteFile}/GameList.html")
    val doc = Jsoup.parse(htmlFile, "UTF-8")

    val gameList: Elements = doc.select("div.game-card")
    val finalGameList: MutableList<GameCardModel> = mutableListOf()
    gameList.forEach { gameElement ->
        val title = gameElement.select("span.tap-app-title__title").text()
        val imageSrc = gameElement.select("div.tap-image-wrapper > img ").attr("src")
        val imageWrapper = if (imageSrc.contains("http")) imageSrc else "Ссылка отсутсвует"
        val href = gameElement.select("div.tap-row-card__contents > a").attr("href")
        val link = BASE_URL + href
        val iconSrc = gameElement.select("div.lazy-image > img").attr("src")
        val icon = if (iconSrc.contains("http")) iconSrc else "Ссылка отсутсвует"
        val ratingText = gameElement.select("div.app-rating__number").text()
        val rating = if (ratingText.isNotEmpty()) ratingText.toFloat() else 0.0f
        val gameCard = GameCardModel(imageWrapper, title, link, icon, rating)
        finalGameList.add(gameCard)
    }

    val gameCardRepository = GameCardRepository()
    gameCardRepository.createTableIfNotExists()
    gameCardRepository.saveGameCards(finalGameList)
    downloadAndSaveImages(finalGameList)

    println("Game List:")
    println("---------")
    finalGameList.forEachIndexed { index, game ->
        println("Game #${index + 1}:")
        println("  Title: ${game.title}")
        println("  Link: ${game.link}")
        println("  Image: ${game.image}")
        println("  Icon: ${game.icon}")
        println("  Rating: ${game.rating}")
        println()
    }
}