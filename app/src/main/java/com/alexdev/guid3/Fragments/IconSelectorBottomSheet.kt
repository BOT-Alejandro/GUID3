package com.alexdev.guid3.Fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.alexdev.guid3.util.CustomIconRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore

class IconSelectorBottomSheet : BottomSheetDialogFragment() {
    interface OnIconSelectedListener {
        fun onIconSelected(iconRes: Int)
        fun onRemoteIconSelected(url: String)
        fun onCustomImageRequested()
        fun onIconsDeleted(urls: List<String>) {}
    }

    var listener: OnIconSelectedListener? = null

    private val iconosBase = arrayOf(
        R.drawable._logogmail,
        R.drawable._logofacebook,
        R.drawable._logoinstragram,
        R.drawable._logobbva,
        R.drawable._logooutlook,
        R.drawable._logoyoutube,
        R.drawable._logonetflix,
        R.drawable._logomercadopago,
        R.drawable._logoprime,
        R.drawable._logoamazon,
        R.drawable._logomercadolibre,
        R.drawable._logoebay,
        R.drawable._logopaypal,
        R.drawable._logoicloud
    )

    private lateinit var recyclerBase: RecyclerView
    private lateinit var recyclerPersonalizados: RecyclerView
    private lateinit var sectionPersonalizados: View
    private lateinit var txtSeleccionInstr: TextView
    private lateinit var btnEliminarSeleccionados: Button
    private lateinit var btnSubir: Button

    private var seleccionActiva = false
    private val seleccionados = mutableSetOf<String>()

    private lateinit var adapterBase: IconosBaseAdapter
    private lateinit var adapterPersonalizados: IconosPersonalizadosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.icon_selector_bottom_sheet, container, false)
        recyclerBase = view.findViewById(R.id.recyclerIconosBase)
        recyclerPersonalizados = view.findViewById(R.id.recyclerIconosPersonalizados)
        sectionPersonalizados = view.findViewById(R.id.sectionPersonalizados)
        txtSeleccionInstr = view.findViewById(R.id.txtSeleccionInstruccion)
        btnEliminarSeleccionados = view.findViewById(R.id.btnEliminarSeleccionados)
        btnSubir = view.findViewById(R.id.btnSubirImagen)

        recyclerBase.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerPersonalizados.layoutManager = GridLayoutManager(requireContext(), 4)

        adapterBase = IconosBaseAdapter(iconosBase.toList()) { resId ->
            listener?.onIconSelected(resId)
            dismiss()
        }
        recyclerBase.adapter = adapterBase

        cargarPersonalizados()

        btnSubir.setOnClickListener {
            listener?.onCustomImageRequested()
            dismiss()
        }

        btnEliminarSeleccionados.setOnClickListener {
            if (seleccionados.isNotEmpty()) {
                val urlsAEliminar = seleccionados.toList()
                eliminarIconosPersonalizados(urlsAEliminar)
            }
        }
        return view
    }

    private fun cargarPersonalizados() {
        val urls = CustomIconRepository.getCustomIcons(requireContext())
        if (urls.isEmpty()) {
            sectionPersonalizados.visibility = View.GONE
            return
        }
        sectionPersonalizados.visibility = View.VISIBLE
        adapterPersonalizados = IconosPersonalizadosAdapter(urls,
            getEstaSeleccionado = { seleccionados.contains(it) },
            onClick = { url ->
                if (seleccionActiva) {
                    alternarSeleccion(url)
                } else {
                    listener?.onRemoteIconSelected(url)
                    dismiss()
                }
            },
            onLongClick = { url ->
                if (!seleccionActiva) entrarModoSeleccion(url) else alternarSeleccion(url)
            })
        recyclerPersonalizados.adapter = adapterPersonalizados
    }

    private fun entrarModoSeleccion(urlInicial: String) {
        seleccionActiva = true
        seleccionados.clear()
        seleccionados.add(urlInicial)
        txtSeleccionInstr.visibility = View.VISIBLE
        btnEliminarSeleccionados.visibility = View.VISIBLE
        actualizarEstadoSeleccionUI()
    }

    private fun alternarSeleccion(url: String) {
        if (seleccionados.contains(url)) seleccionados.remove(url) else seleccionados.add(url)
        if (seleccionados.isEmpty()) {
            salirModoSeleccion()
        } else {
            actualizarEstadoSeleccionUI()
        }
    }

    private fun salirModoSeleccion() {
        seleccionActiva = false
        seleccionados.clear()
        txtSeleccionInstr.visibility = View.GONE
        btnEliminarSeleccionados.visibility = View.GONE
        actualizarEstadoSeleccionUI()
    }

    private fun actualizarEstadoSeleccionUI() {
        adapterPersonalizados.notifyDataSetChanged()
        btnEliminarSeleccionados.text = if (seleccionados.isEmpty()) "Eliminar seleccionados" else "Eliminar (${seleccionados.size})"
    }

    private fun eliminarIconosPersonalizados(urls: List<String>) {
        CustomIconRepository.removeCustomIcons(requireContext(), urls)
        val storage = FirebaseStorage.getInstance()
        urls.forEach { url ->
            try {
                val ref = storage.getReferenceFromUrl(url)
                ref.delete().addOnSuccessListener {  }
                    .addOnFailureListener {  }
            } catch (_: Exception) { }
        }
        val db = FirebaseFirestore.getInstance()
        urls.forEach { url ->
            db.collection("contras").whereEqualTo("iconoPersonalizado", url).get()
                .addOnSuccessListener { snap ->
                    for (doc in snap.documents) {
                        doc.reference.update("iconoPersonalizado", null)
                    }
                }
        }
        seleccionados.clear()
        salirModoSeleccion()
        cargarPersonalizados()
        listener?.onIconsDeleted(urls)
    }

    private class IconosBaseAdapter(
        private val recursos: List<Int>,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.Adapter<IconosBaseAdapter.ResHolder>() {
        inner class ResHolder(v: View) : RecyclerView.ViewHolder(v) {
            val img: ImageView = v.findViewById(R.id.imgIconPreview)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_icon_preview, parent, false)
            return ResHolder(v)
        }
        override fun getItemCount() = recursos.size
        override fun onBindViewHolder(holder: ResHolder, position: Int) {
            val res = recursos[position]
            holder.img.setImageResource(res)
            holder.itemView.setOnClickListener { onClick(res) }
        }
    }

    private class IconosPersonalizadosAdapter(
        private val urls: List<String>,
        private val getEstaSeleccionado: (String) -> Boolean,
        private val onClick: (String) -> Unit,
        private val onLongClick: (String) -> Unit
    ) : RecyclerView.Adapter<IconosPersonalizadosAdapter.UrlHolder>() {
        inner class UrlHolder(v: View) : RecyclerView.ViewHolder(v) {
            val img: ImageView = v.findViewById(R.id.imgIconPreview)
            val overlay: View? = v.findViewById(R.id.selectionOverlay)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_icon_preview, parent, false)
            return UrlHolder(v)
        }
        override fun getItemCount() = urls.size
        override fun onBindViewHolder(holder: UrlHolder, position: Int) {
            val url = urls[position]
            Glide.with(holder.itemView.context)
                .load(url)
                .placeholder(R.drawable._logonotfound)
                .error(R.drawable._logonotfound)
                .into(holder.img)
            val seleccionado = getEstaSeleccionado(url)
            val highlightColor = ContextCompat.getColor(holder.itemView.context, R.color.MoradoOscuro)
            holder.overlay?.visibility = if (seleccionado) View.VISIBLE else View.GONE
            holder.overlay?.setBackgroundColor(highlightColor and 0x55FFFFFF)
            holder.itemView.alpha = if (seleccionado) 0.6f else 1f
            holder.itemView.setOnClickListener { onClick(url) }
            holder.itemView.setOnLongClickListener { onLongClick(url); true }
        }
    }

    companion object {
        fun newInstance(listener: OnIconSelectedListener): IconSelectorBottomSheet {
            val fragment = IconSelectorBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }
}
