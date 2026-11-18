package com.example.wordface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.max
import kotlin.math.min

/** Draws the bundled typefaces in-app, then sends pixels through RemoteViews. */
object WidgetTextRenderer {
    private const val CARD_HEIGHT_DP = 70f
    private const val HORIZONTAL_PADDING_DP = 24f
    private const val VERTICAL_PADDING_DP = 16f
    private val white = Color.WHITE
    private val mutedWhite = Color.rgb(238, 238, 238)

    fun word(context: Context, text: String, widthDp: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        val width = max(1, ((widthDp - HORIZONTAL_PADDING_DP) * density).toInt())
        val height = max(1, ((CARD_HEIGHT_DP - VERTICAL_PADDING_DP) * density * 3f / 10f).toInt())
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = white
            typeface = context.resources.getFont(R.font.instrument_serif_regular)
            isFakeBoldText = true
            textSize = min(18f * density, height * .88f)
        }
        val label = text.ifBlank { "Word of the day" }.replaceFirstChar { it.uppercase() }
        val display = ellipsize(label, paint, width.toFloat())
        val metrics = paint.fontMetrics
        val baseline = (height - metrics.ascent - metrics.descent) / 2f
        canvas.drawText(display, 0f, baseline, paint)
        return bitmap
    }

    fun definition(context: Context, text: String, widthDp: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        val width = max(1, ((widthDp - HORIZONTAL_PADDING_DP) * density).toInt())
        val height = max(1, ((CARD_HEIGHT_DP - VERTICAL_PADDING_DP) * density * 7f / 10f).toInt())
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val typeface = context.resources.getFont(R.font.instrument_serif_regular)
        val words = text.ifBlank { "Today's definition will appear here." }.split(Regex("\\s+"))
        var chosenSize = 14f * density
        var lines: List<String>
        var paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = mutedWhite
            this.typeface = typeface
        }
        while (true) {
            paint.textSize = chosenSize
            lines = wrap(words, paint, width.toFloat())
            val lineHeight = paint.fontSpacing * 1.02f
            if (lines.size * lineHeight <= height || chosenSize <= 8f * density) break
            chosenSize -= density
        }
        val lineHeight = paint.fontSpacing * 1.02f
        val visibleLines = max(1, (height / lineHeight).toInt())
        val shown = lines.take(visibleLines).toMutableList()
        if (shown.size < lines.size && shown.isNotEmpty()) {
            shown[shown.lastIndex] = ellipsize(shown.last() + "…", paint, width.toFloat())
        }
        var baseline = -paint.fontMetrics.ascent
        shown.forEach { line ->
            canvas.drawText(line, 0f, baseline, paint)
            baseline += lineHeight
        }
        return bitmap
    }

    private fun wrap(words: List<String>, paint: Paint, width: Float): List<String> {
        val lines = mutableListOf<String>()
        var line = ""
        for (word in words) {
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(candidate) <= width || line.isEmpty()) line = candidate
            else { lines += line; line = word }
        }
        if (line.isNotEmpty()) lines += line
        return lines
    }

    private fun ellipsize(text: String, paint: Paint, width: Float): String {
        if (paint.measureText(text) <= width) return text
        val suffix = "…"
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + suffix) > width) end--
        return text.substring(0, end) + suffix
    }
}
