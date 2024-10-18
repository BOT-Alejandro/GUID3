package com.alexdev.guid3.Fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alexdev.guid3.R
import com.alexdev.guid3.Ventanas_al_Iniciar.VentanaKNav_View


class Ajustes : Fragment(R.layout.fragment_ajustes) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Acceder al Toolbar desde el Activity
        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)


        // Configura el título del Toolbar
        toolbar?.title = "Configuración"
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Acceder al Toolbar desde el Activity
        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)

        // Restaurar el título original del Toolbar
        toolbar?.title = "GUID3 Technologies"

    }

}