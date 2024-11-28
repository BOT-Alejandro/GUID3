package com.alexdev.guid3.adaptadores

import android.util.Log
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
    private val contrasenas: List<contras> // Lista de contraseñas que será pasada al adaptador
) : RecyclerView.Adapter<ContrasAdapter.VistaContrasena>() {

    // ViewHolder que contiene las vistas de cada item del RecyclerView
    inner class VistaContrasena(view: View) : RecyclerView.ViewHolder(view) {
        val imagenIcono: ImageView = view.findViewById(R.id.img_de_contrasena) // Imagen de icono de la contraseña
        val textoTitulo: TextView = view.findViewById(R.id.txtViewTituloContrasena) // Título de la contraseña
        val textoCorreo: TextView = view.findViewById(R.id.textViewCorreo) // Correo asociado a la contraseña
        val textoContrasena: TextView = view.findViewById(R.id.textViewContrasena) // Contraseña en texto plano
    }

    // Este método crea y devuelve una nueva instancia del ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaContrasena {
        // Inflar el layout del item del RecyclerView (el diseño de cada elemento en la lista)
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_contras, parent, false)
        return VistaContrasena(vista) // Se retorna el ViewHolder con la vista inflada
    }

    // Este método se llama para enlazar los datos de un item específico del adaptador con el ViewHolder
    override fun onBindViewHolder(holder: VistaContrasena, position: Int) {
        val item = contrasenas[position] // Obtenemos el objeto de la lista de contraseñas en la posición actual

        // Usamos Glide para cargar la imagen de manera eficiente (para evitar bloqueos por carga de imágenes)
        Glide.with(holder.itemView.context)
            .load(item.imgSeleccionada) // Cargar la imagen seleccionada para este item
            .into(holder.imagenIcono) // Colocamos la imagen en el ImageView correspondiente

        // Asignamos el título de la contraseña en el TextView correspondiente
        holder.textoTitulo.text = item.titulo
        // Asignamos el correo asociado a la contraseña
        holder.textoCorreo.text = item.correo
        // Asignamos la contraseña en el TextView correspondiente
        holder.textoContrasena.text = item.contra

        // Log de depuración para ver qué elementos se están cargando
        Log.d("ContrasAdapter", "Cargando imagen: ${item.imgSeleccionada} para el título: ${item.titulo} y el correo: ${item.correo}, contraseña: ${item.contra}")
    }

    // Retorna el tamaño de la lista de contraseñas
    override fun getItemCount() = contrasenas.size
}

