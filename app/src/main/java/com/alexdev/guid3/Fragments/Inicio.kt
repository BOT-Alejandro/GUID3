package com.alexdev.guid3.Fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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
import com.alexdev.guid3.R
import com.alexdev.guid3.adaptadores.CategoriaAdapter
import com.alexdev.guid3.adaptadores.ContrasAdapter
import com.alexdev.guid3.dataClasses.categorias
import com.alexdev.guid3.dataClasses.contras
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.alexdev.guid3.viewModels.CategoriaViewModel
import androidx.core.view.isVisible

class Inicio : Fragment(R.layout.fragment_inicio) {
    private lateinit var categoriaViewModel: CategoriaViewModel

    //Lista de contraseñas generadas internamente para pruebas
    private val contras = mutableListOf(
        contras(R.drawable._logofacebook, "Facebook", "hectoralejandro037@gmail.com", "JKGB_+53r"),
        contras(R.drawable._logoinstragram, "Instagram", "hectorzendejas069@gmail.com", "@#dfsf@1"),
        contras(R.drawable._logobbva, "BBVA", "hectoralejandro037@gmail.com", "DSF#$%d452"),
        contras(R.drawable._logooutlook, "Outlook", "hectoralejandro037@gmail.com", "SD#s@#%HH_"),
        contras(R.drawable._logogmail, "Correo Gmail", "hectoralejandro037@gmail.com", "fjn#$%fx&"),
        contras(R.drawable._logox, "Cuenta X: SoyAlex", "hectoralejandro037@gmail.com", "AKC@4Ddfa"),
        contras(R.drawable._logoyoutube, "Cuenta Youtube: Alejandroo", "hectoralejandro23@outlook.com", "%^#FFFBd"),
        contras(R.drawable._logonetflix, "Netflix: Alejandro", "hectoralejandro037@gmail.com", "#@%DFEUhmkf"),
        contras(R.drawable._logomercadopago, "Mercado Pago", "hectoralejandro037@gmail.com", "@#%BGfjgdfb"),
        contras(R.drawable._logoprime, "Cuenta Prime: Alejandro", "hectoralejandro037@gmail.com", "ASFjh3^^%"),
        contras(R.drawable._logoamazon, "Cuenta Amazon: Alejandro", "hectoralejandro037@gmail.com", "ApU_@4Ddfa"),
        contras(R.drawable._logomercadolibre, "Cuenta Mercado Libre: Alejandro", "hectoralejandro037@gmail.com", "&(_aslASmz"),
        contras(R.drawable._logoebay, "Cuenta Ebay: Alejandro", "hectoralejandro037@gmail.com", "@347Dgry845_"),
        contras(R.drawable._logopaypal, "Cuenta Paypal: Alejandro", "hectoralejandro037@gmail.com", "aslf!@vAFL"),
        contras(R.drawable._logoicloud, "Cuenta iCloud: Alejandro Martinez", "hectoralejandro037@gmail.com", "adSFIL3%&gdw"),
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
        val adaptadorContras = ContrasAdapter(contras)
        val recyclerViewContras = view.findViewById<RecyclerView>(R.id.recyclerViewContras)
        recyclerViewContras.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewContras.adapter = adaptadorContras
        txtConteoContra.text = adaptadorContras.itemCount.toString()



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
        if (position < 0 || position >= contras.size) return
        val contra = contras[position]
        val popUp = PopUpEditarContra.newInstance(
            contra.imgSeleccionada,
            contra.titulo,
            contra.correo,
            contra.contra,
            object : PopUpEditarContra.OnContraEditadaListener {
                override fun onContraEditada(
                    imgSeleccionada: Int,
                    iconoPersonalizado: String?,
                    titulo: String,
                    correo: String,
                    contraStr: String
                ) {
                    contra.imgSeleccionada = imgSeleccionada
                    contra.titulo = titulo
                    contra.correo = correo
                    contra.contra = contraStr
                    view?.findViewById<RecyclerView>(R.id.recyclerViewContras)?.adapter?.notifyItemChanged(position)
                    Toast.makeText(requireContext(), getString(R.string.contra_editada), Toast.LENGTH_SHORT).show()
                }
            }
        )
        popUp.show(parentFragmentManager, "PopUpEditarContra")
    }

    private fun mostrarDialogoEliminar(position: Int) {
        if (position < 0 || position >= contras.size) return
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
        if (position < 0 || position >= contras.size) return
        contras.removeAt(position)
        view?.findViewById<RecyclerView>(R.id.recyclerViewContras)?.adapter?.notifyItemRemoved(position)
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
        // Abrir el fragment del pop up
        val fragmentPopUp = PopUpCrearContra()
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
