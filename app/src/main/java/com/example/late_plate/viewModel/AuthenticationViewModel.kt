package com.example.late_plate.viewModel
import com.google.firebase.auth.FirebaseAuth

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(val application: Application): ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null

    private val _loginSignupEvent = MutableSharedFlow<LoginSignupUiEvent>()
    val loginSignupEvent = _loginSignupEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState

    var loginAlert = MutableStateFlow(false)
    var signupAlert = MutableStateFlow(false)

    val isLoadingLogin = MutableStateFlow(false)


    fun loginUser(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()) return

        isLoadingLogin.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                task ->
                viewModelScope.launch{
                    if(task.isSuccessful) {
                        user = auth.currentUser
                        Log.d("LOGIN", "SUCCESS!!! ${user!!.uid}")
                        _loginSignupEvent.emit(LoginSignupUiEvent.LoginSuccess)
                        loginAlert.value = false
                        isLoadingLogin.value = false
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user!!.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    User.username.value = document.getString("username").toString()
                                    User.email = document.getString("email").toString()
                                    User.userID = document.getLong("uID")!!
                                    Log.d("FIRESTORE_USER", "Name: ${User.username}, Email: ${User.email}")
                                } else {
                                    Log.d("FIRESTORE_USER", "No such user document!")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("FIRESTORE_USER", "Failed to get user document", exception)
                            }
                    }
                    else {
                        loginAlert.value = true
                        isLoadingLogin.value = false
                    }
                }

            }
    }

    fun navigateToForgotPass(){
        viewModelScope.launch {
            _loginSignupEvent.emit(LoginSignupUiEvent.ToForgotPassScreen)
        }
    }

    fun navigateToSignUp(){
        viewModelScope.launch {
            _loginSignupEvent.emit(LoginSignupUiEvent.ToSignupScreen)
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            user = auth.currentUser
                            _loginSignupEvent.emit(LoginSignupUiEvent.LoginSuccess)

                            val uid = user?.uid ?: return@launch
                            val userMap = hashMapOf(
                                "username" to (user?.displayName ?: "Unknown"),
                                "email" to (user?.email ?: ""),
                                "uID" to 1
                            )

                            firestore.collection("users")
                                .document(uid)
                                .set(userMap)
                        } else {
                            _loginSignupEvent.emit(
                                LoginSignupUiEvent.LoginFailed(
                                    task.exception?.message ?: "Google sign-in failed"
                                )
                            )
                        }
                    }
                }
        }
    }


    fun navigateFromSignupToLogin(){
        viewModelScope.launch {
            _loginSignupEvent.emit(LoginSignupUiEvent.FromSignupToLogin)
        }
    }


    fun registerUser(username: String, email: String, password: String, confirmPass: String){
        if(username.isEmpty() || email.isEmpty() || username.isEmpty()) return
        if(!password.equals(confirmPass)) return
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user?.updateProfile(profileUpdates)

                        val userMap = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "uID" to 1
                        )

                        if (uid != null) {
                            firestore.collection("users")
                                .document(uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    viewModelScope.launch {
                                        _loginSignupEvent.emit((LoginSignupUiEvent.SignupSuccess))
                                    }
                                    signupAlert.value = false
                                }
                                .addOnFailureListener {
                                    viewModelScope.launch {
                                        _loginSignupEvent.emit((LoginSignupUiEvent.SignupFailed))
                                    }
                                    signupAlert.value = true
                                }
                        }

                }
                    else signupAlert.value = true

            }
    }
    fun resetPassword(email: String) {
        val emailTrimmed = email.trim().lowercase()
        _uiState.value = _uiState.value.copy(isLoading = true)

        Firebase.auth.sendPasswordResetEmail(emailTrimmed)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                showMessageAndReset(
                    message = "If this email exists, you'll receive a reset link",
                    isSent = true
                )
                Log.d("Auth", "Reset email result: ${task.isSuccessful}")
            }
    }

    private fun showMessageAndReset(message: String, isSent: Boolean) {
        _uiState.value = ResetPasswordUiState(
            isLoading = true,
            message = message,
            isSent = isSent
        )

        resetStateWithDelay()
    }

    private fun resetStateWithDelay() {
        viewModelScope.launch {
            Log.d("ResetState", "Waiting 3 seconds before resetting state")
            delay(3000)
            Log.d("ResetState", "Resetting state now")
            _uiState.value = ResetPasswordUiState(
                isLoading = false,
                message = "",
                isSent = null
            )
        }
    }

}

sealed class LoginSignupUiEvent {
    object LoginSuccess : LoginSignupUiEvent()
    data class LoginFailed(val message: String) : LoginSignupUiEvent()
    object PasswordResetSuccess : LoginSignupUiEvent()
    object PasswordResetFailed : LoginSignupUiEvent()
    object ToForgotPassScreen: LoginSignupUiEvent()
    object ToSignupScreen: LoginSignupUiEvent()
    object SignupSuccess: LoginSignupUiEvent()
    object SignupFailed: LoginSignupUiEvent()
    object FromSignupToLogin: LoginSignupUiEvent()
}

data class ResetPasswordUiState(
    var isLoading: Boolean = false,
    val message: String = "",
    val isSent: Boolean? = null
)