package com.tovaxtechnology.jotter.Auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthService(private val auth : FirebaseAuth = FirebaseAuth.getInstance()) {

    fun signInWithEmail(email : String , password : String) : Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email , password)

    }

    fun createUserWithEmail(email : String , password : String) : Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email , password)
    }

    fun sendPasswordResetEmail(email : String) : Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }


    fun sendEmailVerification() : Task<Void>? {
        return auth.currentUser?.sendEmailVerification()
    }

    fun signOut() {
        auth.signOut()
    }


    fun getCurrentUser() : FirebaseUser ? {
        return auth.currentUser
    }

    fun deleteCurrentUser(): Task<Void>?{
        return auth.currentUser?.delete()
    }


}