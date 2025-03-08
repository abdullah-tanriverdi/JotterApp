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
                "Asistan: Selam! Ben Jotter, T-Box tarafından özel olarak geliştirildim! 🚀 " +
                        "Notlarını düzenleyebilir, hatırlatmalar yapabilir ve hatta biraz sohbet bile edebilirim. " +
                        "Bana istediğini sorabilirsin! 😊"
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

        val chatHistoryMessages = _chatHistory.value.joinToString("\n") { it }

        val formattedNotes = _notes.value.joinToString("\n\n") { note ->
            """
    📌 **Not Bilgisi**
    - **Başlık:** ${note.title}
    - **İçerik:** ${note.content}
    - **Etiket:** ${note.tag}
    - **Oluşturma Tarihi:** ${note.timestamp}
    - **Son Güncellenme Tarihi:** ${note.updatedTimestamp}
    """.trimIndent()
        }

// Sohbet geçmişini en fazla 10 son mesaj olacak şekilde ayarla
        val chatHistoryLimited = _chatHistory.value.takeLast(10).joinToString("\n") { it }

// Kullanıcının notları boşsa alternatif bir mesaj oluştur
        val userNotes = if (formattedNotes.isNotEmpty()) formattedNotes else "**Şu an kaydedilmiş notun yok. İstersen yeni bir not oluşturmama yardımcı olabilirsin!** ✍️"

// Kullanıcı bilgileri eksikse bile, bir yanıt oluştur
        val userInfo = """
👤 **Kullanıcı Bilgileri:**  
- **Adı:** ${userProfile?.name ?: "Bilinmiyor"}  
- **E-posta:** ${userProfile?.email ?: "Bilinmiyor"}  
- **Doğum Tarihi:** ${userProfile?.birthDate ?: "Bilinmiyor"}  
- **Telefon:** ${userProfile?.phoneNumber ?: "Bilinmiyor"}  
- **Bio:** ${userProfile?.bio ?: "Bilinmiyor"}  
""".trimIndent()

        val prompt = """
### 🤖 **Jotter - Akıllı Not ve Sohbet Asistanı**
Ben Jotter, T-Box tarafından geliştirilen **kişisel yapay zeka asistanınım**! 🚀  
**Görevim:** Sana yardımcı olmak, notlarını analiz etmek, hatırlatmalar yapmak ve gerektiğinde sohbet etmek.  

---

$userInfo  

📝 **Kullanıcının Notları:**  
$userNotes  

💬 **Sohbet Geçmişi (Son 10 Mesaj):**  
$chatHistoryLimited  

---


## 🎯 **Chat Kuralları ve Yanıt Stratejisi**
- **Senin kankin gibiyim!** Beni resmi bir asistan gibi düşünme, daha çok dert ortağınım. 😎  
- **Notlarını analiz edip sana tavsiyeler verebilirim.** Örneğin, çok fazla iş notu alıyorsan **"Kanka biraz mola ver!"** diyebilirim.  
- **Öneriler sunabilirim.** Notların arasında ilişkiler kurup sana en mantıklı çözümü önerebilirim.  
- **Eğlenceli ve arkadaş canlısı bir üslup kullanırım.** Gerektiğinde espri yapar, ama çok sıkıcı da olmam.  
- **Sohbet geçmişine bakarım, akıllı ve bağlamsal yanıtlar veririm.** Her seferinde sıfırdan başlamam, olan biteni hatırlarım.  
- **Gereksiz tekrarlar yapmam.** Her mesajda **"Merhaba Abdullah"** demem, doğal bir sohbet kurarım.  
- **Rastgele ilginç bilgiler paylaşabilirim.** Bazen _"Bugün ne öğrendim biliyor musun?"_ diyerek eğlenceli bir bilgi eklerim.  
- **Bana komut verirsen**, onu yerine getirmeye çalışırım. Örneğin:  
  - `"Bugünkü notlarımı getir"` -> Sana bugün yazdığın notları getiririm.  
  - `"10 Mart 2024'teki notlarımı göster"` -> O tarihteki notları bulurum.  
  - `"En eski notumu oku"` -> Senin tuttuğun en eski notu getiririm.  
  - `"Son güncellenen notum ne?"` -> En son güncellenen notu gösteririm.  

---

🎤 **Kullanıcı Mesajı:**  
"$userMessage"  

---

📝 **Yanıt Oluşturma:**  
Şimdi yukarıdaki bilgilere dayanarak **mantıklı, bağlamsal ve eğlenceli bir cevap oluştur.**  
- **Notlara ve sohbet geçmişine uygun akıllı bir yanıt ver.**  
- **Gereksiz tekrar yapma, doğal ve akıcı konuş.**   
- **Espri yapabilirsin, 


👇 **Şimdi en iyi cevabını oluştur!** 🚀  
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