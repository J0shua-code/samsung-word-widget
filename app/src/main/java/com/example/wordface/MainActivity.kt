package com.example.wordface

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WordRefreshScheduler.ensureScheduled(this)
        setContentView(R.layout.activity_main)
        val word = findViewById<TextView>(R.id.preview_word)
        val definition = findViewById<TextView>(R.id.preview_definition)
        fun show(entry: WordEntry?) {
            word.text = entry?.word ?: "Word of the Day"
            definition.text = entry?.definition ?: "The Merriam-Webster Word of the Day, on your lock screen."
        }
        show(WordFeed.cached(this))
        findViewById<Button>(R.id.refresh).setOnClickListener {
            word.text = "Fetching today’s word…"
            executor.execute {
                val entry = runCatching { WordFeed.refresh(this) }.getOrNull()
                runOnUiThread {
                    show(entry ?: WordFeed.cached(this))
                    WordWidgetProvider.updateAll(this)
                }
            }
        }
        if (WordFeed.cached(this) == null) findViewById<Button>(R.id.refresh).performClick()
    }
}
