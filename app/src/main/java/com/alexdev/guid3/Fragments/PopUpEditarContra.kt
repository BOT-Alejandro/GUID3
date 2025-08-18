package com.alexdev.guid3.Fragments

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.alexdev.guid3.R
import com.google.android.material.textfield.TextInputEditText
import com.yalantis.ucrop.UCrop
import java.io.File
import androidx.core.net.toUri

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

    // Lanzador para seleccionar imagen
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("PopUpEditarContra", "pickImageLauncher: uri = $uri")
        if (uri != null) {
            // Crear archivo temporal para el recorte
            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "icono_personalizado_${System.currentTimeMillis()}.jpg"))
            Log.d("PopUpEditarContra", "pickImageLauncher: destinationUri = $destinationUri")
            val intent = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(256, 256)
                .getIntent(requireContext())
            cropImageLauncher.launch(intent)
        } else {
            Log.e("PopUpEditarContra", "No se seleccionó ninguna imagen")
            Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para recibir imagen recortada
    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("PopUpEditarContra", "cropImageLauncher: resultCode = ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            Log.d("PopUpEditarContra", "cropImageLauncher: data = $data")
            val resultUri = data?.let { UCrop.getOutput(it) }
            Log.d("PopUpEditarContra", "cropImageLauncher: resultUri = $resultUri")
            if (resultUri != null) {
                iconoPersonalizado = resultUri.toString()
                if (isAdded && view != null) {
                    val imgIcono = view?.findViewById<ImageView>(R.id.imgIcono)
                    if (imgIcono != null) {
                        actualizarVistaPreviaIcono(imgIcono)
                    }
                }
            } else {
                Log.e("PopUpEditarContra", "No se pudo recortar la imagen: resultUri es null")
                Toast.makeText(requireContext(), "No se pudo recortar la imagen", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.d("PopUpEditarContra", "Recorte cancelado por el usuario")
            Toast.makeText(requireContext(), "Recorte cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("imgSeleccionada", imgSeleccionada ?: R.drawable.subir_imagen_icono)
        outState.putString("iconoPersonalizado", iconoPersonalizado)
        outState.putString("tituloActual", tituloActual)
        outState.putString("correoActual", correoActual)
        outState.putString("contraActual", contraActual)
    }

    private fun actualizarVistaPreviaIcono(imgIcono: ImageView) {
        if (iconoPersonalizado != null) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(iconoPersonalizado!!.toUri())
                if (inputStream != null) {
                    imgIcono.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                    inputStream.close()
                } else {
                    imgIcono.setImageResource(imgSeleccionada ?: R.drawable.subir_imagen_icono)
                }
            } catch (e: Exception) {
                imgIcono.setImageResource(imgSeleccionada ?: R.drawable.subir_imagen_icono)
            }
        } else {
            imgIcono.setImageResource(imgSeleccionada ?: R.drawable.subir_imagen_icono)
        }
    }

    private fun mostrarSelectorIconos(imgIcono: ImageView) {
        val selector = IconSelectorBottomSheet.newInstance(object : IconSelectorBottomSheet.OnIconSelectedListener {
            override fun onIconSelected(iconRes: Int) {
                imgSeleccionada = iconRes
                iconoPersonalizado = null
                actualizarVistaPreviaIcono(imgIcono)
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

        // Restaurar estado si existe
        if (savedInstanceState != null) {
            imgSeleccionada = savedInstanceState.getInt("imgSeleccionada", imgSeleccionada ?: R.drawable.subir_imagen_icono)
            iconoPersonalizado = savedInstanceState.getString("iconoPersonalizado", iconoPersonalizado)
            tituloActual = savedInstanceState.getString("tituloActual", tituloActual)
            correoActual = savedInstanceState.getString("correoActual", correoActual)
            contraActual = savedInstanceState.getString("contraActual", contraActual)
        }

        actualizarVistaPreviaIcono(imgIcono)
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
            Log.d("PopUpEditarContra", "GUARDAR: imgSeleccionada=$imgSeleccionada, iconoPersonalizado=$iconoPersonalizado, titulo=$nuevoTitulo, correo=$nuevoCorreo, contra=$nuevaContra")
            listener?.onContraEditada(imgSeleccionada ?: R.drawable.subir_imagen_icono, iconoPersonalizado, nuevoTitulo, nuevoCorreo, nuevaContra)
            dismiss()
        }
        btnCancelar.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
