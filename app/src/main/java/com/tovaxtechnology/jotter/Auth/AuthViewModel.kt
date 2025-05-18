package com.tovaxtechnology.jotter.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException


class AuthViewModel() : ViewModel() {

    private val authService = AuthService()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState


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
                val user = authService.getCurrentUser()
                _authState.value = if (task.isSuccessful && user?.isEmailVerified == true){
                    AuthState.Authenticated
                }else if (task.isSuccessful){
                    AuthState.EmailNotVerified
                }else{
                    AuthState.Unauthenticated
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
                    _authState.value = AuthState.SignUpSuccess

                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
    }


    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading
        authService.sendPasswordResetEmail(email)
        authService.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.ResetSuccess
                } else {

                }
            }

    }

    fun signOut() {
        authService.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun deleteAccount() {
        _authState.value = AuthState.Loading
        val user = authService.getCurrentUser()

        if (user == null) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.AccountDeleted
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthRecentLoginRequiredException) {
                        _authState.value = AuthState.ReauthRequired
                    } else {
                        _authState.value = AuthState.DeleteFailed(exception?.localizedMessage ?: "Unknown error")
                    }
                }
            }
    }




    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object EmailNotVerified : AuthState()
        object Loading : AuthState()
        object ResetSuccess : AuthState()
        object SignUpSuccess : AuthState()
        object AccountDeleted : AuthState()
        object ReauthRequired : AuthState()
        data class DeleteFailed(val message: String) : AuthState()


    }
}


