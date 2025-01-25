package com.tbox.jotter

import com.google.firebase.firestore.FirebaseFirestore
import com.tbox.jotter.auth.AuthViewModel


fun addNoteToFirestore(
  uid : String,
  title: String,
  content: String,
  type: String,
  onSuccess: () -> Unit,
  onFailure: (Exception) -> Unit
){

    val firestore = FirebaseFirestore.getInstance()
    val noteData = hashMapOf(
        "title" to title,
        "content" to content,
        "timestamp" to System.currentTimeMillis(),
        "type" to type
    )


    firestore.collection("notes")
        .document(uid)
        .collection("user_notes")
        .add(noteData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener{ exception ->
            onFailure(exception)

        }

}


fun fetchNotesFromFirestore(
    uid: String,
    onNotesFetched: (List<Map<String, Any>>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("notes")
        .document(uid)
        .collection("user_notes")
        .whereEqualTo("type", "simple")
        .get()
        .addOnSuccessListener { documents ->
            val notes = documents.map { it.data }
            onNotesFetched(notes)
        }
        .addOnFailureListener {
            onFailure(it)
        }
}
