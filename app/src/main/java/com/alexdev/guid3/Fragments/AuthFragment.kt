package com.alexdev.guid3.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alexdev.guid3.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import androidx.core.view.isNotEmpty

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private lateinit var viewModel: AuthViewModel
    private var welcomeAnimated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomeAnimated = savedInstanceState?.getBoolean("welcomeAnimated") ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("welcomeAnimated", welcomeAnimated)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val txtTitulo = view.findViewById<TextView>(R.id.txtTitulo)
        val inputNombreLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.inputNombre)
        val editNombre = view.findViewById<TextInputEditText>(R.id.editNombre)
        val inputEmailLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.inputEmail)
        val editEmail = view.findViewById<TextInputEditText>(R.id.editEmail)
        val inputPassLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.inputPassword)
        val editPassword = view.findViewById<TextInputEditText>(R.id.editPassword)
        val btnAccionPrincipal = view.findViewById<MaterialButton>(R.id.btnAccionPrincipal)
        val txtAlternarModo = view.findViewById<TextView>(R.id.txtAlternarModo)
        val layoutInputs = view.findViewById<View>(R.id.layoutInputs)
        val txtError = view.findViewById<TextView>(R.id.txtError)
        val btnGoogle = view.findViewById<MaterialButton>(R.id.btnGoogle)
        val txtOlvide = view.findViewById<TextView>(R.id.txtOlvide)
        val txtPasswordStrength = view.findViewById<TextView>(R.id.txtPasswordStrength)
        val layoutEmailVerification = view.findViewById<View>(R.id.layoutEmailVerification)
        val btnResendVerification = view.findViewById<MaterialButton>(R.id.btnResendVerification)
        val btnIVerified = view.findViewById<MaterialButton>(R.id.btnIVerified)

        var linkingDialogShown = false

        fun showLinkingDialog() {
            if (linkingDialogShown || !isAdded) return
            linkingDialogShown = true
            val ctx = requireContext()
            val input = TextInputEditText(ctx).apply {
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = getString(R.string.collision_password_hint)
            }
            val layout = com.google.android.material.textfield.TextInputLayout(ctx).apply { addView(input) }
            androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle(R.string.collision_password_prompt_title)
                .setMessage(R.string.collision_password_prompt_message)
                .setView(layout)
                .setPositiveButton(R.string.popup_save) { d, _ ->
                    val pwd = input.text?.toString()?.trim().orEmpty()
                    if (pwd.length < 8) Toast.makeText(ctx, getString(R.string.collision_password_error), Toast.LENGTH_SHORT).show() else { viewModel.linkPendingGoogle(pwd); d.dismiss() }
                }
                .setNegativeButton(R.string.popup_cancel) { d, _ -> viewModel.cancelLinking(); d.dismiss() }
                .setOnDismissListener { linkingDialogShown = false }
                .show()
        }

        fun passwordStrength(pwd: String): Pair<String, Int> {
            if (pwd.isEmpty()) return getString(R.string.password_strength_label) to ContextCompat.getColor(requireContext(), R.color.Gris)
            var score = 0
            if (pwd.length >= 8) score++
            if (pwd.any { it.isLowerCase() }) score++
            if (pwd.any { it.isUpperCase() }) score++
            if (pwd.any { it.isDigit() }) score++
            if (pwd.any { !it.isLetterOrDigit() }) score++
            return when (score) {
                in 0..2 -> getString(R.string.password_weak) to ContextCompat.getColor(requireContext(), R.color.PwdWeak)
                3,4 -> getString(R.string.password_medium) to ContextCompat.getColor(requireContext(), R.color.PwdMedium)
                else -> getString(R.string.password_strong) to ContextCompat.getColor(requireContext(), R.color.PwdStrong)
            }
        }

        fun meetsPasswordPolicy(pwd: String): Boolean {
            val lengthOk = pwd.length >= 8
            val lower = pwd.any { it.isLowerCase() }
            val upper = pwd.any { it.isUpperCase() }
            val digit = pwd.any { it.isDigit() }
            val symbol = pwd.any { !it.isLetterOrDigit() }
            return lengthOk && lower && upper && digit && symbol
        }

        fun setLoading(loading: Boolean) {
            if (viewModel.needsVerification.value == true) {
                btnResendVerification.isEnabled = !loading
                btnIVerified.isEnabled = !loading
                return
            }
            btnAccionPrincipal.isEnabled = !loading
            btnGoogle.isEnabled = !loading
            txtAlternarModo.isEnabled = !loading
            txtOlvide.isEnabled = !loading
            if (loading) btnAccionPrincipal.text = getString(R.string.esperando_label) else {
                val isLogin = viewModel.isLoginMode.value != false
                btnAccionPrincipal.text = if (isLogin) getString(R.string.login_label) else getString(R.string.register_label)
            }
        }

        fun validateFields(isLogin: Boolean): Boolean {
            var ok = true
            val email = editEmail.text?.toString()?.trim().orEmpty()
            val pass = editPassword.text?.toString()?.trim().orEmpty()
            val nombre = editNombre.text?.toString()?.trim().orEmpty()
            inputEmailLayout.error = null
            inputPassLayout.error = null
            inputNombreLayout.error = null
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { inputEmailLayout.error = getString(R.string.error_correo_invalido); ok = false }
            if (!isLogin) {
                if (nombre.isBlank()) { inputNombreLayout.error = getString(R.string.error_nombre_vacio); ok = false }
                else if (nombre.length > 15) { inputNombreLayout.error = getString(R.string.error_nombre_largo); ok = false }
                if (!meetsPasswordPolicy(pass)) { inputPassLayout.error = getString(R.string.error_password_requisitos); ok = false }
            } else if (pass.length < 8) { inputPassLayout.error = getString(R.string.error_pass_corta); ok = false }
            return ok
        }

        fun animateModeChange(isLogin: Boolean) {
            val viewsToFade = listOf<View>(txtTitulo, btnAccionPrincipal, txtAlternarModo, inputNombreLayout, layoutInputs)
            var finished = 0
            viewsToFade.forEach { v ->
                v.animate().alpha(0f).setDuration(150).setListener(object: AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        finished++
                        if (finished == viewsToFade.size) {
                            if (isLogin) {
                                txtTitulo.setText(R.string.login_label)
                                btnAccionPrincipal.setText(R.string.login_label)
                                txtAlternarModo.setText(R.string.switch_to_register)
                                inputNombreLayout.visibility = View.GONE
                            } else {
                                txtTitulo.setText(R.string.register_label)
                                btnAccionPrincipal.setText(R.string.register_label)
                                txtAlternarModo.setText(R.string.switch_to_login)
                                inputNombreLayout.visibility = View.VISIBLE
                            }
                            viewsToFade.forEach { v2 -> v2.alpha = 0f; v2.animate().alpha(1f).setDuration(200).setListener(null).start() }
                        }
                    }
                }).start()
            }
            if (isLogin) {
                txtPasswordStrength.visibility = View.GONE
            } else {
                val pwd = editPassword.text?.toString().orEmpty()
                if (pwd.isNotBlank()) {
                    val (label, color) = passwordStrength(pwd)
                    txtPasswordStrength.text = getString(R.string.password_strength_full, label)
                    txtPasswordStrength.setTextColor(color)
                    txtPasswordStrength.visibility = View.VISIBLE
                }
            }
        }

        editPassword.doAfterTextChanged { txt ->
            val pwd = txt?.toString().orEmpty()
            val isLogin = viewModel.isLoginMode.value != false
            if (isLogin) {
                txtPasswordStrength.visibility = View.GONE
            } else {
                val (label, color) = passwordStrength(pwd)
                txtPasswordStrength.visibility = if (pwd.isBlank()) View.GONE else View.VISIBLE
                if (pwd.isNotBlank()) { txtPasswordStrength.text = getString(R.string.password_strength_full, label); txtPasswordStrength.setTextColor(color) }
            }
        }

        viewModel.needsVerification.observe(viewLifecycleOwner) { needs ->
            if (needs) {
                layoutEmailVerification.visibility = View.VISIBLE
                btnAccionPrincipal.isEnabled = false
                btnGoogle.isEnabled = false
                txtAlternarModo.isEnabled = false
                txtOlvide.isEnabled = false
                txtError.text = getString(R.string.email_not_verified)
                if (!txtError.isVisible) { txtError.visibility = View.VISIBLE; txtError.alpha = 1f }
            } else {
                layoutEmailVerification.visibility = View.GONE
                btnAccionPrincipal.isEnabled = true
                btnGoogle.isEnabled = true
                txtAlternarModo.isEnabled = true
                txtOlvide.isEnabled = true
            }
        }

        btnResendVerification.setOnClickListener { viewModel.sendVerificationEmail(); Toast.makeText(requireContext(), getString(R.string.email_verification_sent), Toast.LENGTH_SHORT).show() }
        btnIVerified.setOnClickListener { viewModel.reloadAndCheckEmail() }
        viewModel.needsLinking.observe(viewLifecycleOwner) { need -> if (need) showLinkingDialog() }
        viewModel.isLoginMode.observe(viewLifecycleOwner, Observer { animateModeChange(it) })
        viewModel.loading.observe(viewLifecycleOwner) { setLoading(it) }
        viewModel.error.observe(viewLifecycleOwner) { err ->
            if (err.isNullOrBlank()) {
                if (txtError.isVisible) txtError.animate().alpha(0f).setDuration(150).withEndAction { txtError.visibility = View.GONE }.start()
            } else {
                txtError.text = err
                if (!txtError.isVisible) { txtError.alpha = 0f; txtError.visibility = View.VISIBLE }
                txtError.animate().alpha(1f).setDuration(150).start()
            }
        }
        viewModel.user.observe(viewLifecycleOwner) { u -> if (u != null) parentFragmentManager.beginTransaction().replace(R.id.fragmento_contenedor, Inicio()).commitAllowingStateLoss() }

        viewModel.resendCooldown.observe(viewLifecycleOwner) { seconds ->
            if (viewModel.needsVerification.value == true) {
                if (seconds > 0) { btnResendVerification.isEnabled = false; btnResendVerification.text = getString(R.string.resend_cooldown, seconds) }
                else { btnResendVerification.isEnabled = true; btnResendVerification.text = getString(R.string.resend_verification) }
            }
        }

        txtAlternarModo.setOnClickListener { viewModel.toggleMode() }
        txtOlvide.setOnClickListener { viewModel.resetPassword(editEmail.text?.toString()?.trim().orEmpty()) }
        btnAccionPrincipal.setOnClickListener {
            val isLogin = viewModel.isLoginMode.value ?: true
            if (!validateFields(isLogin)) return@setOnClickListener
            val email = editEmail.text?.toString()?.trim().orEmpty()
            val pass = editPassword.text?.toString()?.trim().orEmpty()
            if (isLogin) {
                viewModel.login(email, pass)
            } else {
                val name = editNombre.text?.toString()?.trim().orEmpty()
                viewModel.register(name, email, pass)
            }
        }
        editPassword.setOnEditorActionListener { _, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { btnAccionPrincipal.performClick(); true } else false }
        listOf(editEmail, editPassword, editNombre).forEach { it?.doAfterTextChanged { _ -> inputEmailLayout.error = null; inputPassLayout.error = null; inputNombreLayout.error = null } }

        btnGoogle.setBackgroundResource(R.drawable.bg_google_button)
        btnGoogle.setRippleColorResource(R.color.Ripple)
        btnGoogle.setOnClickListener { iniciarSignInGoogle() }
        txtAlternarModo.isClickable = true
        txtAlternarModo.isFocusable = true
        txtAlternarModo.setBackgroundResource(R.drawable.bg_text_ripple)

        if (!welcomeAnimated) animarBienvenida(view)
    }

    private fun animarBienvenida(root: View) {
        if (welcomeAnimated) return // asegurar única ejecución
        val contenedor = root.findViewById<LinearLayout>(R.id.layoutBienvenida) ?: return
        if (contenedor.isNotEmpty()) contenedor.removeAllViews()
        contenedor.visibility = View.VISIBLE
        val frase = getString(R.string.bienvenida_text)
        val scale = resources.displayMetrics.density
        val tvColor = TypedValue()
        val resolved = requireContext().theme.resolveAttribute(android.R.attr.textColorPrimary, tvColor, true)
        val colorInt = if (resolved && tvColor.resourceId != 0) ContextCompat.getColor(requireContext(), tvColor.resourceId) else ContextCompat.getColor(requireContext(), android.R.color.black)
        val inicioDelay = 120L
        val letraDelay = 55L
        Log.d("AuthFragment", "Animación bienvenida solo texto frase='${frase}'")
        frase.forEachIndexed { index, c ->
            val tv = TextView(requireContext()).apply {
                text = c.toString()
                alpha = 0f
                translationX = -32f * scale // aparece desde la izquierda (donde estaba la imagen)
                setTextColor(colorInt)
                textSize = 18f
            }
            contenedor.addView(tv)
            (tv.layoutParams as? LinearLayout.LayoutParams)?.setMargins(0, 0, (2 * scale).toInt(), 0)
            tv.animate()
                .alpha(1f)
                .translationX(0f)
                .setStartDelay(inicioDelay + index * letraDelay)
                .setDuration(260L)
                .withEndAction { if (index == frase.lastIndex) welcomeAnimated = true }
                .start()
        }
    }

    private fun iniciarSignInGoogle() {
        val serverId = getString(R.string.server_client_id)
        if (serverId.isBlank() || serverId == "@string/server_client_id") { viewModel.setError("Configura server_client_id en strings.xml"); return }
        viewModel.clearError()
        viewModel.setLoading(true)
        val credentialManager = CredentialManager.create(requireContext())
        val googleIdOption = GetGoogleIdOption.Builder().setServerClientId(serverId).setFilterByAuthorizedAccounts(false).setAutoSelectEnabled(true).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireContext(), request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleCred.idToken
                        Log.d("AuthFragment", "Token obtenido len=${idToken.length}")
                        if (idToken.isEmpty()) { viewModel.setError("Token Google vacío"); viewModel.setLoading(false) } else { viewModel.setLoading(false); viewModel.loginWithGoogle(idToken) }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("AuthFragment", "Parse token", e); viewModel.setError("Error parseando token Google"); viewModel.setLoading(false)
                    }
                } else { viewModel.setError("Credencial no soportada"); viewModel.setLoading(false) }
            } catch (e: Exception) {
                Log.w("AuthFragment", "Fallo getCredential", e); viewModel.setError(e.message ?: "Cancelado o sin credenciales"); viewModel.setLoading(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
