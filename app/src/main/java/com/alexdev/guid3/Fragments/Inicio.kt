package com.alexdev.guid3.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import android.view.LayoutInflater
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
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.alexdev.guid3.viewModels.CategoriaViewModel

class Inicio : Fragment(R.layout.fragment_inicio) {
    private lateinit var categoriaViewModel: CategoriaViewModel


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
    private val haciaIzquierda: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.hacia_izquierda) }
    private val haciaDerecha: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.hacia_derecha) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel para que las categorías se actualicen conforme se cambie el modo oscuro de la aplicación
        categoriaViewModel = ViewModelProvider(this).get(CategoriaViewModel::class.java)

        val btnAbrirOpciones = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAbrirOpciones)
        val btnAddContras = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddContras)
        val btnAddCategorias = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddCategorias)
        val txtAddCategoria = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.txtAddCategoria)
        val txtAddContra = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.txtAddContra)

        // Configurar la barra de herramientas
        val toolbar =
            (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = "GUID3"

        // Configurar el RecyclerView de la lista de contraseñas
        val recyclerViewContras = view.findViewById<RecyclerView>(R.id.recyclerViewContras)
        val adaptadorContras = ContrasAdapter(contras)
        recyclerViewContras.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewContras.adapter = adaptadorContras

        // Configurar el RecyclerView con el ViewModel y la lista de Categorias
        val recyclerViewCategoria = view.findViewById<RecyclerView>(R.id.recyclerViewCategorias)
        val adaptadorCategorias = CategoriaAdapter(mutableListOf())  // Adaptador de Categorías
        recyclerViewCategoria.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategoria.adapter = adaptadorCategorias

        // Observar cambios en las categorías del ViewModel
        categoriaViewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            adaptadorCategorias.actualizarLista(categorias)  // Actualizar el adaptador con la nueva lista
        }

        // Agregar ScrollListener al RecyclerView de contraseñas
        recyclerViewContras.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // desplazamiento hacia abajo
                    // Animación para ocultar el RecyclerView de categorías
                    hideRecyclerViewWithAnimation(recyclerViewCategoria)
                } else if (dy < 0) { // desplazamiento hacia arriba
                    // Animación para mostrar el RecyclerView de categorías
                    showRecyclerViewWithAnimation(recyclerViewCategoria)
                }
            }
        })

        // Configurar el botón para abrir y cerrar las opciones
        btnAbrirOpciones.setOnClickListener {
            // Si los botones ya están visibles, los ocultamos con animación
            if (btnAddContras.visibility == View.VISIBLE) {
                // Animación de cierre
                btnAddContras.startAnimation(haciaAbajo)
                btnAddCategorias.startAnimation(haciaAbajo)
                txtAddContra.startAnimation(haciaDerecha)
                txtAddCategoria.startAnimation(haciaDerecha)
                txtAddContra.visibility = View.INVISIBLE
                txtAddCategoria.visibility = View.INVISIBLE
                btnAddContras.visibility = View.INVISIBLE
                btnAddCategorias.visibility = View.INVISIBLE

                // Deshabilitar los botones para que no sean clickeables
                btnAddContras.isEnabled = false
                btnAddCategorias.isEnabled = false
                txtAddContra.isEnabled = false
                txtAddCategoria.isEnabled = false

                // Animación de rotación para el FAB principal
                btnAbrirOpciones.startAnimation(rotarCerrar)
            } else {
                // Si los botones están ocultos, los mostramos con animación
                btnAddContras.startAnimation(desdeAbajo)
                btnAddCategorias.startAnimation(desdeAbajo)
                txtAddContra.startAnimation(haciaIzquierda)
                txtAddCategoria.startAnimation(haciaIzquierda)
                txtAddContra.visibility = View.VISIBLE
                txtAddCategoria.visibility = View.VISIBLE
                btnAddContras.visibility = View.VISIBLE
                btnAddCategorias.visibility = View.VISIBLE

                // Habilitar los botones para que sean clickeables
                btnAddContras.isEnabled = true
                btnAddCategorias.isEnabled = true
                txtAddContra.isEnabled = true
                txtAddCategoria.isEnabled = true

                // Animación de rotación para el FAB principal
                btnAbrirOpciones.startAnimation(rotarAbrir)
            }
        }

        //Configurar el boton de agregar Contraseñas
        btnAddContras.setOnClickListener { alPresionarBotonContras() }

        //Configurar el boton 2 de agregar Contraseñas
        txtAddContra.setOnClickListener { alPresionarBotonContras() }

        //Configurar el boton de agregar Categorias
        btnAddCategorias.setOnClickListener { alPresionarBotonCategorias() }

        //Configurar el boton 2 de agregar Categorias
        txtAddCategoria.setOnClickListener { alPresionarBotonCategorias() }

    }

    // Función para agregar una nueva categoría
    private fun alPresionarBotonCategorias(){
        // Inflar el diseño del pop-up
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.pop_up_crear_categoria, null)

        // Crear el dialogo
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Obtener Referencias a los elementos del pop-up
        val inputNombreCategoria = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputTextCategoria)
        val btnCategoriaGuardar = dialogView.findViewById<Button>(R.id.btnCategoriaGuardar)
        val btnCategoriaCancelar = dialogView.findViewById<Button>(R.id.btnCategoriaCancelar)

        // Configurar el botón "Guardar"
        btnCategoriaGuardar.setOnClickListener {
            val nuevaCategoria = inputNombreCategoria.text.toString().trim()
            if (nuevaCategoria.isNotEmpty()) {
                // Agregar la nueva categoría a la lista y actualizar el RecyclerView
                categoriaViewModel.agregarCategoria(categorias(categoria = nuevaCategoria))
                // Notificar que se ha agregado una nueva categoría
                Toast.makeText(requireContext(), "Categoría '$nuevaCategoria' guardada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                inputNombreCategoria.error = "Éste campo no puede estar vacío"
            }
        }


        // Configurar el botón "Cancelar"
        btnCategoriaCancelar.setOnClickListener {
            dialog.dismiss()
        }

        // Mostrar el diálogo
        dialog.show()
    }


    private fun alPresionarBotonContras(){
        // Abrir el fragment del pop up
        val fragmentPopUp = PopUpCrearContra()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmento_contenedor, fragmentPopUp)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun hideRecyclerViewWithAnimation(recyclerView: RecyclerView) {
        val animator = ObjectAnimator.ofFloat(recyclerView, "translationY", 0f, -recyclerView.height.toFloat())
        animator.duration = 300 // Duración de la animación en milisegundos
        animator.start()

        // ocultar la vista después de la animación
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                recyclerView.visibility = View.GONE
            }
        })
    }

    private fun showRecyclerViewWithAnimation(recyclerView: RecyclerView) {
        // Mostrar el RecyclerView antes de la animación (en caso de que estuviera oculto)
        recyclerView.visibility = View.VISIBLE

        val animator = ObjectAnimator.ofFloat(recyclerView, "translationY", -recyclerView.height.toFloat(), 0f)
        animator.duration = 300 // Duración de la animación en milisegundos
        animator.start()
    }


}



