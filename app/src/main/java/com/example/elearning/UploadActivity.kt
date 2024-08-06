package com.example.elearning

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var fileUri: Uri
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val PICK_FILE_REQUEST = 1
    private var uploadCallback: UploadCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        val uploadButton: Button = findViewById(R.id.uploadButton)
        val selectFileButton: Button = findViewById(R.id.selectButton)

        selectFileButton.setOnClickListener {
            selectFile()
        }

        uploadButton.setOnClickListener {
            if (::fileUri.isInitialized) {
                uploadFile()
            } else {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf" // Filter for PDF files
        }
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                fileUri = it
                Toast.makeText(this, "File selected: ${getFileName(it)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return uri.path?.substringAfterLast('/') ?: "Unknown File"
    }

    private fun uploadFile() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "${UUID.randomUUID()}.pdf"
        val storageRef = storage.reference.child("pdfs/$fileName")

        storageRef.putFile(fileUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Use the download URL
                val tutorial = Tutorial(
                    title = title,
                    description = description,
                    id = UUID.randomUUID().toString(),
                    fileUrl = downloadUrl.toString(), // Ensure this is properly set
                    pdfUrl = downloadUrl.toString()   // Ensure this is properly set
                )

                saveTutorialToFirestore(tutorial)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTutorialToFirestore(tutorial: Tutorial) {
        firestore.collection("tutorials")
            .document(tutorial.id)
            .set(tutorial)
            .addOnSuccessListener {
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
                uploadCallback?.onUploadSuccess() // Notify success
                finish() // Close the activity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save tutorial", Toast.LENGTH_SHORT).show()
            }
    }

    fun setUploadCallback(callback: UploadCallback) {
        this.uploadCallback = callback
    }
}
