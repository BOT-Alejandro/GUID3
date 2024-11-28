package com.alexdev.guid3.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.adaptadores.CategoriaAdapter
import com.alexdev.guid3.adaptadores.ContrasAdapter
import com.alexdev.guid3.dataClasses.categorias
import com.alexdev.guid3.dataClasses.contras
import android.view.animation.Animation
import android.view.animation.AnimationUtils

class Inicio : Fragment(R.layout.fragment_inicio) {

    //Lista de contraseñas generadas internamente para pruebas
    private val contras = mutableListOf(
        contras(R.drawable.logo_facebook, "Facebook", "hectoralejandro037@gmail.com", "Alex1104"),
        contras(R.drawable.logo_instagram, "Instagram", "hectorzendejas069@gmail.com", "Alex271202"),
        contras(R.drawable.logo_bbva, "BBVA", "hectoralejandro037@gmail.com", "Alex1104"),
        contras(R.drawable.logo_outlook, "Outlook", "hectoralejandro037@gmail.com", "Alex1104"),
        contras(R.drawable.logo_gmail, "Gmail", "hectoralejandro037@gmail.com", "Alex1104"),
        contras(R.drawable.logo_youtube, "Youtube", "hectoralejandro037@gmail.com", "Alex1104"),
        contras(R.drawable.logo_x, "X", "hectoralejandro037@gmail.com", "Alex1104")
    )

    //Lista de categorias de contraseñas
    private val categorias = mutableListOf<categorias>()

    //Animaciones
    private val rotarAbrir: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotar_al_abrir_anim) }
    private val rotarCerrar: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotar_al_cerrar_anim) }
    private val desdeAbajo: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.desde_abajo_anim) }
    private val haciaAbajo: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.hacia_abajo_anim) }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAbrirOpciones = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAbrirOpciones)
        val btnAddContras = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddContras)
        val btnAddCategorias = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddCategorias)


        // Configurar la barra de herramientas
        val toolbar =
            (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = "GUID3"

        // Configurar el RecyclerView de la lista de contraseñas
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContras)
        val adaptadorContras = ContrasAdapter(contras)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adaptadorContras

        //Configurar el RecyclerView de la lista de Categorias
        val recyclerViewCategoria = view.findViewById<RecyclerView>(R.id.recyclerViewCategorias)

        // Crear el adaptador y configurar la acción al seleccionar una categoría
        val adaptadorCategorias = CategoriaAdapter(categorias) { categoriaSeleccionada ->
            // Acción cuando se selecciona una categoría
            Toast.makeText(requireContext(), "Seleccionaste: $categoriaSeleccionada", Toast.LENGTH_SHORT).show()
        }

        recyclerViewCategoria.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerViewCategoria.adapter = adaptadorCategorias

        // Configurar el botón para abrir y cerrar las opciones
        btnAbrirOpciones.setOnClickListener {
            // Si los botones ya están visibles, los ocultamos con animación
            if (btnAddContras.visibility == View.VISIBLE) {
                // Animación de cierre
                btnAddContras.startAnimation(haciaAbajo)
                btnAddCategorias.startAnimation(haciaAbajo)
                btnAddContras.visibility = View.INVISIBLE
                btnAddCategorias.visibility = View.INVISIBLE

                // Deshabilitar los botones para que no sean clickeables
                btnAddContras.isEnabled = false
                btnAddCategorias.isEnabled = false

                // Animación de rotación para el FAB principal
                btnAbrirOpciones.startAnimation(rotarCerrar)
            } else {
                // Si los botones están ocultos, los mostramos con animación
                btnAddContras.startAnimation(desdeAbajo)
                btnAddCategorias.startAnimation(desdeAbajo)
                btnAddContras.visibility = View.VISIBLE
                btnAddCategorias.visibility = View.VISIBLE

                // Habilitar los botones para que sean clickeables
                btnAddContras.isEnabled = true
                btnAddCategorias.isEnabled = true

                // Animación de rotación para el FAB principal
                btnAbrirOpciones.startAnimation(rotarAbrir)
            }
        }

        //Configurar el boton de agregar Contraseñas
        btnAddContras.setOnClickListener {
            Toast.makeText(requireContext(), "Agregar Contraseña", Toast.LENGTH_SHORT).show()
        }

        //Configurar el boton de agregar Categorias
        btnAddCategorias.setOnClickListener {
            Toast.makeText(requireContext(), "Agregar Categoria", Toast.LENGTH_SHORT).show()
        }

    }


}



