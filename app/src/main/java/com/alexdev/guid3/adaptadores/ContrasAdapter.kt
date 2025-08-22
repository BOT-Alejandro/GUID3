package com.alexdev.guid3.adaptadores

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.dataClasses.contras
import com.bumptech.glide.Glide
import java.io.File

class ContrasAdapter(
    private val contrasenas: MutableList<contras> // Cambiado a MutableList para permitir modificaciones
) : RecyclerView.Adapter<ContrasAdapter.VistaContrasena>() {

    private val clearHandler = Handler(Looper.getMainLooper())

    // ViewHolder que contiene las vistas de cada item del RecyclerView
    inner class VistaContrasena(view: View) : RecyclerView.ViewHolder(view) {
        val imagenIcono: ImageView = view.findViewById(R.id.img_de_contrasena)
        val textoTitulo: TextView = view.findViewById(R.id.txtViewTituloContrasena)
        val textoCorreo: TextView = view.findViewById(R.id.textViewCorreo)
        val textoContrasena: TextView = view.findViewById(R.id.textViewContrasena)
        val btnMostrarOcultar: ImageButton = view.findViewById(R.id.btnMostrarOcultar)
        val btnCopiar: ImageButton = view.findViewById(R.id.btnCopiar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaContrasena {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_contras, parent, false)
        return VistaContrasena(vista)
    }

    override fun onBindViewHolder(holder: VistaContrasena, position: Int) {
        val item = contrasenas[position]
        val iconoUri = item.iconoPersonalizado
        val recursoDrawable = if (item.imgSeleccionada > 0) item.imgSeleccionada else R.drawable._logonotfound
        if (iconoUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(recursoDrawable)
                .into(holder.imagenIcono)
        } else if (iconoUri.startsWith("http")) {
            Glide.with(holder.itemView.context)
                .load(iconoUri)
                .error(recursoDrawable)
                .into(holder.imagenIcono)
        } else {
            val file = File(iconoUri.toUri().path ?: "")
            if (file.exists()) {
                Glide.with(holder.itemView.context)
                    .load(file)
                    .into(holder.imagenIcono)
            } else {
                Glide.with(holder.itemView.context)
                    .load(recursoDrawable)
                    .into(holder.imagenIcono)
            }
        }
        holder.textoTitulo.text = item.titulo
        holder.textoCorreo.text = item.correo
        holder.textoContrasena.text = item.contra

        // Funcion para mostrar/ocultar el texto con un efecto de desenfoque a los textviews de correo y contraseña
        val aplicarBlur: (TextView) -> Unit = { textView ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val blur = RenderEffect.createBlurEffect(15f, 15f, Shader.TileMode.CLAMP)
                textView.setRenderEffect(blur)
            } else {
                textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                textView.paint.maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
            }
        }

        val quitarBlur: (TextView) -> Unit = { textView ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                textView.setRenderEffect(null)
            } else {
                textView.paint.maskFilter = null
            }
        }

        if (item.esVisible) {
            quitarBlur(holder.textoCorreo)
            quitarBlur(holder.textoContrasena)
            holder.btnMostrarOcultar.setImageResource(R.drawable.mostrar_icono)
        } else {
            aplicarBlur(holder.textoCorreo)
            aplicarBlur(holder.textoContrasena)
            holder.btnMostrarOcultar.setImageResource(R.drawable.ocultar_icono)
        }

        holder.btnMostrarOcultar.setOnClickListener {
            item.esVisible = !item.esVisible
            notifyItemChanged(position)
        }

        holder.btnCopiar.setOnClickListener { v ->
            val ctx = v.context
            val plain = item.contra
            if (plain.isNullOrEmpty()) {
                Toast.makeText(ctx, "Sin contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("password", plain)
            cm.setPrimaryClip(clip)
            Toast.makeText(ctx, "Contraseña copiada (se eliminará en breve)", Toast.LENGTH_SHORT).show()
            val snapshot = plain
            clearHandler.postDelayed({
                val current = cm.primaryClip
                val currentText = if (current != null && current.itemCount > 0) current.getItemAt(0).coerceToText(ctx).toString() else null
                if (currentText == snapshot) {
                    val emptyClip = ClipData.newPlainText("", "")
                    cm.setPrimaryClip(emptyClip)
                    Toast.makeText(ctx, "Portapapeles limpiado", Toast.LENGTH_SHORT).show()
                }
            }, 30_000)
        }
    }

    override fun getItemCount() = contrasenas.size

    fun agregarContras(contras: contras) {
        contrasenas.add(contras)
        notifyItemInserted(contrasenas.size - 1)
    }
}
