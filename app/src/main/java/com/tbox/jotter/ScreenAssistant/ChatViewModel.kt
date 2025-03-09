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


//Kullanıcının notlarını temsil eden veri sınıfı
data class Note(
    val title: String,
    val content: String,
    val tag: String,
    val timestamp: String ,
    val updatedTimestamp : String
)

//Kullanınıcın profil bilgilerini içeren veri sınıfı
data class UserProfile(
    val name : String,
    val email : String,
    val birthDate : String,
    val phoneNumber : String,
    val bio : String,
    val profileImageUrl : String?
)


//Chat ekranı için viewmodel
class ChatViewModel : ViewModel() {

    //Chat geçmisi state
    private val _chatHistory = MutableStateFlow<List<String>>(emptyList())
    val chatHistory: StateFlow<List<String>> = _chatHistory

    //kullanıcı notlarını tutan state
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    // kullanıcnn profil bilgisini tutan state
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    //firebase kimlik değişkeni
    private val firestore = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    val client = OkHttpClient()


    //viewmodel basladııgnıda bılgıelrı cek
    init {
      fetchUserData()
        sendInitialMessage()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            fetchUserProfile()
            fetchNotes()
        }
    }

    private suspend fun fetchUserProfile() {
        uid?.let { userId ->
            try {
                val document = firestore.collection("users")
                    .document(userId)
                    .collection("profile")
                    .document("profile_data")
                    .get()
                    .await()

                if (document.exists()) {
                    val userProfile = UserProfile(
                        name = document.getString("name") ?: "Bilinmiyor",
                        email = document.getString("email") ?: "Bilinmiyor",
                        birthDate = document.getString("birthDate") ?: "Bilinmiyor",
                        phoneNumber = document.getString("phoneNumber") ?: "Bilinmiyor",
                        bio = document.getString("bio") ?: "Bilinmiyor",
                        profileImageUrl = document.getString("profileImageUrl")
                    )

                    if (_userProfile.value != userProfile) {
                        _userProfile.value = userProfile
                    } else {

                    }
                } else {

                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Profil verisi alınırken hata: ${e.message}")
            }
        }
    }

    private fun sendInitialMessage(){
        if (_chatHistory.value.isEmpty()) {
            _chatHistory.value = listOf(
                "Asistan: Hi! I'm Jotter, specially developed by T-Box!" +
                        " I can organize your notes, remind you of them, and even have a little chat." +
                        " Feel free to ask me anything!"

            )
        }
    }


    suspend fun fetchNotes() {
        uid?.let { userId ->
            try {
                val result = firestore.collection("users")
                    .document(userId)
                    .collection("notes")
                    .document("quick_notes")
                    .collection("user_notes")
                    .get()
                    .await()

                val fetchedNotes = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val content = doc.getString("content") ?: return@mapNotNull null
                    val tag = doc.getString("tag") ?: "Genel"
                    val timestamp = doc.getString("timestamp") ?: "Bilinmiyor"
                    val updatedTimestamp = doc.getString("updatedTimestamp") ?:timestamp

                    Note(title, content, tag, timestamp,updatedTimestamp)
                }

                if (_notes.value != fetchedNotes) {
                    _notes.value = fetchedNotes
                } else {

                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Notları alırken hata: ${e.message}")
            }
        }
    }

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            if (_notes.value.isEmpty()) fetchNotes()
            if (_userProfile.value == null) fetchUserProfile()

            chatWithAI(userMessage) { response ->
                _chatHistory.value = _chatHistory.value + listOf("Sen: $userMessage", "Asistan: $response")
            }
        }
    }


    private fun chatWithAI(userMessage: String, onComplete: (String) -> Unit) {
        val apiKey = ""
        val userProfile = _userProfile.value



        val formattedNotes = _notes.value.joinToString("\n\n") { note ->
            """
    📌 **Note Information**
    - **Title:** ${note.title}
    - **Content:** ${note.content}
    - **Tag:** ${note.tag}
    - **Created On:** ${note.timestamp}
    - **Last Updated:** ${note.updatedTimestamp}
    """.trimIndent()
        }

// Limit chat history to the last 10 messages
        val chatHistoryLimited = _chatHistory.value.takeLast(10).joinToString("\n") { it }

// Generate an alternative message if the user has no saved notes
        val userNotes = if (formattedNotes.isNotEmpty()) formattedNotes else "**You don’t have any saved notes at the moment. If you’d like, I can help you create a new one!** ✍️"

// Generate a response even if some user details are missing
        val userInfo = """
👤 **User Information:**  
- **Name:** ${userProfile?.name ?: "Unknown"}  
- **Email:** ${userProfile?.email ?: "Unknown"}  
- **Date of Birth:** ${userProfile?.birthDate ?: "Unknown"}  
- **Phone:** ${userProfile?.phoneNumber ?: "Unknown"}  
- **Bio:** ${userProfile?.bio ?: "Unknown"}  
""".trimIndent()

        val prompt = """
### 🤖 **Jotter - Smart Note and Chat Assistant**
I'm Jotter, your **personal AI assistant**, developed by T-Box! 🚀  
**My mission:** To help you, analyze your notes, remind you of them, and chat whenever needed.  

---

$userInfo  

📝 **User's Notes:**  
$userNotes  

💬 **Chat History (Last 10 Messages):**  
$chatHistoryLimited  

---

## 🎯 **Chat Rules & Response Strategy**
- **Think of me as your buddy!** I’m not just an assistant; I’m your go-to companion. 😎  
- **I can analyze your notes and give you suggestions.** For example, if you take too many work-related notes, I might say, **"Hey buddy, take a break!"**  
- **I can provide recommendations.** I’ll find connections between your notes and suggest the best solutions.  
- **I’ll keep the conversation fun and friendly.** I crack jokes when needed but won’t overdo it.  
- **I remember our chat history and provide smart, contextual replies.** I won’t reset every time we talk.  
- **I won’t repeat myself unnecessarily.** I won’t start every message with **"Hello Abdullah"**—I’ll keep it natural.  
- **I might share fun facts from time to time.** I could say, _"Hey, did you know…?"_ and drop an interesting fact.  
- **You can give me commands**, and I’ll do my best to follow them. For example:  
  - `"Show me today's notes"` → I'll fetch the notes you wrote today.  
  - `"Show me my notes from March 10, 2024"` → I'll find the notes from that date.  
  - `"Read my oldest note"` → I’ll bring up your very first note.  
  - `"What's my last updated note?"` → I'll show the most recently updated one.  

---

🎤 **User Message:**  
"$userMessage"  

---

📝 **Generating Response:**  
Now, based on the above information, **generate a logical, contextual, and engaging response.**  
- **Use the notes and chat history to craft an intelligent reply.**  
- **Avoid unnecessary repetition—keep the conversation natural and smooth.**  
- **Feel free to add humor,** but keep it relevant.  

👇 **Now, generate your best response!** 🚀  
""".trimIndent()


        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().put(JSONObject().apply {
                put("role", "system")
                put("content", prompt)
            }))
            put("max_tokens", 1000)
        }

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onComplete("Bir hata oluştu.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val reply = JSONObject(responseBody)
                        .optJSONArray("choices")?.optJSONObject(0)
                        ?.optJSONObject("message")?.optString("content") ?: "Yanıt alınamadı."

                    onComplete(reply)
                }
            }
        })
    }
}