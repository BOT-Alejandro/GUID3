package com.alexdev.guid3.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.adaptadores.CategoriaAdapter
import com.alexdev.guid3.adaptadores.ContrasAdapter
import com.alexdev.guid3.dataClasses.categorias
import com.alexdev.guid3.dataClasses.contras
import com.alexdev.guid3.viewModels.CategoriaViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import android.view.animation.Animation
import android.view.animation.AnimationUtils

class Inicio : Fragment(R.layout.fragment_inicio) {
    private lateinit var categoriaViewModel: CategoriaViewModel

    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()
    private val contrasCollection = db.collection("contras")
    private var listaContras = mutableListOf<contras>()
    private var contrasListener: ListenerRegistration? = null

    //Lista de categorias de contraseñas
    private val categorias = mutableListOf<categorias>()

    //Animaciones
    private val rotarAbrir: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotar_al_abrir_anim) }
    private val rotarCerrar: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotar_al_cerrar_anim) }
    private val desdeAbajo: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.desde_abajo_anim) }
    private val haciaAbajo: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.hacia_abajo_anim) }
    private val haciaIzquierda: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.hacia_izquierda) }
    private val haciaDerecha: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.hacia_derecha) }

    private val TAG = "InicioFragment"

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Inicializando Firestore y UI")

        // ViewModel para que las categorías se actualicen conforme se cambie el modo oscuro de la aplicación
        categoriaViewModel = ViewModelProvider(this)[CategoriaViewModel::class.java]


        val btnAbrirOpciones = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAbrirOpciones)
        val btnAddContras = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddContras)
        val btnAddCategorias = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAddCategorias)
        val txtAddCategoria = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.txtAddCategoria)
        val txtAddContra = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.txtAddContra)

        // Configurar la barra de herramientas
        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = "GUID3"

        // Configurar el RecyclerView de la lista de contraseñas
        val txtConteoContra = view.findViewById<TextView>(R.id.txt_conteo_contra)
        val adaptadorContras = ContrasAdapter(listaContras)
        val recyclerViewContras = view.findViewById<RecyclerView>(R.id.recyclerViewContras)
        recyclerViewContras.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewContras.adapter = adaptadorContras
        txtConteoContra.text = adaptadorContras.itemCount.toString()

        // Escuchar cambios en Firestore y actualizar la lista
        contrasListener = contrasCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error al cargar contraseñas desde Firestore", error)
                Toast.makeText(requireContext(), "Error al cargar contraseñas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (snapshot != null) {
                Log.d(TAG, "Snapshot recibido: ${snapshot.documents.size} documentos")
                listaContras.clear()
                for (doc in snapshot.documents) {
                    val contra = doc.toObject(contras::class.java)
                    if (contra != null) listaContras.add(contra)
                }
                adaptadorContras.notifyDataSetChanged()
                txtConteoContra.text = listaContras.size.toString()
            } else {
                Log.w(TAG, "Snapshot nulo en listener de contras")
            }
        }



        // Actualizar el conteo en tiempo real
        adaptadorContras.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                txtConteoContra.text = adaptadorContras.itemCount.toString()
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                txtConteoContra.text = adaptadorContras.itemCount.toString()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                txtConteoContra.text = adaptadorContras.itemCount.toString()
            }
        })

        // Configurar el RecyclerView con el ViewModel y la lista de Categorias
        val recyclerViewCategoria = view.findViewById<RecyclerView>(R.id.recyclerViewCategorias)
        val adaptadorCategorias = CategoriaAdapter(mutableListOf())  // Adaptador de Categorías
        recyclerViewCategoria.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategoria.adapter = adaptadorCategorias

        // Observar cambios en las categorías del ViewModel
        categoriaViewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            adaptadorCategorias.setListaCategorias(categorias)  // Actualizar el adaptador con la nueva lista
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
            if (btnAddContras.isVisible) {
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

        configurarBotonesAgregar(
            listOf(
                view.findViewById(R.id.btnAddContras),
                view.findViewById(R.id.txtAddContra)
            ),
            ::alPresionarBotonContras
        )
        configurarBotonesAgregar(
            listOf(
                view.findViewById(R.id.btnAddCategorias),
                view.findViewById(R.id.txtAddCategoria)
            ),
            ::alPresionarBotonCategorias
        )
        configurarDeslizarRecyclerView(recyclerViewContras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        contrasListener?.remove()
    }

    // Función auxiliar para configurar listeners en varios botones
    private fun configurarBotonesAgregar(botones: List<View>, accion: () -> Unit) {
        botones.forEach { boton ->
            boton.setOnClickListener { accion() }
        }
    }

    private fun configurarDeslizarRecyclerView(recyclerViewContras: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        mostrarDialogoEditar(position)
                    }
                    ItemTouchHelper.LEFT -> {
                        mostrarDialogoEliminar(position)
                    }
                }
                recyclerViewContras.adapter?.notifyItemChanged(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable()

                val icon: Drawable?
                val iconMargin = (itemView.height - 100) / 2 // Margen del icono

                if (dX > 0) { // Deslizar a la derecha
                    background.color = Color.BLUE
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.editar_elemento_icono)
                    icon?.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + 100,
                        itemView.bottom - iconMargin
                    )
                } else { // Deslizar a la izquierda
                    background.color = Color.RED
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.eliminar_elemento_icono)
                    icon?.setBounds(
                        itemView.right - iconMargin - 100,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }

                background.draw(c)
                icon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        // Asignar ItemTouchHelper al RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewContras)
    }

    // Diálogo para editar elemento
    private fun mostrarDialogoEditar(position: Int) {
        val adaptadorContras = ContrasAdapter(listaContras)
        if (position < 0 || position >= listaContras.size) return
        val contra = listaContras[position]
        Log.d(TAG, "Editar contraseña: $contra")
        val popUp = PopUpEditarContra.newInstance(
            contra.imgSeleccionada,
            contra.titulo,
            contra.correo,
            contra.contra,
            object : PopUpEditarContra.OnContraEditadaListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onContraEditada(
                    imgSeleccionada: Int,
                    iconoPersonalizado: String?,
                    titulo: String,
                    correo: String,
                    contraStr: String
                ) {
                    Log.d(TAG, "onContraEditada: imgSeleccionada=$imgSeleccionada, iconoPersonalizado=$iconoPersonalizado, titulo=$titulo, correo=$correo, contra=$contraStr")
                    Log.d(TAG, "Actualizando contraseña en Firestore: $titulo, $correo")
                    val docQuery = contrasCollection
                        .whereEqualTo("titulo", contra.titulo)
                        .whereEqualTo("correo", contra.correo)
                        .whereEqualTo("contra", contra.contra)
                        .get()
                    docQuery.addOnSuccessListener { docs ->
                        Log.d(TAG, "Documentos encontrados para editar: ${docs.size()} documentos")
                        for (doc in docs) {
                            doc.reference.update(
                                mapOf(
                                    "imgSeleccionada" to imgSeleccionada,
                                    "iconoPersonalizado" to iconoPersonalizado,
                                    "titulo" to titulo,
                                    "correo" to correo,
                                    "contra" to contraStr
                                )
                            ).addOnSuccessListener {
                                Log.d(TAG, "Contraseña actualizada correctamente")
                                // Actualizar la lista local y refrescar el adapter
                                contra.imgSeleccionada = imgSeleccionada
                                contra.iconoPersonalizado = iconoPersonalizado
                                contra.titulo = titulo
                                contra.correo = correo
                                contra.contra = contraStr
                                adaptadorContras.notifyDataSetChanged()
                            }.addOnFailureListener { e ->
                                Log.e(TAG, "Error al actualizar contraseña", e)
                            }
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error al buscar documentos para editar", e)
                    }
                    Toast.makeText(requireContext(), getString(R.string.contra_editada), Toast.LENGTH_SHORT).show()
                }
            }
        )
        popUp.show(parentFragmentManager, "PopUpEditarContra")
    }

    private fun mostrarDialogoEliminar(position: Int) {
        if (position < 0 || position >= listaContras.size) return
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.eliminar))
            .setMessage(getString(R.string.confirmar_eliminar_contra))
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                eliminarElemento(position)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun eliminarElemento(position: Int) {
        if (position < 0 || position >= listaContras.size) return
        val contra = listaContras[position]
        Log.d(TAG, "Eliminando contraseña: $contra")
        // TODO: Eliminar de Firestore
        val docQuery = contrasCollection
            .whereEqualTo("titulo", contra.titulo)
            .whereEqualTo("correo", contra.correo)
            .whereEqualTo("contra", contra.contra)
            .get()
        docQuery.addOnSuccessListener { docs ->
            Log.d(TAG, "Documentos encontrados para eliminar: ${docs.size()} documentos")
            for (doc in docs) {
                doc.reference.delete()
                    .addOnSuccessListener { Log.d(TAG, "Contraseña eliminada correctamente") }
                    .addOnFailureListener { e -> Log.e(TAG, "Error al eliminar contraseña", e) }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error al buscar documentos para eliminar", e)
        }
        Toast.makeText(requireContext(), getString(R.string.contra_eliminada), Toast.LENGTH_SHORT).show()
    }

    // Función para agregar una nueva categoría
    private fun alPresionarBotonCategorias(){
        // Inflar el diseño del pop-up
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.pop_up_crear_categoria, null)

        // Crear el dialogo
        val dialog = AlertDialog.Builder(requireContext())
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
        Log.d(TAG, "Abriendo PopUpCrearContra para nueva contraseña")
        val fragmentPopUp = PopUpCrearContra()
        fragmentPopUp.listener = object : PopUpCrearContra.OnContraCreadaListener {
            override fun onContraCreada(imgSeleccionada: Int, iconoPersonalizado: String?, titulo: String, correo: String, contra: String) {
                Log.d(TAG, "Creando nueva contraseña: $titulo, $correo")
                val nuevaContra = contras(
                    imgSeleccionada = imgSeleccionada,
                    titulo = titulo,
                    correo = correo,
                    contra = contra,
                    iconoPersonalizado = iconoPersonalizado
                )
                contrasCollection.add(nuevaContra)
                    .addOnSuccessListener { Log.d(TAG, "Contraseña guardada en Firestore correctamente") }
                    .addOnFailureListener { e -> Log.e(TAG, "Error al guardar contraseña en Firestore", e) }
                Toast.makeText(requireContext(), "Contraseña creada", Toast.LENGTH_SHORT).show()
            }
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmento_contenedor, fragmentPopUp)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun hideRecyclerViewWithAnimation(recyclerView: RecyclerView) {
        val animator = ObjectAnimator.ofFloat(recyclerView, "translationY", 0f, -recyclerView.height.toFloat())
        animator.duration = 200 // Duración de la animación en milisegundos
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
        animator.duration = 200 // Duración de la animación en milisegundos
        animator.start()

    }



}
