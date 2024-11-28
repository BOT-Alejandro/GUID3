package com.alexdev.guid3.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.dataClasses.categorias

class CategoriaAdapter(
    private val categorias: MutableList<categorias>,
    private val alSeleccionarCategoria: (String) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.VistaCategoria>() {

    inner class VistaCategoria(view: View) : RecyclerView.ViewHolder(view) {
        val textoCategoria: TextView = view.findViewById(R.id.txtView_Categoria)

        init {
            view.setOnClickListener {
                alSeleccionarCategoria(categorias[adapterPosition].categoria) // Selecciona el campo 'categoria'
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaCategoria {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return VistaCategoria(vista)
    }

    override fun onBindViewHolder(holder: VistaCategoria, position: Int) {
        // Usa el campo 'categoria' para asignar el texto al TextView
        holder.textoCategoria.text = categorias[position].categoria
    }

    override fun getItemCount() = categorias.size
}

