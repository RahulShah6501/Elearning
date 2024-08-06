package com.example.elearning

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), UploadCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadButton: Button
    private lateinit var refreshButton: Button
    private lateinit var tutorialAdapter: TutorialAdapter

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)
        refreshButton = findViewById(R.id.refreshButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        uploadButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivityForResult(intent, UPLOAD_REQUEST_CODE)
        }

        refreshButton.setOnClickListener {
            loadTutorials()
        }

        loadTutorials()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        // Sign out the user from Firebase Authentication
        FirebaseAuth.getInstance().signOut()

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Optionally, finish the current activity
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPLOAD_REQUEST_CODE) {
            loadTutorials()
        }
    }

    override fun onUploadSuccess() {
        loadTutorials() // Refresh data on successful upload
    }

    private fun loadTutorials() {
        firestore.collection("tutorials")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    val tutorials = snapshot.toObjects(Tutorial::class.java)
                    if (::tutorialAdapter.isInitialized) {
                        tutorialAdapter.updateTutorials(tutorials)
                    } else {
                        tutorialAdapter = TutorialAdapter(this, tutorials, firestore) // Pass Firestore instance here
                        recyclerView.adapter = tutorialAdapter
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle error
                Log.e("MainActivity", "Error loading tutorials", e)
            }
    }

    companion object {
        private const val UPLOAD_REQUEST_CODE = 1
    }
}
