package com.alexdev.guid3.Fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alexdev.guid3.R
import com.bumptech.glide.Glide
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PopUpCrearContra : Fragment(R.layout.pop_up_crear_contra) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textiInputContrasena = view.findViewById<TextInputLayout>(R.id.textInputContrasena)
        val editTextContrasena = view.findViewById<TextInputEditText>(R.id.editTextContrasena)
        val progressBarContrasena = view.findViewById<ProgressBar>(R.id.progressRigidez)
        val presetFacebook = view.findViewById<ImageView>(R.id.presetFacebook)
        val presetGmail = view.findViewById<ImageView>(R.id.presetGmail)
        val presetYoutube = view.findViewById<ImageView>(R.id.presetYoutube)
        val presetInstagram = view.findViewById<ImageView>(R.id.presetInstagram)
        val presetBBVA = view.findViewById<ImageView>(R.id.presetBBVA)
        val presetX = view.findViewById<ImageView>(R.id.presetX)
        val presetOutlook = view.findViewById<ImageView>(R.id.presetOutlook)
        val presetLogo1 = view.findViewById<ImageView>(R.id.presetLogo1)
        val presetLogo2 = view.findViewById<ImageView>(R.id.presetLogo2)
        val presetLogo3 = view.findViewById<ImageView>(R.id.presetMercadoPago)
        val switchAvanzado = view.findViewById<SwitchMaterial>(R.id.switchAvanzado)
        val layoutOpcionesAvanzadas = view.findViewById<RelativeLayout>(R.id.layoutOpcionesAvanzadas)
        val btnGenerar = view.findViewById<Button>(R.id.btnGenerar)
        val btnGuardar = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGuardar)
        val btnCancelar = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelar)

        Glide.with(this)
            .load(R.drawable.logo_facebook)
            .into(presetFacebook)

        Glide.with(this)
            .load(R.drawable.logo_gmail)
            .into(presetGmail)

        Glide.with(this)
            .load(R.drawable.logo_youtube)
            .into(presetYoutube)

        Glide.with(this)
            .load(R.drawable.logo_instagram)
            .into(presetInstagram)

        Glide.with(this)
            .load(R.drawable.logo_bbva)
            .into(presetBBVA)

        Glide.with(this)
            .load(R.drawable.logo_x)
            .into(presetX)

        Glide.with(this)
            .load(R.drawable.logo_outlook)
            .into(presetOutlook)

        Glide.with(this)
            .load(R.drawable.logo_youtube)
            .into(presetLogo1)

        Glide.with(this)
            .load(R.drawable.logo_facebook)
            .into(presetLogo2)




        // Listener para que el switch muestre las opciones avanzadas
        switchAvanzado.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                layoutOpcionesAvanzadas.visibility = View.VISIBLE
                btnGenerar.visibility = View.VISIBLE
            } else {
                layoutOpcionesAvanzadas.visibility = View.GONE
                btnGenerar.visibility = View.GONE
            }
        }

        // Metodo que esta a la espera de cambios en el TextInputEditText de la contraseña
        editTextContrasena.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Metodo que se ejecuta cada vez que se modifica el texto del TextInputEditText
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                evaluarComplejidadDeContrasena(password, textiInputContrasena, progressBarContrasena)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun evaluarComplejidadDeContrasena(
        password: String,
        textInputContrasena: TextInputLayout,
        progressBarContrasena: ProgressBar
    ) {

        var puntuaciondePassword = 0
        // Verifica los criterios de seguridad
        if (password.length >= 8) puntuaciondePassword++
        if (password.any { it.isUpperCase() }) puntuaciondePassword++
        if (password.any { it.isLowerCase() }) puntuaciondePassword++
        if (password.any { it.isDigit() }) puntuaciondePassword++
        if (password.any { "!@#$%^&*()-_+=<>?/".contains(it) }) puntuaciondePassword++

        // Si la contraseña contiene espacios deshabilitar el boton de Guardar
        if (password.contains(" ")) {
            val btnGuardar = requireView().findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGuardar)
            puntuaciondePassword = 0 // Si hay espacios, la contraseña se considera inválida
            textInputContrasena.helperText = "La contraseña no debe contener espacios"
            btnGuardar.isEnabled = false

        } else if(password.isEmpty()){
            textInputContrasena.helperText = "Ingrese una contraseña"
            progressBarContrasena.progress = 0
        } else {
            // Configura la barra de progreso y colores dinámicos
            progressBarContrasena.progress = puntuaciondePassword * 20 // Cada criterio vale 20%
            when (puntuaciondePassword) {
                0, 1 -> { // Contraseña débil
                    progressBarContrasena.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                    textInputContrasena.helperText = "Contraseña muy débil"
                }
                2, 3 -> { // Contraseña regular
                    progressBarContrasena.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light))
                    textInputContrasena.helperText = "Contraseña regular"
                }
                4 -> { // Contraseña fuerte
                    progressBarContrasena.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                    textInputContrasena.helperText = "Contraseña fuerte"
                }
                5 -> { // Contraseña muy fuerte
                    progressBarContrasena.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
                    textInputContrasena.helperText = "Contraseña muy fuerte"
                }
            }
        }
    }
}