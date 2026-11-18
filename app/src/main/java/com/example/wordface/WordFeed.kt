package com.example.wordface

import android.content.Context
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.net.HttpURLConnection
import java.net.URL

data class WordEntry(val word: String, val definition: String)

object WordFeed {
    private const val PREFS = "word_cache"
    private const val KEY_WORD = "word"
    private const val KEY_DEFINITION = "definition"
    private val refreshLock = Any()

    fun cached(context: Context): WordEntry? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val word = prefs.getString(KEY_WORD, null) ?: return null
        return WordEntry(word, prefs.getString(KEY_DEFINITION, "") ?: "")
    }

    fun refresh(context: Context): WordEntry = synchronized(refreshLock) {
        val connection = URL(context.getString(R.string.feed_url)).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36")
            connection.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml, */*")
            connection.inputStream.use { input ->
                val parser = Xml.newPullParser().apply { setInput(input, null) }
                var inItem = false
                var tag = ""
                var title = ""
                var description = ""
                var shortDefinition = ""
                while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                    when (parser.eventType) {
                        XmlPullParser.START_TAG -> {
                            tag = parser.name.substringAfter(':').lowercase()
                            if (tag == "item") inItem = true
                        }
                        XmlPullParser.TEXT, XmlPullParser.CDSECT -> if (inItem) {
                            val value = parser.text.orEmpty()
                            when (tag) {
                                "title" -> if (title.isEmpty()) title = value
                                "description" -> if (description.isEmpty()) description = value
                                "shortdef" -> if (shortDefinition.isEmpty()) shortDefinition = value
                            }
                        }
                        XmlPullParser.END_TAG -> if (parser.name.equals("item", true)) break
                    }
                    parser.next()
                }
                val word = title.trim()
                val definition = cleanHtml(shortDefinition.ifEmpty { description }).ifEmpty { "Open Word Face to refresh today's definition." }
                if (word.isBlank()) throw IllegalStateException("The feed did not contain a word.")
                val entry = WordEntry(word, definition)
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                    .putString(KEY_WORD, entry.word).putString(KEY_DEFINITION, entry.definition)
                    .apply()
                return entry
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun cleanHtml(source: String): String = source
        .replace(Regex("(?i)<br\\s*/?>|</p>"), " ")
        .replace(Regex("<[^>]*>"), " ")
        .replace("&nbsp;", " ").replace("&#160;", " ")
        .replace("&amp;", "&").replace("&quot;", "\"").replace("&#39;", "'").replace("&lt;", "<").replace("&gt;", ">")
        .replace(Regex("\\s+"), " ").trim()
}
