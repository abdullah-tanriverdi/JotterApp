package com.tovaxtechnology.jotter.Chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tovaxtechnology.jotter.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class Todo(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val tag: String = "",
    val importance: String = "Normal",
    val completed: Boolean = false,
    val timestamp: Timestamp? = null
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val client = OkHttpClient()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory






    init {
        fetchTodos()
        _chatHistory.value = listOf(
            ChatMessage(
                text = "Hoş geldin! Jotter AI ile görevlerini konuşabilir, planlarını netleştirebilirsin. Başlayalım mı?",
                isUser = false
            )
        )
    }

    fun sendMessage(userMessage: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            if (_todos.value.isEmpty()) fetchTodos()



            val newList = _chatHistory.value + ChatMessage(userMessage, isUser = true)
            _chatHistory.value = newList.takeLast(20)

            val todosContext = buildTodosContext(
                important = "Önemli",
                postpone = "Ertelenmiş",
                normal = "Normal"
            )

            chatWithAI(userMessage, todosContext) { response ->
                val updated = _chatHistory.value + ChatMessage(response, isUser = false)
                _chatHistory.value = updated.takeLast(20)

                onComplete()
            }
        }
    }



    fun fetchTodos() {
        uid?.let { userId ->
            viewModelScope.launch {
                try {
                    val result = firestore.collection("todos")
                        .document(userId)
                        .collection("items")
                        .get()
                        .await()

                    val todos = result.documents.mapNotNull { doc ->
                        doc.toObject(Todo::class.java)?.copy(id = doc.id)
                    }

                    _todos.value = todos
                } catch (e: Exception) {
                }
            }
        }
    }

    fun buildTodosContext(
        important: String,
        postpone: String,
        normal: String
    ): String {
        return _todos.value.joinToString("\n\n") { todo ->
            val dateStr = todo.timestamp?.toDate()?.let {
                SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
            } ?: "Tarih yok"

            val status = if (todo.completed) "Tamamlandı ✅" else "Bekliyor ⏳"

            val importanceRaw = todo.importance.trim().lowercase(Locale.ROOT)
            val importanceText = when (importanceRaw) {
                important.lowercase() -> "Yüksek Öncelikli ❗"
                postpone.lowercase() -> "Düşük Öncelikli 🕓"
                normal.lowercase() -> "Normal Öncelikli 📝"
                else -> "Belirsiz Öncelik ❔ (${todo.importance})"
            }

            """
        🔹 Görev Kimliği: ${todo.id}
        • Başlık: ${todo.title}
        • Açıklama: ${todo.content}
        • Etiket: ${todo.tag}
        • Önem Derecesi: $importanceText
        • Tamamlanma Durumu: $status
        • Oluşturulma Tarihi: $dateStr
        """.trimIndent()
        }
    }


    private fun chatWithAI(userMessage: String,  todosContext: String, onComplete: (String) -> Unit) {
        val apiKey = "sk-proj-"


        val todosInfo = todosContext



        val messagesArray = JSONArray().apply {
            put(
                JSONObject()
                    .put("role", "system")
                    .put(
                        "content",
                        """
                       

                        Sen Jotter adlı bir görev yöneticisi uygulamasında görevleri analiz eden ve kullanıcıya yardımcı olan bir yapay zekâ asistanısın.

                        Kullanıcının görev listesi aşağıda verilmiştir. Lütfen sadece bu görev bilgilerini referans alarak konuş:

                        $todosInfo
🔹 Görevlerin:
- Kullanıcının görev listesini dikkatlice analiz etmek ve görevlerin öncelik sırasını anlamak.
- Özellikle tamamlanmamış ve ertelenmiş görevlere öncelik vererek, odaklanma ve zaman yönetimi önerileri sunmak.
- Görevlerin önem derecesine göre kullanıcıyı bilgilendirmek ve motivasyonunu artıracak destek sağlamak.
- Tamamlanmamış görevler için somut, uygulanabilir ve kullanıcının iş akışına uygun önerilerde bulunmak.
- Eksik veya belirsiz bilgi varsa bunu açıkça belirtmek ve kullanıcıdan daha fazla bilgi istemek.
- Asla veri uydurmamak; sadece mevcut ve verilen görevlere dayalı yanıtlar üretmek.
- Görevler arasındaki ilişkileri ve öncelik çatışmalarını analiz ederek, kullanıcının iş yükünü hafifletmeye yardımcı olmak.
- Kullanıcıya görevlerle ilgili pratik ipuçları ve hatırlatmalar sunmak, gerekirse sonraki adımlar için yol göstermek.
- Konuşma tarzında samimi, destekleyici ve motive edici olmak; kullanıcıya güven ve pozitif enerji vermek.
- Gerektiğinde, görevlerle ilgili sorunları veya engelleri tespit edip, çözüm önerileri getirmek.


                        """.trimIndent()
                    )
            )

            _chatHistory.value.forEach { msg ->
                put(
                    JSONObject()
                        .put("role", if (msg.isUser) "user" else "assistant")
                        .put("content", msg.text)
                )
            }

            put(JSONObject().put("role", "user").put("content", userMessage))
        }

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", messagesArray)
            put("max_tokens", 500)
        }

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onComplete("Bir hata oluştu: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val reply = response.body?.string()?.let { body ->
                    JSONObject(body).optJSONArray("choices")
                        ?.optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content")
                } ?: "Asistandan yanıt alınamadı."
                onComplete(reply)
            }
        })
    }
}
