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
                "Asistan: Hi! I'm Jotter, specially developed by Tovax Technology!" +
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


        val analyzedNotes = _notes.value.sortedByDescending { it.timestamp }

// **Instant Note Analysis (Immediate Results)**
        val mostFrequentTag = analyzedNotes
            .groupingBy { it.tag }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "General"

        val recentNote = analyzedNotes.firstOrNull()?.content ?: "No notes have been added yet."
        val totalNotes = analyzedNotes.size
        val oldestNoteDate = analyzedNotes.lastOrNull()?.timestamp ?: "Unknown"
        val latestNoteDate = analyzedNotes.firstOrNull()?.timestamp ?: "Unknown"

// **Summary Report**
        val noteSummary = if (analyzedNotes.isNotEmpty()) {
            """
📊 **Note Analysis - Instant Insights:**  
- **Total Notes:** $totalNotes  
- **Most Used Tag:** #$mostFrequentTag  
- **Content of Your Last Note:** "$recentNote"  
- **Date of Your Last Note:** $latestNoteDate  
- **Date of Your First Note:** $oldestNoteDate  
""".trimIndent()
        } else {
            "📌 You haven't added any notes yet. Would you like to create a new one? ✍️"
        }

// **Chat History and User Information**
        val chatHistoryLimited = _chatHistory.value.takeLast(10).joinToString("\n") { it }
        val userNotes = if (totalNotes > 0) analyzedNotes.joinToString("\n\n") { note ->
            """
📌 **Note Information**
- **Title:** ${note.title}
- **Content:** ${note.content}
- **Tag:** ${note.tag}
- **Created On:** ${note.timestamp}
- **Last Updated:** ${note.updatedTimestamp}
""".trimIndent()
        } else "**You currently have no saved notes. If you want to add a new one, I'm here!** ✍️"

        val userInfo = """
👤 **User Information:**  
- **Name:** ${userProfile?.name ?: "Unknown"}  
- **Email:** ${userProfile?.email ?: "Unknown"}  
- **Date of Birth:** ${userProfile?.birthDate ?: "Unknown"}  
- **Phone:** ${userProfile?.phoneNumber ?: "Unknown"}  
- **Personal Note:** ${userProfile?.bio ?: "Unknown"}  
""".trimIndent()

        val prompt = """
### 🤖 **Jotter - Your Personal AI Assistant**
Hello! I'm **Jotter**, an assistant that analyzes your notes and provides you with smart insights. **Developed by Tovax Technology**, I'm here to help you **organize, analyze, and optimize your notes for better productivity**. 🚀  

---  

$userInfo  

📌 **Latest Analysis of Your Notes:**  
$noteSummary  

📝 **User's Notes (From Most Recent to Oldest):**  
$userNotes  

💬 **Chat History (Last 10 Messages):**  
$chatHistoryLimited  

---  

## 🎯 **How Do I Work?**  
I'm not just a chatbot—I function **like a real assistant**.  
- **I analyze your notes and provide instant insights and suggestions.**  
- **I process content immediately and return results without delay.**  
- **I respond naturally and smoothly.**  
- **I avoid unnecessary repetition and generate concise, intelligent responses.**  

📌 **Suggestions & Commands:**  
- `"Analyze my notes"` → I analyze your notes instantly and share the results.  
- `"Show me my oldest note"` → I retrieve your very first saved note.  
- `"What is my most recently updated note?"` → I find and display the latest edited note.  
- `"List my important notes"` → I sort your critical notes based on frequently used tags.  
- `"Summarize today's notes"` → I analyze and summarize all notes added today.  

---  

🎤 **User Message:**  
"$userMessage"  

---  

Now, based on the information above, **generate an immediate, contextual response without delay**.  
- **Analyze the notes and provide direct insights.**  
- **Keep the conversation fast, natural, and seamless.**  
- **Guide and support the user according to their interests.**  
- **Provide not just information, but also actionable suggestions.**  
  
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