package com.alexdev.guid3.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alexdev.guid3.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val txtTitulo = view.findViewById<TextView>(R.id.txtTitulo)
        val inputNombre = view.findViewById<TextInputLayout>(R.id.inputNombre)
        val btnAccionPrincipal = view.findViewById<MaterialButton>(R.id.btnAccionPrincipal)
        val txtAlternarModo = view.findViewById<TextView>(R.id.txtAlternarModo)
        val layoutInputs = view.findViewById<View>(R.id.layoutInputs)
        val txtError = view.findViewById<TextView>(R.id.txtError)
        val btnGoogle = view.findViewById<MaterialButton>(R.id.btnGoogle)

        // Animación fade para alternar entre login y registro
        fun animateModeChange(isLogin: Boolean) {
            // Fade out todos los elementos
            txtTitulo.animate().alpha(0f).setDuration(150).start()
            btnAccionPrincipal.animate().alpha(0f).setDuration(150).start()
            txtAlternarModo.animate().alpha(0f).setDuration(150).start()
            inputNombre.animate().alpha(0f).setDuration(150).start()
            layoutInputs.animate().alpha(0f).setDuration(150).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Cambiar visibilidad y textos
                    if (isLogin) {
                        txtTitulo.text = "Iniciar sesión"
                        btnAccionPrincipal.text = "Iniciar sesión"
                        txtAlternarModo.text = "¿No tienes cuenta? Regístrate"
                        inputNombre.visibility = View.GONE
                    } else {
                        txtTitulo.text = "Registrarse"
                        btnAccionPrincipal.text = "Registrarse"
                        txtAlternarModo.text = "¿Ya tienes cuenta? Inicia sesión"
                        inputNombre.visibility = View.VISIBLE
                    }
                    // Fade in todos los elementos
                    txtTitulo.animate().alpha(1f).setDuration(200).start()
                    btnAccionPrincipal.animate().alpha(1f).setDuration(200).start()
                    txtAlternarModo.animate().alpha(1f).setDuration(200).start()
                    inputNombre.animate().alpha(if (inputNombre.visibility == View.VISIBLE) 1f else 0f).setDuration(200).start()
                    layoutInputs.animate().alpha(1f).setDuration(200).setListener(null).start()
                }
            }).start()
        }

        // Observa el modo actual y aplica animación
        viewModel.isLoginMode.observe(viewLifecycleOwner, Observer { isLogin ->
            animateModeChange(isLogin)
        })

        // Alternar modo al tocar el texto
        txtAlternarModo.setOnClickListener {
            viewModel.toggleMode()
        }

        // Animación para mostrar mensaje de error
        fun showError(message: String) {
            txtError.text = message
            txtError.visibility = View.VISIBLE
            txtError.alpha = 0f
            txtError.animate().alpha(1f).setDuration(200).start()
        }
        fun hideError() {
            txtError.animate().alpha(0f).setDuration(200).withEndAction {
                txtError.visibility = View.GONE
            }.start()
        }

        // Feedback visual para el botón Google
        btnGoogle.setBackgroundResource(R.drawable.bg_google_button)
        btnGoogle.setRippleColorResource(R.color.Ripple)

        // Feedback visual para alternar modo
        txtAlternarModo.isClickable = true
        txtAlternarModo.isFocusable = true
        txtAlternarModo.setBackgroundResource(R.drawable.bg_text_ripple)

        // Responsividad: ajustar paddings si es necesario
        // ...puedes agregar lógica para ajustar paddings/margins según el tamaño de pantalla...

        // Aquí irá la lógica de autenticación (Firebase y Google) en el siguiente paso
    }
}
