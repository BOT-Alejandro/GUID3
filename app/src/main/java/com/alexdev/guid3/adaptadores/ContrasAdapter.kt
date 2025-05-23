package com.alexdev.guid3.adaptadores

import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.BlurMaskFilter
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.dataClasses.contras
import com.bumptech.glide.Glide

class ContrasAdapter(
    private val contrasenas: MutableList<contras> // Cambiado a MutableList para permitir modificaciones
) : RecyclerView.Adapter<ContrasAdapter.VistaContrasena>() {

    // ViewHolder que contiene las vistas de cada item del RecyclerView
    inner class VistaContrasena(view: View) : RecyclerView.ViewHolder(view) {
        val imagenIcono: ImageView = view.findViewById(R.id.img_de_contrasena)
        val textoTitulo: TextView = view.findViewById(R.id.txtViewTituloContrasena)
        val textoCorreo: TextView = view.findViewById(R.id.textViewCorreo)
        val textoContrasena: TextView = view.findViewById(R.id.textViewContrasena)
        val btnMostrarOcultar: ImageButton = view.findViewById(R.id.btnMostrarOcultar)
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

        // Funcion para mostrar/ocultar el texto con un efecto de desenfoque a los textviews de correo y contraseña
        val aplicarBlur: (TextView) -> Unit = { textView ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val blur = RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP)
                textView.setRenderEffect(blur)
            } else {
                textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                textView.paint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
            }
        }

        val quitarBlur: (TextView) -> Unit = { textView ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                textView.setRenderEffect(null)
            } else {
                textView.paint.maskFilter = null
            }
        }

        // Aplica o quita el efecto según el estado actual
        if (item.esVisible) {
            quitarBlur(holder.textoCorreo)
            quitarBlur(holder.textoContrasena)
            holder.btnMostrarOcultar.setImageResource(R.drawable.mostrar_icono) // ícono de "visible"
        } else {
            aplicarBlur(holder.textoCorreo)
            aplicarBlur(holder.textoContrasena)
            holder.btnMostrarOcultar.setImageResource(R.drawable.ocultar_icono) // ícono de "oculto"
        }

        holder.btnMostrarOcultar.setOnClickListener {
            item.esVisible = !item.esVisible
            notifyItemChanged(position)
        }

    }

    override fun getItemCount() = contrasenas.size

    // Metodo para agregar un nuevo item a la lista
    fun agregarContrasena(nuevaContrasena: contras) {
        contrasenas.add(nuevaContrasena)
        notifyItemInserted(contrasenas.size - 1) // Notificar al adaptador del cambio
    }
}


