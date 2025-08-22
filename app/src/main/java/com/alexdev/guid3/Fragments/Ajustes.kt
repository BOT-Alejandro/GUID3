package com.alexdev.guid3.Fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alexdev.guid3.R
import com.alexdev.guid3.Ventanas_al_Iniciar.VentanaKNav_View
import com.alexdev.guid3.util.AvatarUtils
import com.bumptech.glide.Glide
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth

class Ajustes : Fragment(R.layout.fragment_ajustes) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonModoNoche = view.findViewById<SwitchMaterial>(R.id.Switch_modo_noche)
        val switchIdioma = view.findViewById<SwitchMaterial>(R.id.Switch_cambiar_idioma)
        val imgPerfil = view.findViewById<ImageView>(R.id.foto_de_usario)
        val txtNombre = view.findViewById<TextView>(R.id.txtPerfilNombre)
        val txtEmail = view.findViewById<TextView>(R.id.txtPerfilEmail)
        val btnEditarPerfil = view.findViewById<Button>(R.id.btnEditarPerfil)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val nombreMostrar = user.displayName ?: user.email ?: getString(R.string.app_name)
            txtNombre.text = nombreMostrar
            txtEmail.text = user.email ?: ""
            val photo = user.photoUrl?.toString()
            if (!photo.isNullOrBlank()) {
                Glide.with(this).load(photo).placeholder(R.drawable.foto).error(R.drawable.foto).into(imgPerfil)
            } else {
                imgPerfil.setImageBitmap(AvatarUtils.createInitialAvatar(requireContext(), nombreMostrar))
            }
        }

        btnEditarPerfil.setOnClickListener {
            val u = FirebaseAuth.getInstance().currentUser
            val dialog = EditarPerfilDialogFragment.newInstance(u?.displayName, u?.photoUrl?.toString(), object: EditarPerfilDialogFragment.OnPerfilActualizadoListener {
                override fun onPerfilActualizado(nuevoNombre: String?, nuevaFotoUrl: String?) {
                    val updated = FirebaseAuth.getInstance().currentUser
                    if (updated != null) {
                        val nombreMostrarAct = updated.displayName ?: updated.email ?: getString(R.string.app_name)
                        txtNombre.text = nombreMostrarAct
                        txtEmail.text = updated.email ?: ""
                        val p = updated.photoUrl?.toString()
                        if (!p.isNullOrBlank()) {
                            Glide.with(this@Ajustes).load(p).placeholder(R.drawable.foto).error(R.drawable.foto).into(imgPerfil)
                        } else {
                            imgPerfil.setImageBitmap(AvatarUtils.createInitialAvatar(requireContext(), nombreMostrarAct))
                        }
                        (activity as? VentanaKNav_View)?.refreshHeaderFromAuth()
                    }
                }
            })
            dialog.show(parentFragmentManager, "EditarPerfilDialog")
        }

        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = getString(R.string.editar_perfil_titulo)
    }

}