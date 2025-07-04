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
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.late_plate.util.isConnected
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AuthenticationViewModel @Inject constructor(val application: Application): ViewModel() {
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    var userLoggedIn = MutableStateFlow(false)
        private set


    init {
        Log.d("AUTHENTICATION", "entered view model")
        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            user = auth?.currentUser
            if(user != null){
                userLoggedIn.value = true
            }
        } catch (e: Exception) {
            Log.e("AUTH_VIEWMODEL", "Firebase init failed", e)
        }
    }

    private val _loginSignupEvent = MutableSharedFlow<LoginSignupUiEvent>()
    val loginSignupEvent = _loginSignupEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState

    var loginAlert = MutableStateFlow(false)
    var signupAlert = MutableStateFlow(false)

    val isLoadingLogin = MutableStateFlow(false)


    fun loginUser(email: String, password: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) return

        if (!isConnected(application)) {
            viewModelScope.launch {
                _loginSignupEvent.emit(LoginSignupUiEvent.LoginFailed("No internet connection"))
            }
            return
        }

        isLoadingLogin.value = true

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
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
                    } else {
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
        if (!isConnected(application)) {
            viewModelScope.launch {
                _loginSignupEvent.emit(LoginSignupUiEvent.LoginFailed("No internet connection"))
            }
            return
        }
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


    fun registerUser(username: String, email: String, password: String, confirmPass: String) {
        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim()
        val trimmedPass = password.trim()
        val trimmedConfirmPass = confirmPass.trim()

        if (trimmedUsername.isEmpty() || trimmedEmail.isEmpty() || trimmedPass.isEmpty() || trimmedConfirmPass.isEmpty()) return
        if (trimmedPass != trimmedConfirmPass) {
            signupAlert.value = true
            return
        }

        if (!isConnected(application)) {
            viewModelScope.launch {
                _loginSignupEvent.emit(LoginSignupUiEvent.SignupFailed)
            }
            return
        }

        auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(trimmedUsername)
                        .build()
                    user?.updateProfile(profileUpdates)

                    if (uid != null) {
                        val userMap = hashMapOf(
                            "username" to trimmedUsername,
                            "email" to trimmedEmail,
                            "uID" to 1
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                viewModelScope.launch {
                                    _loginSignupEvent.emit(LoginSignupUiEvent.SignupSuccess)
                                }
                                signupAlert.value = false
                            }
                            .addOnFailureListener {
                                user?.delete() // optional cleanup
                                viewModelScope.launch {
                                    _loginSignupEvent.emit(LoginSignupUiEvent.SignupFailed)
                                }
                                signupAlert.value = true
                            }
                    } else {
                        signupAlert.value = true
                    }
                } else {
                    signupAlert.value = true
                }
            }
    }
    fun resetPassword(email: String) {
        val emailTrimmed = email.trim().lowercase()
        _uiState.value = _uiState.value.copy(isLoading = true)

        if (!isConnected(application)) {
            viewModelScope.launch {
                _loginSignupEvent.emit(LoginSignupUiEvent.LoginFailed("No internet connection"))
            }
            return
        }

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