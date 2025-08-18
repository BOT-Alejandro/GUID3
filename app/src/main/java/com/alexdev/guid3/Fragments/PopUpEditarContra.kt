package com.alexdev.guid3.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.alexdev.guid3.R
import com.google.android.material.textfield.TextInputEditText
import com.yalantis.ucrop.UCrop
import java.io.File

class PopUpEditarContra : DialogFragment() {
    interface OnContraEditadaListener {
        fun onContraEditada(imgSeleccionada: Int, iconoPersonalizado: String?, titulo: String, correo: String, contra: String)
    }

    var imgSeleccionada: Int? = null
    var tituloActual: String? = null
    var correoActual: String? = null
    var contraActual: String? = null
    var listener: OnContraEditadaListener? = null
    var iconoPersonalizado: String? = null
    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultUri = data?.let { UCrop.getOutput(it) }
            resultUri?.let {
                iconoPersonalizado = it.toString()
                view?.findViewById<ImageView>(R.id.imgIcono)?.setImageBitmap(BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(it)))
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "icono_personalizado.jpg"))
            val intent = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(256, 256)
                .getIntent(requireContext())
            cropImageLauncher.launch(intent)
        }
    }

    private fun mostrarSelectorIconos(imgIcono: ImageView) {
        val selector = IconSelectorBottomSheet.newInstance(object : IconSelectorBottomSheet.OnIconSelectedListener {
            override fun onIconSelected(iconRes: Int) {
                imgSeleccionada = iconRes
                iconoPersonalizado = null
                imgIcono.setImageResource(iconRes)
            }
            override fun onCustomImageRequested() {
                pickImageLauncher.launch("image/*")
            }
        })
        selector.show(parentFragmentManager, "IconSelectorBottomSheet")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pop_up_editar_contra, container, false)
        val imgIcono = view.findViewById<ImageView>(R.id.imgIcono)
        val btnCambiarIcono = view.findViewById<Button>(R.id.btnCambiarIcono)
        val inputTitulo = view.findViewById<TextInputEditText>(R.id.inputTitulo)
        val inputCorreo = view.findViewById<TextInputEditText>(R.id.inputCorreo)
        val inputContrasena = view.findViewById<TextInputEditText>(R.id.inputContrasena)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        // Mostrar el icono personalizado si existe, si no el drawable
        if (iconoPersonalizado != null) {
            imgIcono.setImageBitmap(BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(Uri.parse(iconoPersonalizado))))
        } else {
            imgIcono.setImageResource(imgSeleccionada ?: R.drawable.subir_imagen_icono)
        }
        inputTitulo.setText(tituloActual)
        inputCorreo.setText(correoActual)
        inputContrasena.setText(contraActual)

        btnCambiarIcono.setOnClickListener {
            mostrarSelectorIconos(imgIcono)
        }

        btnGuardar.setOnClickListener {
            val nuevoTitulo = inputTitulo.text.toString().trim()
            val nuevoCorreo = inputCorreo.text.toString().trim()
            val nuevaContra = inputContrasena.text.toString().trim()
            listener?.onContraEditada(imgSeleccionada ?: R.drawable.subir_imagen_icono, iconoPersonalizado, nuevoTitulo, nuevoCorreo, nuevaContra)
            dismiss()
        }
        btnCancelar.setOnClickListener {
            dismiss()
        }

        return view
    }

    companion object {
        fun newInstance(imgSeleccionada: Int, titulo: String, correo: String, contra: String, listener: OnContraEditadaListener): PopUpEditarContra {
            val fragment = PopUpEditarContra()
            fragment.imgSeleccionada = imgSeleccionada
            fragment.tituloActual = titulo
            fragment.correoActual = correo
            fragment.contraActual = contra
            fragment.listener = listener
            return fragment
        }
    }
}
