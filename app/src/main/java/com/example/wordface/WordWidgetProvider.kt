package com.example.wordface

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews

open class WordWidgetProvider : AppWidgetProvider() {
    protected open val widthDp: Int = 124
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        val remoteViews = views(context, widthDp)
        appWidgetIds.forEach { manager.updateAppWidget(it, remoteViews) }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val components = listOf(
                ComponentName(context, WordWidget2x1Provider::class.java) to 124,
                ComponentName(context, WordWidget3x1Provider::class.java) to 186
            )
            components.forEach { (component, widthDp) ->
                val remoteViews = views(context, widthDp)
                manager.getAppWidgetIds(component).forEach { manager.updateAppWidget(it, remoteViews) }
            }
        }

        private fun views(context: Context, widthDp: Int) = RemoteViews(context.packageName, R.layout.face_widget).apply {
            val entry = WordFeed.cached(context)
            setImageViewBitmap(R.id.word_image, WidgetTextRenderer.word(context, entry?.word ?: "Word of the day", widthDp))
            setImageViewBitmap(R.id.definition_image, WidgetTextRenderer.definition(context, entry?.definition ?: "Connect to the internet to load today's definition.", widthDp))
        }
    }
}

class WordWidget2x1Provider : WordWidgetProvider() { override val widthDp = 124 }
class WordWidget3x1Provider : WordWidgetProvider() { override val widthDp = 186 }
