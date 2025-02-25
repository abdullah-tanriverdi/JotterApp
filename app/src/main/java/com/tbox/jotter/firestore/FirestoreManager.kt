package com.tbox.jotter.firestore

import com.google.firebase.firestore.FirebaseFirestore






fun fetchNoteDetailFromFirestore(
    noteId: String,
    uid: String,
    onNoteFetched: (Map<String, String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .document(uid)  // UID ile ilgili notları almak için
        .collection("simple_notes")
        .document(noteId)  // Notun id'si ile notu alıyoruz
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val noteData = document.data?.mapValues { it.value.toString() } ?: emptyMap()
                onNoteFetched(noteData)
            } else {
                onFailure(Exception("Note not found"))
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}



fun updateNoteInFirestore(
    uid: String,
    noteId: String,
    title: String,
    content: String,
    timestamp: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val noteData = hashMapOf(
        "title" to title,
        "content" to content,
        "timestamp" to timestamp
    )

    firestore.collection("users")
        .document(uid)
        .collection("simple_notes")
        .document(noteId)
        .update(noteData as Map<String, Any>)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
}







