package com.uan.madlibsgame.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.uan.madlibsgame.R

class StoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        val btnStoryNew = findViewById<Button>(R.id.btn_story_new)
        val title = findViewById<TextView>(R.id.story_title)
        val storyTextView = findViewById<TextInputEditText>(R.id.story_text_content)

        val bundle = intent.extras

        if (bundle != null) {
            title.text = bundle.getString("storyTitle")
            var story = bundle.getString("storyContent")

            storyTextView.setText(story)
        }

        btnStoryNew.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}