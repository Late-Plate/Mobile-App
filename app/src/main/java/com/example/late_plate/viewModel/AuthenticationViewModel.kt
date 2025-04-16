package com.example.late_plate.viewModel
import com.google.firebase.auth.FirebaseAuth

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
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


    fun loginUser(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()) return

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                task ->
                viewModelScope.launch{
                    if(task.isSuccessful) {
                        user = auth.currentUser
                        Log.d("LOGIN", "SUCCESS!!! ${user!!.uid}")
                        _loginSignupEvent.emit(LoginSignupUiEvent.LoginSuccess)
                    }
                    else Log.d("LOGIN", "Fail!!!${user}")
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

    fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        val emailTrimmed = email.trim()
        Log.d("FIREBASE_DEBUG", "Checking existence for: '$emailTrimmed'")

        auth.fetchSignInMethodsForEmail(emailTrimmed)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val methods = task.result?.signInMethods ?: emptyList()
                    Log.d("FIREBASE_DEBUG", "Sign-in methods found: $methods")
                    callback(methods.isNotEmpty())
                } else {
                    val exception = task.exception
                    Log.e("FIREBASE_DEBUG", "Error checking email", exception)

                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            Log.d("FIREBASE_DEBUG", "Firebase explicitly reports user not found")
                        }
                        is FirebaseAuthException -> {
                            Log.d("FIREBASE_DEBUG", "Auth error code: ${exception.errorCode}")
                        }
                        else -> {
                            Log.d("FIREBASE_DEBUG", "Other error type: ${exception?.javaClass?.simpleName}")
                        }
                    }
                    callback(false)
                }
            }
    }


    fun registerUser(username: String, email: String, password: String){
        if(username.isEmpty() || email.isEmpty() || username.isEmpty()) return
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user?.updateProfile(profileUpdates)

                        // Save user to Firestore
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
                                }
                                .addOnFailureListener {
                                    viewModelScope.launch {
                                        _loginSignupEvent.emit((LoginSignupUiEvent.SignupFailed))
                                    }
                                }
                        }

                }

            }
    }



    fun resetPassword(email: String){
        if (email.isEmpty()) return
        Log.d("RESET", "")
        _uiState.value = _uiState.value.copy(isLoading = true, message = "", isSent = null)
        checkEmailExists(email) { exists ->
            if (exists) {
                auth.sendPasswordResetEmail(email.trim())
                    .addOnCompleteListener { task ->
                        viewModelScope.launch {
                            if (task.isSuccessful) {
                                Log.d("SUCCESS", "")
                                _loginSignupEvent.emit(LoginSignupUiEvent.PasswordResetSuccess)
                                _uiState.value = _uiState.value.copy(isLoading = true, message = "Password reset email sent.", isSent = true)
                            } else {
                                Log.d("Fail", "")
                                _loginSignupEvent.emit(LoginSignupUiEvent.PasswordResetFailed)
                                _uiState.value = _uiState.value.copy(isLoading = true, message = "Password reset email not sent!", isSent = false)
                            }

                            kotlinx.coroutines.delay(3000)



                        }
                    }
            }else{
                Log.d("NOT FOUND", "")
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    message = "Password reset email not sent!",
                    isSent = false
                )
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "",
                        isSent = null
                    )
                    _loginSignupEvent.emit(LoginSignupUiEvent.PasswordResetFailed)

                }

            }
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
}

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val isSent: Boolean? = null
)