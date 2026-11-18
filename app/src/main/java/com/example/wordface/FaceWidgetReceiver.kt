package com.example.wordface

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.concurrent.Executors

class FaceWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != REQUEST_ACTION) return
        val pageId = intent.getStringExtra("pageId") ?: return
        if (pageId != PAGE_ID) return
        val pending = goAsync()
        executor.execute {
            try {
                val cached = WordFeed.cached(context)
                val entry = cached
                val lock = buildViews(context, entry, false)
                val aod = buildViews(context, entry, true)
                context.sendBroadcast(Intent(RESPONSE_ACTION).apply {
                    setPackage("com.android.systemui")
                    putExtra("package", context.packageName)
                    putExtra("pageId", pageId)
                    putExtra("show", true)
                    putExtra("origin", lock)
                    putExtra("aod", aod)
                })
            } finally {
                pending.finish()
            }
        }
    }

    private fun buildViews(context: Context, entry: WordEntry?, aod: Boolean): RemoteViews =
        RemoteViews(context.packageName, R.layout.face_widget).apply {
            setImageViewBitmap(R.id.word_image, WidgetTextRenderer.word(context, entry?.word ?: "Word of the day", 124))
            setImageViewBitmap(R.id.definition_image, WidgetTextRenderer.definition(context, entry?.definition ?: "Connect to the internet to load today's definition.", 124))
            if (aod) {
                setInt(android.R.id.background, "setBackgroundResource", R.drawable.widget_aod)
            }
        }

    companion object {
        const val PAGE_ID = "word_of_the_day"
        private const val REQUEST_ACTION = "com.samsung.android.intent.action.REQUEST_SERVICEBOX_REMOTEVIEWS"
        private const val RESPONSE_ACTION = "com.samsung.android.intent.action.RESPONSE_SERVICEBOX_REMOTEVIEWS"
        private val executor = Executors.newSingleThreadExecutor()
    }
}
