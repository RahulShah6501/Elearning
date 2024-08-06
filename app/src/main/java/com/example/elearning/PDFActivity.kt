package com.example.elearning

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PDFActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        // Retrieve the PDF URL from the intent
        val pdfUrl = intent.getStringExtra("PDF_URL")

        if (pdfUrl != null) {
            Log.d("PDFActivity", "Received PDF URL: $pdfUrl")

            // URL encode the PDF URL
            val encodedPdfUrl = Uri.encode(pdfUrl, ":/?=&")
            Log.d("PDFActivity", "Encoded PDF URL: $encodedPdfUrl")

            // Create the Google Docs Viewer URL
            val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$encodedPdfUrl"
            Log.d("PDFActivity", "Google Docs URL: $googleDocsUrl")

            // Create an intent to open the PDF in Google Docs Viewer
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + applicationContext.packageName))
            }

            // Log the intent data and type
            Log.d("PDFActivity", "Intent data: ${intent.data}")
            Log.d("PDFActivity", "Intent type: ${intent.type}")

            // Start the activity to view the PDF
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("PDFActivity", "Failed to start activity", e)
            }

            // Setup download button
            val downloadButton = findViewById<Button>(R.id.downloadButton)
            downloadButton.setOnClickListener {
                downloadPDF(pdfUrl)
            }
        } else {
            Log.e("PDFActivity", "PDF URL is missing or null")
        }
    }

    private fun downloadPDF(pdfUrl: String) {
        val request = DownloadManager.Request(Uri.parse(pdfUrl)).apply {
            setTitle("Downloading PDF")
            setDescription("Downloading...")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded.pdf")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
    }
}
