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
import com.alexdev.guid3.util.CustomIconRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.yalantis.ucrop.UCrop
import java.io.File
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.security.MessageDigest
import androidx.appcompat.app.AlertDialog

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
    private var originalImgSeleccionada: Int? = null
    private var originalIconoPersonalizado: String? = null
    private var iconoModificado = false
    private var originalIconoHash: String? = null
    private var nuevoIconoHash: String? = null
    private var uploadDialog: AlertDialog? = null
    private var isUploading = false

    private fun computeFileHash(uri: Uri): String? {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(8192)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    md.update(buffer, 0, read)
                }
            }
            md.digest().joinToString("") { String.format("%02x", it) }
        } catch (e: Exception) {
            Log.e("PopUpEditarContra", "Error calculando hash: ${e.message}")
            null
        }
    }

    private fun subirImagenAFirebaseStorage(uri: Uri, hash: String, onSuccess: (String) -> Unit, onSkip: () -> Unit, onError: (Exception) -> Unit) {
        if (originalIconoHash != null && originalIconoHash == hash && originalIconoPersonalizado != null) {
            Log.d("PopUpEditarContra", "Hash igual al original, se evita re-subida")
            onSkip()
            return
        }
        val storageRef = FirebaseStorage.getInstance().reference
        val nombreArchivo = "iconos_personalizados/${System.currentTimeMillis()}_${hash.take(12)}.jpg"
        val imagenRef = storageRef.child(nombreArchivo)
        val uploadTask = imagenRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            imagenRef.downloadUrl.addOnSuccessListener { url ->
                Log.d("PopUpEditarContra", "Imagen subida: $url")
                onSuccess(url.toString())
            }.addOnFailureListener { e -> onError(e) }
        }.addOnFailureListener { e -> onError(e) }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d("PopUpEditarContra", "pickImageLauncher: uri = $uri")
        if (uri != null) {
            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "icono_personalizado_${System.currentTimeMillis()}.jpg"))
            val intent = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(256, 256)
                .getIntent(requireContext())
            cropImageLauncher.launch(intent)
        } else {
            Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("PopUpEditarContra", "cropImageLauncher: resultCode = ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val resultUri = data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                val hash = computeFileHash(resultUri)
                nuevoIconoHash = hash
                iconoModificado = true
                if (hash != null) {
                    isUploading = true
                    showUploadDialog()
                    subirImagenAFirebaseStorage(resultUri, hash,
                        onSuccess = { urlSubida ->
                            iconoPersonalizado = urlSubida
                            CustomIconRepository.addCustomIcon(requireContext(), hash, urlSubida)
                            if (isAdded) {
                                view?.findViewById<ImageView>(R.id.imgIcono)?.let { img ->
                                    Glide.with(this).load(iconoPersonalizado).placeholder(R.drawable._logonotfound).error(R.drawable._logonotfound).into(img)
                                }
                            }
                            isUploading = false
                            dismissUploadDialog()
                        },
                        onSkip = {
                            iconoPersonalizado = originalIconoPersonalizado
                            if (isAdded) actualizarVistaPreviaIcono(view?.findViewById(R.id.imgIcono)!!)
                            isUploading = false
                            dismissUploadDialog()
                        },
                        onError = { e ->
                            Toast.makeText(requireContext(), "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                            isUploading = false
                            dismissUploadDialog()
                        }
                    )
                } else {
                    view?.findViewById<ImageView>(R.id.imgIcono)?.let { img ->
                        Glide.with(this).load(resultUri).placeholder(R.drawable._logonotfound).into(img)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "No se pudo recortar la imagen", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(requireContext(), "Recorte cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarVistaPreviaIcono(imgIcono: ImageView) {
        val recursoFallback = R.drawable._logonotfound
        when {
            iconoPersonalizado != null -> {
                Glide.with(this)
                    .load(iconoPersonalizado)
                    .placeholder(recursoFallback)
                    .error(recursoFallback)
                    .into(imgIcono)
            }
            (imgSeleccionada ?: 0) > 0 -> imgIcono.setImageResource(imgSeleccionada!!)
            else -> imgIcono.setImageResource(recursoFallback)
        }
    }

    private fun mostrarSelectorIconos(imgIcono: ImageView) {
        val selector = IconSelectorBottomSheet.newInstance(object : IconSelectorBottomSheet.OnIconSelectedListener {
            override fun onIconSelected(iconRes: Int) {
                imgSeleccionada = iconRes
                iconoPersonalizado = null
                iconoModificado = true
                nuevoIconoHash = null
                actualizarVistaPreviaIcono(imgIcono)
            }
            override fun onRemoteIconSelected(url: String) {
                iconoPersonalizado = url
                imgSeleccionada = 0
                iconoModificado = true
                nuevoIconoHash = null
                actualizarVistaPreviaIcono(imgIcono)
            }
            override fun onCustomImageRequested() {
                pickImageLauncher.launch("image/*")
            }
            override fun onIconsDeleted(urls: List<String>) {
                if (iconoPersonalizado != null && urls.contains(iconoPersonalizado)) {
                    iconoPersonalizado = null
                    imgSeleccionada = 0
                    actualizarVistaPreviaIcono(imgIcono)
                }
            }
        })
        selector.show(parentFragmentManager, "IconSelectorBottomSheet")
    }

    private fun showUploadDialog() {
        if (uploadDialog?.isShowing == true) return
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.progress_upload_dialog, null)
        uploadDialog = AlertDialog.Builder(requireContext())
            .setView(v)
            .setCancelable(false)
            .create()
        uploadDialog?.show()
        updateGuardarState()
    }
    private fun dismissUploadDialog() {
        uploadDialog?.dismiss()
        updateGuardarState()
    }

    private fun updateGuardarState() {
        val root = view ?: return
        val btn = root.findViewById<MaterialButton?>(R.id.btnGuardar) ?: return
        btn.isEnabled = !isUploading
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

        if (originalImgSeleccionada == null) originalImgSeleccionada = imgSeleccionada
        if (originalIconoPersonalizado == null) originalIconoPersonalizado = iconoPersonalizado
        if (originalIconoPersonalizado != null && !originalIconoPersonalizado!!.startsWith("http")) {
            try { originalIconoHash = computeFileHash(originalIconoPersonalizado!!.toUri()) } catch (_: Exception) {}
        }

        if (savedInstanceState != null) {
            imgSeleccionada = savedInstanceState.getInt("imgSeleccionada", imgSeleccionada ?: 0)
            iconoPersonalizado = savedInstanceState.getString("iconoPersonalizado", iconoPersonalizado)
            tituloActual = savedInstanceState.getString("tituloActual", tituloActual)
            correoActual = savedInstanceState.getString("correoActual", correoActual)
            contraActual = savedInstanceState.getString("contraActual", contraActual)
            iconoModificado = savedInstanceState.getBoolean("iconoModificado", iconoModificado)
            originalImgSeleccionada = savedInstanceState.getInt("originalImgSeleccionada", originalImgSeleccionada ?: 0)
            originalIconoPersonalizado = savedInstanceState.getString("originalIconoPersonalizado", originalIconoPersonalizado)
            originalIconoHash = savedInstanceState.getString("originalIconoHash", originalIconoHash)
            nuevoIconoHash = savedInstanceState.getString("nuevoIconoHash", nuevoIconoHash)
        }

        actualizarVistaPreviaIcono(imgIcono)
        inputTitulo.setText(tituloActual)
        inputCorreo.setText(correoActual)
        inputContrasena.setText(contraActual)

        btnCambiarIcono.setOnClickListener { mostrarSelectorIconos(imgIcono) }

        btnGuardar.setOnClickListener {
            if (isUploading) {
                Toast.makeText(requireContext(), "Espera a que termine la subida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nuevoTitulo = view.findViewById<TextInputEditText>(R.id.inputTitulo).text.toString().trim()
            val nuevoCorreo = view.findViewById<TextInputEditText>(R.id.inputCorreo).text.toString().trim()
            val nuevaContra = view.findViewById<TextInputEditText>(R.id.inputContrasena).text.toString().trim()
            val finalImgRes: Int
            val finalIconoPers: String?
            if (iconoModificado) {
                finalImgRes = (imgSeleccionada ?: 0)
                finalIconoPers = iconoPersonalizado
            } else {
                finalImgRes = (originalImgSeleccionada ?: 0)
                finalIconoPers = originalIconoPersonalizado
            }
            Log.d("PopUpEditarContra", "GUARDAR: iconoModificado=$iconoModificado imgRes=$finalImgRes iconoPers=$finalIconoPers hashNuevo=$nuevoIconoHash hashOriginal=$originalIconoHash")
            listener?.onContraEditada(finalImgRes, finalIconoPers, nuevoTitulo, nuevoCorreo, nuevaContra)
            dismiss()
        }
        btnCancelar.setOnClickListener { dismiss() }
        updateGuardarState()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("imgSeleccionada", imgSeleccionada ?: 0)
        outState.putString("iconoPersonalizado", iconoPersonalizado)
        outState.putString("tituloActual", tituloActual)
        outState.putString("correoActual", correoActual)
        outState.putString("contraActual", contraActual)
        outState.putBoolean("iconoModificado", iconoModificado)
        outState.putInt("originalImgSeleccionada", originalImgSeleccionada ?: 0)
        outState.putString("originalIconoPersonalizado", originalIconoPersonalizado)
        outState.putString("originalIconoHash", originalIconoHash)
        outState.putString("nuevoIconoHash", nuevoIconoHash)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        fun newInstance(imgSeleccionada: Int, iconoPersonalizado: String?, titulo: String, correo: String, contra: String, listener: OnContraEditadaListener): PopUpEditarContra {
            val fragment = PopUpEditarContra()
            fragment.imgSeleccionada = imgSeleccionada
            fragment.iconoPersonalizado = iconoPersonalizado
            fragment.tituloActual = titulo
            fragment.correoActual = correo
            fragment.contraActual = contra
            fragment.listener = listener
            fragment.originalImgSeleccionada = imgSeleccionada
            fragment.originalIconoPersonalizado = iconoPersonalizado
            return fragment
        }
    }
}
