package com.example.elearning

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TutorialDetailActivity : AppCompatActivity() {
    private lateinit var tutorialTitleTextView: TextView
    private lateinit var tutorialDescriptionTextView: TextView
    private lateinit var viewContentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial_detail)

        tutorialTitleTextView = findViewById(R.id.tutorialTitleTextView)
        tutorialDescriptionTextView = findViewById(R.id.tutorialDescriptionTextView)
        viewContentButton = findViewById(R.id.viewContentButton)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val pdfUrl = intent.getStringExtra("pdfUrl")

        tutorialTitleTextView.text = title
        tutorialDescriptionTextView.text = description

        viewContentButton.setOnClickListener {
            val intent = Intent(this, PDFActivity::class.java)
            intent.putExtra("pdfUrl", pdfUrl)
            startActivity(intent)
        }
    }
}
