package com.alexdev.guid3.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IconSelectorBottomSheet : BottomSheetDialogFragment() {
    interface OnIconSelectedListener {
        fun onIconSelected(iconRes: Int)
        fun onCustomImageRequested()
    }

    var listener: OnIconSelectedListener? = null

    private val iconos = arrayOf(
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.icon_selector_bottom_sheet, container, false)
        val recyclerIconos = view.findViewById<RecyclerView>(R.id.recyclerIconos)
        val btnSubirImagen = view.findViewById<Button>(R.id.btnSubirImagen)
        val txtTitulo = view.findViewById<TextView>(R.id.txtTituloSelector)

        recyclerIconos.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerIconos.adapter = IconosAdapter(iconos) { iconRes ->
            listener?.onIconSelected(iconRes)
            dismiss()
        }

        btnSubirImagen.setOnClickListener {
            listener?.onCustomImageRequested()
            dismiss()
        }

        return view
    }

    class IconosAdapter(
        private val iconos: Array<Int>,
        private val onClick: (Int) -> Unit
    ) : RecyclerView.Adapter<IconosAdapter.IconViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon_preview, parent, false)
            return IconViewHolder(view)
        }
        override fun getItemCount() = iconos.size
        override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
            holder.bind(iconos[position], onClick)
        }
        class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(iconRes: Int, onClick: (Int) -> Unit) {
                val imgIcon = itemView.findViewById<android.widget.ImageView>(R.id.imgIconPreview)
                imgIcon.setImageResource(iconRes)
                itemView.setOnClickListener { onClick(iconRes) }
            }
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

