package com.tbox.jotter.firestore

import com.google.firebase.firestore.FirebaseFirestore


fun addNoteToFirestore(
    uid: String,
    title: String,
    content: String,
    type: String,
    tag: String?,
    timestamp: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
){

    val firestore = FirebaseFirestore.getInstance()
    val noteData = hashMapOf(
        "title" to title,
        "content" to content,
        "timestamp" to System.currentTimeMillis(),
        "type" to type,
        "timestamp" to timestamp

    )
    tag?.let {
        noteData["tag"] = it
    }


    firestore.collection("simple_notes")
        .document(uid)
        .collection("user_simple_notes")
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

    firestore.collection("simple_notes")
        .document(uid)
        .collection("user_simple_notes")
        .whereEqualTo("type", "simple")
        .get()
        .addOnSuccessListener { documents ->
            val notes = documents.map { document ->
                // Burada her dokümanın documentId'sini alıyoruz ve map'e ekliyoruz
                document.data + ("noteId" to document.id)
            }
            onNotesFetched(notes)
        }
        .addOnFailureListener {
            onFailure(it)
        }
}

fun fetchNoteDetailFromFirestore(
    noteId: String,
    uid: String,
    onNoteFetched: (Map<String, String>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("simple_notes")
        .document(uid)  // UID ile ilgili notları almak için
        .collection("user_simple_notes")
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

    firestore.collection("simple_notes")
        .document(uid)
        .collection("user_simple_notes")
        .document(noteId)
        .update(noteData as Map<String, Any>)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
}

