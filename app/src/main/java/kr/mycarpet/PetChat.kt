package kr.mycarpet

import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object PetChat {
    fun ask(endpoint: String, name: String, happiness: Int, question: String, hunger: String, sick: Boolean): String {
        val connection = URL(endpoint).openConnection() as HttpsURLConnection
        try {
            connection.connectTimeout = 7000
            connection.readTimeout = 30000
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            val body = JSONObject().put("name", name).put("happiness", happiness).put("question", question).put("hunger",hunger).put("sick",sick)
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            return connection.inputStream.bufferedReader().use { JSONObject(it.readText()).getString("reply") }
        } finally { connection.disconnect() }
    }
}

