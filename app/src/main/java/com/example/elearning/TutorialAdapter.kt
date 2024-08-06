package com.example.elearning

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class TutorialAdapter(
    private val context: MainActivity,
    var tutorials: List<Tutorial>,
    private val firestore: FirebaseFirestore // Add Firestore parameter
) : RecyclerView.Adapter<TutorialAdapter.ViewHolder>() {

    private val storage by lazy { FirebaseStorage.getInstance() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tutorial_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tutorial = tutorials[position]
        holder.titleTextView.text = tutorial.title
        holder.descriptionTextView.text = tutorial.description

        holder.deleteButton.setOnClickListener {
            deleteFile(tutorial)
        }

        holder.viewPdfButton.setOnClickListener {
            val pdfUrl = tutorial.pdfUrl
            if (!pdfUrl.isNullOrEmpty()) {
                val intent = Intent(context, PDFActivity::class.java).apply {
                    putExtra("PDF_URL", pdfUrl)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return tutorials.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTutorials(newTutorials: List<Tutorial>) {
        tutorials = newTutorials
        notifyDataSetChanged()
    }

    private fun deleteFile(tutorial: Tutorial) {
        val pdfUrl = tutorial.pdfUrl
        if (pdfUrl.isNullOrEmpty()) {
            Log.w("TutorialAdapter", "Cannot delete file: pdfUrl is null or empty")
            return
        }

        Log.d("TutorialAdapter", "Attempting to delete file at: $pdfUrl")

        val storageRef: StorageReference = storage.getReferenceFromUrl(pdfUrl)
        storageRef.delete().addOnSuccessListener {
            Log.d("TutorialAdapter", "File deleted successfully")

            // Remove the tutorial from Firestore
            firestore.collection("tutorials").document(tutorial.id).delete().addOnSuccessListener {
                Log.d("TutorialAdapter", "Document deleted from Firestore successfully")

                // Remove the tutorial from the list and update the UI
                val updatedList = tutorials.filter { it.id != tutorial.id }
                updateTutorials(updatedList)
            }.addOnFailureListener { exception ->
                Log.e("TutorialAdapter", "Failed to delete document from Firestore", exception)
            }
        }.addOnFailureListener { exception ->
            Log.e("TutorialAdapter", "Failed to delete file", exception)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val viewPdfButton: Button = itemView.findViewById(R.id.viewPdfButton)
    }
}
