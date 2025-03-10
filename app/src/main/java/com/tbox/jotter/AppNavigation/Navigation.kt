package com.tbox.jotter.AppNavigation


import androidx.navigation.NavHostController
import com.tbox.jotter.Auth.AuthViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.tbox.jotter.ScreenAssistant.ChatScreen
import com.tbox.jotter.ScreenAssistant.ChatViewModel
import com.tbox.jotter.ScreenHome.ScreenHome
import com.tbox.jotter.ScreenPermission.PermissionScreen
import com.tbox.jotter.ScreenProfile.ScreenProfile
import com.tbox.jotter.ScreenProfile.ScreenProfileEdit
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotes
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesAdd
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesDetails
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesEdit
import com.tbox.jotter.ScreenRegistration.ScreenLogin
import com.tbox.jotter.ScreenRegistration.ScreenReset
import com.tbox.jotter.ScreenSplash.ScreenSplash
import com.tbox.jotter.signup.ScreenSignUp


@Composable
fun Navigation (
    navController : NavHostController,
    authViewModel: AuthViewModel
){

    NavHost(navController = navController , startDestination = "permission" ){


        composable("splash"){
            ScreenSplash(navController = navController, authViewModel = authViewModel)
        }

        composable("permission"){
            PermissionScreen(navController)
        }


        composable("home"){
            ScreenHome(navController = navController)
        }



        composable("quick_notes_add"){
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenQuickNotesAdd(navController = navController , uid = currentUser?.uid?: "")
        }



        composable("quick_notes"){
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenQuickNotes(navController = navController, uid = currentUser?.uid?: "")
        }



        composable("quick_notes_detail/{noteId}"){
            backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenQuickNotesDetails(noteId = noteId , uid = currentUser?.uid ?: "", navController = navController)

        }




        composable("quick_notes_edit/{uid}/{noteId}"){
            backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ScreenQuickNotesEdit(navController,uid,noteId)

        }



        composable("quick_notes_chatbot"){
            val chatViewModel = ChatViewModel()
            ChatScreen(navController = navController ,chatViewModel )
        }


        composable("profile"){
            ScreenProfile(navController = navController )
        }

        composable("profile_edit"){
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenProfileEdit(navController = navController , userId = currentUser?.uid?: "")
        }

        composable("login"){
            ScreenLogin(navController = navController,authViewModel = authViewModel)
        }


        composable("signup"){
            ScreenSignUp(navController = navController, authViewModel = authViewModel)
        }


        composable("reset"){
            ScreenReset(navController = navController,authViewModel = authViewModel)
        }


    }
}