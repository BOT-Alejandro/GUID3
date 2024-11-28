package com.alexdev.guid3.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.dataClasses.contras
import com.bumptech.glide.Glide

class ContrasAdapter(
    private val contrasenas: List<contras>
) : RecyclerView.Adapter<ContrasAdapter.VistaContrasena>() {

    inner class VistaContrasena(view: View) : RecyclerView.ViewHolder(view) {
        val imagenIcono: ImageView = view.findViewById(R.id.img_de_contrasena)
        val textoTitulo: TextView = view.findViewById(R.id.txtViewTituloContrasena)
        val textoCorreo: TextView = view.findViewById(R.id.textViewCorreo)
        val textoContrasena: TextView = view.findViewById(R.id.textViewContrasena)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaContrasena {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_contras, parent, false)
        return VistaContrasena(vista)
    }

    override fun onBindViewHolder(holder: VistaContrasena, position: Int) {
        val item = contrasenas[position]

        Glide.with(holder.itemView.context)
            .load(item.imgSeleccionada)
            .into(holder.imagenIcono)

        holder.textoTitulo.text = item.titulo
        holder.textoCorreo.text = item.correo
        holder.textoContrasena.text = item.contra
    }

    override fun getItemCount() = contrasenas.size
}
