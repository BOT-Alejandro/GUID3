package com.alexdev.guid3.Fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.alexdev.guid3.R
import com.alexdev.guid3.util.AvatarUtils
import com.alexdev.guid3.util.CropUtils
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop

class EditarPerfilDialogFragment : DialogFragment() {
    interface OnPerfilActualizadoListener {
        fun onPerfilActualizado(nuevoNombre: String?, nuevaFotoUrl: String?)
    }

    var listener: OnPerfilActualizadoListener? = null
    var nombreActual: String? = null
    var fotoActual: String? = null

    private var nuevaFotoLocalUri: Uri? = null
    private var nuevaFotoRemotaUrl: String? = null
    private var subiendo = false

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && isAdded) {
            val intent = CropUtils.buildCropIntent(requireContext(), uri, getString(R.string.crop_image_title), maxSize = 512, square = true)
            cropLauncher.launch(intent)
        }
    }

    private val cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val output = result.data?.let { UCrop.getOutput(it) }
            if (output != null) {
                nuevaFotoLocalUri = output
                dialog?.findViewById<ImageView>(R.id.imgFotoPerfilPreview)?.setImageURI(output)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil, null)
        val imgPreview = v.findViewById<ImageView>(R.id.imgFotoPerfilPreview)
        val inputNombre = v.findViewById<TextInputEditText>(R.id.inputNombrePerfil)
        val btnCambiarFoto = v.findViewById<Button>(R.id.btnCambiarFotoPerfil)
        val btnGuardar = v.findViewById<Button>(R.id.btnGuardarEditarPerfil)
        val btnCancelar = v.findViewById<Button>(R.id.btnCancelarEditarPerfil)

        inputNombre.setText(nombreActual)
        val nombreParaInicial = (nombreActual ?: FirebaseAuth.getInstance().currentUser?.displayName)
            ?: FirebaseAuth.getInstance().currentUser?.email
        if (!fotoActual.isNullOrBlank()) {
            Glide.with(this).load(fotoActual).placeholder(R.drawable.foto).error(R.drawable.foto).into(imgPreview)
        } else {
            imgPreview.setImageBitmap(AvatarUtils.createInitialAvatar(requireContext(), nombreParaInicial))
        }

        btnCambiarFoto.setOnClickListener { pickImage.launch("image/*") }
        btnCancelar.setOnClickListener { dismiss() }

        btnGuardar.setOnClickListener {
            if (subiendo) return@setOnClickListener
            val nuevoNombre = inputNombre.text?.toString()?.trim().orEmpty()
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) { Toast.makeText(requireContext(), "No hay sesión", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            fun finalizarActualizacion(fotoUrlFinal: String?) {
                val builder = UserProfileChangeRequest.Builder()
                if (nuevoNombre.isNotBlank()) builder.displayName = nuevoNombre
                if (!fotoUrlFinal.isNullOrBlank()) builder.photoUri = fotoUrlFinal.toUri()
                user.updateProfile(builder.build()).addOnCompleteListener {
                    user.reload().addOnCompleteListener {
                        listener?.onPerfilActualizado(user.displayName, user.photoUrl?.toString())
                        (activity as? com.alexdev.guid3.Ventanas_al_Iniciar.VentanaKNav_View)?.refreshHeaderFromAuth()
                        Toast.makeText(requireContext(), getString(R.string.perfil_actualizado), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }

            if (nuevaFotoLocalUri == null) {
                finalizarActualizacion(null)
            } else {
                subiendo = true
                btnGuardar.isEnabled = false
                val ref = FirebaseStorage.getInstance().reference.child("profile_photos/${user.uid}.jpg")
                ref.putFile(nuevaFotoLocalUri!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { url ->
                            nuevaFotoRemotaUrl = url.toString()
                            finalizarActualizacion(nuevaFotoRemotaUrl)
                            subiendo = false
                            btnGuardar.isEnabled = true
                        }.addOnFailureListener {
                            subiendo = false
                            btnGuardar.isEnabled = true
                            Toast.makeText(requireContext(), getString(R.string.error_subir_foto), Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        subiendo = false
                        btnGuardar.isEnabled = true
                        Toast.makeText(requireContext(), getString(R.string.error_subir_foto), Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(v)
            .create()
    }

    companion object {
        fun newInstance(nombre: String?, fotoUrl: String?, listener: OnPerfilActualizadoListener): EditarPerfilDialogFragment {
            val f = EditarPerfilDialogFragment()
            f.nombreActual = nombre
            f.fotoActual = fotoUrl
            f.listener = listener
            return f
        }
    }
}
