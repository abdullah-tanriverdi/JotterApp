package com.tovaxtechnology.jotter.Auth

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.forException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

class AuthService(private val auth : FirebaseAuth) {

    fun signInWithEmail(email : String , password : String) : Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email , password)

    }

    fun createUserWithEmail(email : String , password : String) : Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email , password)
    }

    fun sendPasswordResetEmail(email : String) : Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    fun sendEmailVerification() : Task<Void> {
        return auth.currentUser?.sendEmailVerification() ?: forException(Exception("Kullanıcı oturum açmadı"))
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }


    fun getCurrentUser() : FirebaseUser ? {
        return auth.currentUser
    }


}