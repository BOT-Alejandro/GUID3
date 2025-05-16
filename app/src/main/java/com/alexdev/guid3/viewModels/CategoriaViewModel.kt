package com.alexdev.guid3.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexdev.guid3.dataClasses.categorias

class CategoriaViewModel : ViewModel() {
    // MutableLiveData que contiene la lista mutable de categorías
    private val _categorias = MutableLiveData<MutableList<categorias>>(mutableListOf())
    val categorias: LiveData<MutableList<categorias>> get() = _categorias

    // Método para agregar una nueva categoría a la lista
    fun agregarCategoria(nuevaCategoria: categorias) {
        val listaActualizada = _categorias.value ?: mutableListOf()
        listaActualizada.add(nuevaCategoria)
        _categorias.value = listaActualizada  // Actualizamos el LiveData con la lista modificada
    }
}

