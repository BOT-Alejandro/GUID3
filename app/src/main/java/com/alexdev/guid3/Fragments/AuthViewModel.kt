package com.alexdev.guid3.Fragments

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.AuthCredential

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoginMode = MutableLiveData(true)
    val isLoginMode: LiveData<Boolean> get() = _isLoginMode

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    private val _user = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val user: LiveData<FirebaseUser?> get() = _user

    private val _needsVerification = MutableLiveData(false)
    val needsVerification: LiveData<Boolean> get() = _needsVerification

    private val _needsLinking = MutableLiveData(false)
    val needsLinking: LiveData<Boolean> get() = _needsLinking
    private var pendingGoogleCredential: AuthCredential? = null
    private var pendingEmail: String? = null

    private val _resendCooldown = MutableLiveData(0)
    val resendCooldown: LiveData<Int> get() = _resendCooldown
    private var cooldownTimer: CountDownTimer? = null

    fun toggleMode() {
        _isLoginMode.value = _isLoginMode.value == false
        _error.value = null
    }

    fun login(email: String, password: String) {
        if (_loading.value == true) return
        _loading.value = true
        _error.value = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { res ->
                val u = res.user
                if (u != null && !u.isEmailVerified && u.providerData.any { it.providerId == "password" }) {
                    _needsVerification.value = true
                    _user.value = u // mantener sesión para poder recargar
                    _error.value = "" // limpiar
                } else {
                    _user.value = u
                    _needsVerification.value = false
                }
                _loading.value = false
            }
            .addOnFailureListener { e ->
                val code = (e as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                _error.value = mapLoginError(code)
                _loading.value = false
            }
    }

    fun register(name: String, email: String, password: String) {
        if (_loading.value == true) return
        _loading.value = true
        _error.value = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { res ->
                val u = res.user
                if (u != null) {
                    if (name.isNotBlank()) {
                        val updates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                        u.updateProfile(updates)
                    }
                    u.sendEmailVerification()
                    _needsVerification.value = true
                    _user.value = u
                }
                _loading.value = false
            }
            .addOnFailureListener { e ->
                val code = (e as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                _error.value = mapRegisterError(code)
                _loading.value = false
            }
    }

    fun sendVerificationEmail() {
        val u = auth.currentUser ?: return
        if (u.isEmailVerified) { _needsVerification.value = false; return }
        if ((_resendCooldown.value ?: 0) > 0) return
        u.sendEmailVerification()
        startCooldown()
        _error.value = null
    }
    private fun startCooldown() {
        cooldownTimer?.cancel()
        _resendCooldown.value = 15
        cooldownTimer = object : CountDownTimer(15_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                _resendCooldown.value = (millisUntilFinished / 1000L).toInt()
            }
            override fun onFinish() { _resendCooldown.value = 0 }
        }.start()
    }

    fun reloadAndCheckEmail() {
        val u = auth.currentUser ?: return
        _loading.value = true
        u.reload().addOnCompleteListener {
            _loading.value = false
            if (u.isEmailVerified) {
                _needsVerification.value = false
                _user.value = auth.currentUser
            } else {
                _needsVerification.value = true
            }
        }
    }

    fun signOut() {
        cooldownTimer?.cancel()
        cooldownTimer = null
        auth.signOut()
        _user.value = null
        _needsVerification.value = false
        _resendCooldown.value = 0
    }

    fun loginWithGoogle(idToken: String) {
        if (_loading.value == true) return
        _loading.value = true
        _error.value = null
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { res ->
                val user = res.user
                _user.value = user
                _loading.value = false
                _needsLinking.value = false
                pendingGoogleCredential = null
                pendingEmail = null
                if (user != null) {
                    val data = mapOf(
                        "uid" to user.uid,
                        "email" to (user.email ?: ""),
                        "displayName" to (user.displayName ?: ""),
                        "photoUrl" to (user.photoUrl?.toString() ?: ""),
                        "provider" to "google"
                    )
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(user.uid)
                        .set(data, SetOptions.merge())
                }
            }
            .addOnFailureListener { e ->
                if (e is FirebaseAuthUserCollisionException) {
                    pendingGoogleCredential = credential
                    pendingEmail = e.email
                    _needsLinking.value = true
                    _error.value = null
                } else {
                    _loading.value = false
                    _error.value = "Error con autenticación de Google"
                }
                _loading.value = false
            }
    }

    fun linkPendingGoogle(password: String) {
        val email = pendingEmail
        val cred = pendingGoogleCredential
        if (email.isNullOrBlank() || cred == null) {
            _error.value = "Estado inválido de vinculación"
            return
        }
        if (password.length < 8) {
            _error.value = "Contraseña incorrecta"
            return
        }
        _loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val current = auth.currentUser
                if (current != null) {
                    current.linkWithCredential(cred)
                        .addOnSuccessListener { lr ->
                            _loading.value = false
                            pendingGoogleCredential = null
                            pendingEmail = null
                            _needsLinking.value = false
                            _user.value = lr.user
                            _error.value = "Cuenta vinculada correctamente"
                        }
                        .addOnFailureListener { le ->
                            _loading.value = false
                            _error.value = mapLoginError((le as? com.google.firebase.auth.FirebaseAuthException)?.errorCode)
                        }
                } else {
                    _loading.value = false
                    _error.value = "Sesión perdida al vincular"
                }
            }
            .addOnFailureListener { se ->
                _loading.value = false
                val code = (se as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                _error.value = mapLoginError(code)
            }
    }

    fun cancelLinking() {
        pendingGoogleCredential = null
        pendingEmail = null
        _needsLinking.value = false
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _error.value = "Ingrese un correo para restablecer"
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { _error.value = "Se envió enlace de restablecimiento" }
            .addOnFailureListener { e -> _error.value = e.localizedMessage ?: "Error al enviar correo" }
    }

    fun setLoading(v: Boolean) { _loading.value = v }
    fun setError(msg: String?) { _error.value = msg }
    fun clearError() { _error.value = null }

    private fun mapLoginError(code: String?): String {
        return when (code) {
            "ERROR_INVALID_EMAIL", "ERROR_INVALID_CREDENTIAL" -> "Credenciales inválidas"
            "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta"
            "ERROR_USER_NOT_FOUND" -> "Usuario no encontrado"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está en uso"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Error de red, inténtalo de nuevo"
            else -> "Error al iniciar sesión"
        }
    }
    private fun mapRegisterError(code: String?): String {
        return when (code) {
            "ERROR_INVALID_EMAIL" -> "Correo inválido"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está en uso"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Error de red, inténtalo de nuevo"
            else -> "Error al registrar"
        }
    }
}
