package com.tbox.jotter.ScreenAssistant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tbox.jotter.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Properties

data class Note(
    val title: String,
    val content: String,
    val tag: String?,
    val timestamp: Long
)



class ChatViewModel : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<String>>(emptyList())
    val chatHistory: StateFlow<List<String>> = _chatHistory

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    fun fetchNotes() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Notları Firebase'den çekmeye çalışıyorum...")

                val result = firestore.collection("users")
                    .document(uid)
                    .collection("notes")
                    .document("simple_notes")
                    .collection("user_notes")
                    .get()
                    .await()

                val fetchedNotes = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val content = doc.getString("content") ?: return@mapNotNull null
                    val tag = doc.getString("tag")
                    val timestamp = try {
                        doc.getLong("timestamp") ?: doc.getDouble("timestamp")?.toLong() ?: doc.getString("timestamp")?.toLong() ?: 0L
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "timestamp dönüştürme hatası: ${e.message}")
                        0L
                    }
                    Note(title, content, tag, timestamp)
                }

                _notes.value = fetchedNotes

                Log.d("ChatViewModel", "Notlar başarıyla çekildi: ${_notes.value}")

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Notları alırken hata: ${e.message}")
            }
        }
    }


    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            if (_notes.value.isEmpty()) {  // Notlar henüz yüklenmediyse, önce yükle
                fetchNotes()
                kotlinx.coroutines.delay(1000) // Notların yüklenmesini beklemek için 1 saniye gecikme ekle
            }

            chatWithAI(userMessage) { response ->
                _chatHistory.value = _chatHistory.value + listOf("Sen: $userMessage", "Asistan: $response")
            }
        }
    }




    private fun chatWithAI(userMessage: String, onComplete: (String) -> Unit) {




        val apiKey = BuildConfig.OPEN_API_KEY
        val client = OkHttpClient()

        val formattedNotes = _notes.value.joinToString("\n") { "${it.title}: ${it.content}" }

        val prompt = """
        Aşağıda kullanıcının notları ve mesaj geçmişi bulunmaktadır. Kullanıcı bir soru sorduğunda, notları dikkatlice tarayarak doğru bir yanıt ver. 
        
        **Önemli:** Eğer ilgili bir bilgi varsa **kesin ve net** bir cevap ver. Eğer doğrudan bir bilgi yoksa, "Bu konuda bir notun bulunmuyor." diye belirt.

        **Kullanıcı Notları:**  
        ${if (formattedNotes.isNotEmpty()) formattedNotes else "(Kullanıcının kaydedilmiş notu yok.)"}

      
        **Kullanıcı Mesajı:** "$userMessage"

        Yukarıdaki bilgilere göre **en doğru ve mantıklı cevabı üret.** 
    """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", prompt)
                })
            })
            put("max_tokens", 150)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onComplete("Bir hata oluştu, tekrar deneyin.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = response.body?.string()
                    val reply = JSONObject(responseBody).optJSONArray("choices")?.optJSONObject(0)
                        ?.optJSONObject("message")?.optString("content") ?: "Yanıt alınamadı."

                    onComplete(reply)
                }
            }
        })
    }
}
