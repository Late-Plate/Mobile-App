package com.example.late_plate.viewModel
import com.google.firebase.auth.FirebaseAuth

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                        loginAlert.value = false
                    }
                    else loginAlert.value = true
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

                            // Optional: Save to Firestore if new user
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
        val emailTrimmed = email.trim()
        setupEmulatorIfDebug()
        _uiState.value.isLoading = true

        auth.fetchSignInMethodsForEmail(emailTrimmed)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleSignInMethodsResult(emailTrimmed, task.result?.signInMethods)
                } else {
                    showMessageAndReset("Error checking email", isSent = false)
                }
            }
    }

    private fun setupEmulatorIfDebug() {
        if (BuildConfig.DEBUG) {
            try {
                Firebase.auth.useEmulator("10.0.2.2", 9099)
            } catch (e: Exception) {
                Log.e("Emulator", "Emulator setup failed", e)
            }
        }
    }

    private fun handleSignInMethodsResult(email: String, methods: List<String>?) {
        if (!methods.isNullOrEmpty()) {
            sendResetEmail(email)
        } else {
            Log.d("ISLOADING", _uiState.value.isLoading.toString())
            showMessageAndReset("Email not registered", isSent = false)
        }
    }

    private fun sendResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showMessageAndReset("Reset email sent", isSent = true)
                } else {
                    showMessageAndReset("Failed to send reset email", isSent = false)
                }
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




//    fun resetPassword(email: String){
//        if (email.isEmpty()) return
//        Log.d("RESET", "")
//        _uiState.value = _uiState.value.copy(isLoading = true, message = "", isSent = null)
//        checkEmailExists(email) { exists ->
//            if (exists) {
//                auth.sendPasswordResetEmail(email.trim())
//                    .addOnCompleteListener { task ->
//                        viewModelScope.launch {
//                            if (task.isSuccessful) {
//                                Log.d("SUCCESS", "")
//                                _loginSignupEvent.emit(LoginSignupUiEvent.PasswordResetSuccess)
//                                _uiState.value = _uiState.value.copy(isLoading = true, message = "Password reset email sent.", isSent = true)
//                            } else {
//                                Log.d("Fail", "")
//                                _loginSignupEvent.emit(LoginSignupUiEvent.PasswordResetFailed)
//                                _uiState.value = _uiState.value.copy(isLoading = true, message = "Password reset email not sent!", isSent = false)
//                            }
//
//                            kotlinx.coroutines.delay(3000)
//
//
//
//                        }
//                    }
//            }else{
//                Log.d("NOT FOUND", "")
//                _uiState.value = _uiState.value.copy(
//                    isLoading = true,
//                    message = "Password reset email not sent!",
//                    isSent = false
//                )
//                viewModelScope.launch {
//                    kotlinx.coroutines.delay(2000)
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        message = "",
//                        isSent = null
//                    )
//                    _loginSignupEvent.emit(LoginSignupUiEvent.PasswordResetFailed)
//
//                }
//
//            }
//        }
//
//
//
//
//    }



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