package com.tovaxtechnology.jotter.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel() : ViewModel() {

    private val authService: AuthService = AuthService(FirebaseAuth.getInstance())

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatatus()
    }

    fun checkAuthStatatus() {
        val user = authService.getCurrentUser()
        if (user == null) {
            _authState.value = AuthState.Unauthenticated
        } else if (user.isEmailVerified) {
            _authState.value = AuthState.Authenicated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        authService.signInWithEmail(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = authService.getCurrentUser()
                    if (user?.isEmailVerified == true) {
                        _authState.value = AuthState.Authenicated
                    } else {
                        _authState.value = AuthState.Error("E-posta doğrulanmamış.")
                    }
                } else {
                    _authState.value = AuthState.Error("Giriş başarısız.")
                }
            }
    }

    fun signup(email: String, password: String) {
        _authState.value = AuthState.Loading
        authService.createUserWithEmail(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authService.sendEmailVerification()
                    authService.signOut()
                    _authState.value = AuthState.Success("Doğrulama E-postası Gönderildi. Lütfen E-Posta Adresinizi Kontrol Ediniz.")
                } else {
                    _authState.value = AuthState.Error("Kayıt başarısız.")
                }
            }
    }

    fun deleteAccount(onDeleteComplete: () -> Unit) {
        _authState.value = AuthState.Loading
        val user = authService.getCurrentUser()

        if (user == null) {
            _authState.value = AuthState.Error("Kullanıcı oturum açmamış.")
            return
        }

    }

    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading
        authService.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Şifre Sıfırlama E-Postası Gönderildi")
                } else {
                    _authState.value = AuthState.Error("Şifre Sıfırlama E-Postası Gönderilemedi.")
                }
            }
    }

    fun signout(onSignoutComplete: () -> Unit) {
        authService.signOut()
        _authState.value = AuthState.Unauthenticated
        onSignoutComplete()
    }

    sealed class AuthState {
        object Authenicated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
