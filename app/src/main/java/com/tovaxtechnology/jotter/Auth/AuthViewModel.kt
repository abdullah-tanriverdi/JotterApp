package com.tovaxtechnology.jotter.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel() : ViewModel() {

    private val authService: AuthService = AuthService(FirebaseAuth.getInstance())

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatatus()
    }

    fun checkAuthStatatus() {
        val user = authService.getCurrentUser()
        _authState.value = when {
            user == null -> AuthState.Unauthenticated
            user.isEmailVerified -> AuthState.Authenticated
            else -> AuthState.EmailNotVerified
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        authService.signInWithEmail(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = authService.getCurrentUser()
                    if (user?.isEmailVerified == true) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Email is not verified.")
                    }
                } else {
                    _authState.value = AuthState.Error("Login failed: ${task.exception?.message}")
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
                    _authState.value = AuthState.Success("Verification email sent. Please check your inbox.")
                } else {
                    _authState.value = AuthState.Error("Sign-up failed: ${task.exception?.message}")
                }
            }
    }

    fun deleteAccount() {
        _authState.value = AuthState.Loading
        val task = authService.deleteCurrentUser()
        if (task == null) {
            _authState.value = AuthState.Error("No user is signed in.")
        } else {
            task.addOnCompleteListener { result ->
                _authState.value = if (result.isSuccessful) {
                    AuthState.Success("Account deleted successfully.")
                } else {
                    AuthState.Error("Failed to delete account.")
                }
            }
        }
    }

    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading
        authService.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Password reset email sent.")
                } else {
                    _authState.value = AuthState.Error("Failed to send password reset email.")
                }
            }
    }

    fun signOut() {
        authService.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object EmailNotVerified : AuthState()
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
