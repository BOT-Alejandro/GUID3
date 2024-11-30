package com.alexdev.guid3.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.dataClasses.categorias

class CategoriaAdapter(private var listaCategorias: MutableList<categorias>) :
    RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCategoria: TextView = itemView.findViewById(R.id.txtView_Categoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.nombreCategoria.text = listaCategorias[position].categoria
    }

    override fun getItemCount(): Int = listaCategorias.size

    // Método para actualizar la lista de categorías y notificar cambios
    fun actualizarLista(nuevaLista: List<categorias>) {
        listaCategorias.clear()
        listaCategorias.addAll(nuevaLista)
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }
}




