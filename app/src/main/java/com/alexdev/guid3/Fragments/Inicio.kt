package com.alexdev.guid3.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexdev.guid3.R
import com.alexdev.guid3.adaptadores.ContrasAdapter
import com.alexdev.guid3.dataClasses.contras

private val contras = mutableListOf(
    contras(R.drawable.logo_facebook, "Facebook", "hectoralejandro037@gmail.com", "Alex1104"),
    contras(R.drawable.logo_instagram, "Instagram", "hectorzendejas069@gmail.com", "Alex271202"),
    contras(R.drawable.logo_bbva, "BBVA", "hectoralejandro037@gmail.com", "Alex1104"),
    contras(R.drawable.logo_outlook, "Outlook", "hectoralejandro037@gmail.com", "Alex1104"),
    contras(R.drawable.logo_gmail, "Gmail", "hectoralejandro037@gmail.com", "Alex1104"),
    contras(R.drawable.logo_youtube, "Youtube", "hectoralejandro037@gmail.com", "Alex1104"),
    contras(R.drawable.logo_x, "Twitter", "hectoralejandro037@gmail.com", "Alex1104"),

)

class Inicio : Fragment(R.layout.fragment_inicio) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar =
            (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = "GUID3"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Configurar el RecyclerView directamente
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContras)
        val adaptadorContras = ContrasAdapter(contras)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adaptadorContras

        return view
    }

}


