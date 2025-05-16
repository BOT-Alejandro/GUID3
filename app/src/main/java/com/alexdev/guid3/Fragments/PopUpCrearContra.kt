package com.alexdev.guid3.Fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.adaptadores.ContrasAdapter
import com.alexdev.guid3.dataClasses.contras
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Random

class PopUpCrearContra : Fragment(R.layout.pop_up_crear_contra) {

    private var imgSeleccionada: Int = R.drawable._logogmail


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textInputContrasena = view.findViewById<TextInputLayout>(R.id.textInputContrasena)
        val editTextContrasena = view.findViewById<TextInputEditText>(R.id.editTextContrasena)
        val textInputNombre = view.findViewById<TextInputEditText>(R.id.editTextNombre)
        val textInputCorreo = view.findViewById<TextInputEditText>(R.id.editTextCorreo)
        val progressBarContrasena = view.findViewById<ProgressBar>(R.id.progressRigidez)
        val presetFacebook = view.findViewById<ImageView>(R.id.presetFacebook)
        val presetGmail = view.findViewById<ImageView>(R.id.presetGmail)
        val presetYoutube = view.findViewById<ImageView>(R.id.presetYoutube)
        val presetInstagram = view.findViewById<ImageView>(R.id.presetInstagram)
        val presetBBVA = view.findViewById<ImageView>(R.id.presetBBVA)
        val presetX = view.findViewById<ImageView>(R.id.presetX)
        val presetOutlook = view.findViewById<ImageView>(R.id.presetOutlook)
        val presetMercadoPago = view.findViewById<ImageView>(R.id.presetMercadoPago)
        val presetNetflix = view.findViewById<ImageView>(R.id.presetNetflix)
        val btnImgPersonalizada = view.findViewById<ImageView>(R.id.btnImgPersonalizada)
        val switchAvanzado = view.findViewById<SwitchMaterial>(R.id.switchAvanzado)
        val layoutOpcionesAvanzadas = view.findViewById<RelativeLayout>(R.id.layoutOpcionesAvanzadas)
        val btnGenerar = view.findViewById<Button>(R.id.btnGenerar)
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)
        val btnSugerencias = view.findViewById<MaterialButton>(R.id.btnSugerencias)

        val presetButtons = mapOf(
            R.id.presetGmail to Pair("Gmail", ::algoritmoGmail),
            R.id.presetYoutube to Pair("YouTube", ::algoritmoYoutube),
            R.id.presetInstagram to Pair("Instagram", ::algoritmoInstagram),
            R.id.presetBBVA to Pair("BBVA", ::algoritmoBBVA),
            R.id.presetX to Pair("X", ::algoritmoX),
            R.id.presetOutlook to Pair("Outlook", ::algoritmoOutlook),
            R.id.presetMercadoPago to Pair("MercadoPago", ::algoritmoMercadoPago),
            R.id.presetNetflix to Pair("Netflix", ::algoritmoNetflix),
            R.id.presetFacebook to Pair("Facebook", ::algoritmoFacebook)
        )

        presetButtons.forEach { (id, pair) ->
            val imageButton = view.findViewById<ImageView>(id)
            imageButton.setOnClickListener {
                val nombre = pair.first
                val algoritmo = pair.second
                textInputNombre.setText("$nombre: ")
                editTextContrasena.setText(algoritmo())
                Toast.makeText(requireContext(), "$nombre seleccionado", Toast.LENGTH_SHORT).show()
            }
        }

        btnImgPersonalizada.setOnClickListener {
            // Aquí, abre un diálogo o cualquier tipo de vista con las opciones de imagen disponibles
        }

        btnGuardar.setOnClickListener {
                // Obtener el fragmento "Inicio" y agregar la nueva contraseña
                val fragmentInicio = Inicio()

                // Navegar de regreso al fragmento "Inicio"
                val supportFragmentManager = requireActivity().supportFragmentManager
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmento_contenedor, fragmentInicio)
                    .addToBackStack(null)
                    .commit()

                // Cerrar el fragmento actual (PopUpCrearContra)
                requireActivity().supportFragmentManager.popBackStack()
        }

        Glide.with(this)
            .load(R.drawable._logogmail)
            .into(presetGmail)

        Glide.with(this)
            .load(R.drawable._logoyoutube)
            .into(presetYoutube)

        Glide.with(this)
            .load(R.drawable._logoinstragram)
            .into(presetInstagram)

        Glide.with(this)
            .load(R.drawable._logobbva)
            .into(presetBBVA)

        Glide.with(this)
            .load(R.drawable._logox)
            .into(presetX)

        Glide.with(this)
            .load(R.drawable._logooutlook)
            .into(presetOutlook)

        Glide.with(this)
            .load(R.drawable._logomercadopago)
            .into(presetMercadoPago)

        Glide.with(this)
            .load(R.drawable._logonetflix)
            .into(presetNetflix)

        Glide.with(this)
            .load(R.drawable._logofacebook)
            .into(presetFacebook)




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

        // onClickListener para abrir el pop up de aviso sobre las sugerencias de algoritmos
        btnSugerencias.setOnClickListener{alPresionarBotonSugerencias()}

        // Metodo que esta a la espera de cambios en el TextInputEditText de la contraseña
        editTextContrasena.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Metodo que se ejecuta cada vez que se modifica el texto del TextInputEditText
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                evaluarComplejidadDeContrasena(password, textInputContrasena, progressBarContrasena)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Metodo que genera una contraseña aleatoria
        btnGenerar.setOnClickListener {
            val contrasenaGenerada = generarContrasena()
            editTextContrasena.setText(contrasenaGenerada)
            Toast.makeText(requireContext(), "Contraseña generada", Toast.LENGTH_SHORT).show()
        }

        btnGuardar.setOnClickListener {
            val editTextNombre = view?.findViewById<TextInputEditText>(R.id.editTextNombre)
            val editTextCorreo = view?.findViewById<TextInputEditText>(R.id.editTextCorreo)
            val editTextContra = view?.findViewById<TextInputEditText>(R.id.editTextContrasena)
            val inputNombre = editTextNombre?.text.toString()
            val inputCorreo = editTextCorreo?.text.toString()
            val inputContra = editTextContra?.text.toString()
            if (inputNombre.isEmpty() && inputCorreo.isEmpty() && inputContra.isEmpty()) {
                btnGuardar.isEnabled = false
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            } else{
                btnGuardar.isEnabled = true
                Toast.makeText(requireContext(), "Contraseña guardada", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    // Algoritmos de generación de contraseñas para cada servicio
    private fun algoritmoGmail(): String = generarContrasenaCompleja()

    private fun algoritmoYoutube(): String = generarContrasenaConSimbolos()

    private fun algoritmoInstagram(): String = generarContrasenaConLongitudFija()

    private fun algoritmoBBVA(): String = generarContrasenaMuySegura()

    private fun algoritmoX(): String = generarContrasenaNumericaConSimbolos()

    private fun algoritmoOutlook(): String = generarContrasenaCompleja()

    private fun algoritmoMercadoPago(): String = generarContrasenaMuySegura()

    private fun algoritmoNetflix(): String = generarContrasenaConLongitudFija()

    private fun algoritmoFacebook(): String = generarContrasenaAlfanumerica()

    // Funciones auxiliares para la generación de contraseñas
    private fun generarContrasenaCompleja(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()"
        return (1..16).map { chars.random() }.joinToString("")
    }

    private fun generarContrasenaConSimbolos(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%"
        return (1..16).map { chars.random() }.joinToString("")
    }

    private fun generarContrasenaConLongitudFija(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..16).map { chars.random() }.joinToString("")
    }

    private fun generarContrasenaMuySegura(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()-_=+[]{}|;:,.<>?"
        return (1..16).map { chars.random() }.joinToString("")
    }

    private fun generarContrasenaNumericaConSimbolos(): String {
        val chars = "0123456789!@#\$%^&*"
        return (1..16).map { chars.random() }.joinToString("")
    }

    private fun generarContrasenaAlfanumerica(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..16).map { chars.random() }.joinToString("")
    }

    private fun generarContrasena(): String {
        val checkMayusculas = view?.findViewById<CheckBox>(R.id.checkMayusculas)
        val checkMinusculas = view?.findViewById<CheckBox>(R.id.checkMinusculas)
        val checkNumeros = view?.findViewById<CheckBox>(R.id.checkNumeros)
        val checkGuion = view?.findViewById<CheckBox>(R.id.checkGuion)
        val checkGuionBajo = view?.findViewById<CheckBox>(R.id.checkGuionBajo)
        val checkEspeciales = view?.findViewById<CheckBox>(R.id.checkEspeciales)

        val mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val minusculas = "abcdefghijklmnopqrstuvwxyz"
        val numeros = "0123456789"
        val simbolos = "!@#%^&*()+=<>?/"

        // Obtener la longitud de la contraseña
        val longitud = obtenerLongitudContrasena()

        // Determinar las categorías habilitadas
        val caracteresDisponibles = StringBuilder()
        if (checkMayusculas != null) {
            if (checkMayusculas.isChecked) caracteresDisponibles.append(mayusculas)
        }
        if (checkMinusculas != null) {
            if (checkMinusculas.isChecked) caracteresDisponibles.append(minusculas)
        }
        if (checkNumeros != null) {
            if (checkNumeros.isChecked) caracteresDisponibles.append(numeros)
        }
        if (checkGuion != null) {
            if (checkGuion.isChecked) caracteresDisponibles.append("-")
        }
        if (checkGuionBajo != null) {
            if (checkGuionBajo.isChecked) caracteresDisponibles.append("_")
        }
        if (checkEspeciales != null) {
            if (checkEspeciales.isChecked) caracteresDisponibles.append(simbolos)
        }

        if (caracteresDisponibles.isEmpty()) {
            // Si no hay ninguna opción seleccionada, devolver una contraseña aleatoria
            caracteresDisponibles.append(mayusculas)
            caracteresDisponibles.append(minusculas)
            caracteresDisponibles.append(numeros)
            caracteresDisponibles.append(simbolos)
            caracteresDisponibles.append("-")
            caracteresDisponibles.append("_")
        }

        // Generar la contraseña aleatoria
        val contrasenaGenerada = StringBuilder()
        val random = Random()
        repeat(longitud) {
            val randomIndex = random.nextInt(caracteresDisponibles.length)
            contrasenaGenerada.append(caracteresDisponibles[randomIndex])
        }

        return contrasenaGenerada.toString()
    }

    // Metodo que obtiene la longitud de la contraseña
    private fun obtenerLongitudContrasena(): Int {
        val editTextLongitudContra = view?.findViewById<TextInputEditText>(R.id.editTextLongitudContra)
        val longitudInput = editTextLongitudContra?.text.toString()
        return if (longitudInput.isEmpty()) {
            // Si no se ingresó longitud, establecer longitud aleatoria entre 8 y 16
            Random().nextInt(9) + 8  // genera entre 8 y 16
        } else {
            val longitud = longitudInput.toIntOrNull() ?: 8
            // Asegurar que esté entre 8 y 16
            longitud.coerceIn(8, 16)
        }
    }

    private fun alPresionarBotonSugerencias(){
        // inflar el diseño del pop up
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.pop_up_sugerencias, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val btnEntendido = dialogView.findViewById<MaterialButton>(R.id.btnEntendido)
        btnEntendido.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun evaluarComplejidadDeContrasena(password: String, textInputContrasena: TextInputLayout, progressBarContrasena: ProgressBar) {

        var puntuaciondePassword = 0
        // Verifica los criterios de seguridad
        if (password.length >= 8) puntuaciondePassword++
        if (password.any { it.isUpperCase() }) puntuaciondePassword++
        if (password.any { it.isLowerCase() }) puntuaciondePassword++
        if (password.any { it.isDigit() }) puntuaciondePassword++
        if (password.any { "!@#$%^&*()-_+=<>?/".contains(it) }) puntuaciondePassword++

        // Si la contraseña contiene espacios deshabilitar el boton de Guardar
        if (password.contains(" ")) {
            val btnGuardar = requireView().findViewById<MaterialButton>(R.id.btnGuardar)
            puntuaciondePassword = 0 // Si hay espacios, la contraseña se considera inválida
            progressBarContrasena.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            textInputContrasena.helperText = "La contraseña no debe contener espacios"
            btnGuardar.isEnabled = false

        } else if (password.isEmpty()) {
            val btnGuardar = requireView().findViewById<MaterialButton>(R.id.btnGuardar)
            textInputContrasena.helperText = "Ingrese una contraseña"
            progressBarContrasena.progress = 0
            btnGuardar.isEnabled = false
        } else if (password.length < 8) {
            val btnGuardar = requireView().findViewById<MaterialButton>(R.id.btnGuardar)
            textInputContrasena.helperText = "La contraseña debe tener al menos 8 caracteres"
            progressBarContrasena.progress = 0
            btnGuardar.isEnabled = false
        } else {
            // Configura la barra de progreso y colores dinámicos
            progressBarContrasena.progress = puntuaciondePassword * 20 // Cada criterio vale 20%
            when (puntuaciondePassword) {
                0, 1 -> { // Contraseña débil
                    progressBarContrasena.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                    textInputContrasena.helperText = "Contraseña muy débil"
                }
                2, 3 -> { // Contraseña regular
                    progressBarContrasena.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light))
                    textInputContrasena.helperText = "Contraseña regular"
                }
                4 -> { // Contraseña fuerte
                    progressBarContrasena.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                    textInputContrasena.helperText = "Contraseña aceptable"
                    val btnGuardar = requireView().findViewById<MaterialButton>(R.id.btnGuardar)
                    btnGuardar.isEnabled = true
                }
                5 -> { // Contraseña muy fuerte
                    progressBarContrasena.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
                    textInputContrasena.helperText = "Contraseña segura"
                    val btnGuardar = requireView().findViewById<MaterialButton>(R.id.btnGuardar)
                    btnGuardar.isEnabled = true
                }
            }
        }
    }
}
