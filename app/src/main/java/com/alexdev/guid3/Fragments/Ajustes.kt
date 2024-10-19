package com.alexdev.guid3.Fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alexdev.guid3.R
import com.alexdev.guid3.Ventanas_al_Iniciar.VentanaKNav_View
import com.google.android.material.switchmaterial.SwitchMaterial


class Ajustes : Fragment(R.layout.fragment_ajustes) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botonModoNoche = view.findViewById<SwitchMaterial>(R.id.Switch_modo_noche)
        val botonIdioma = view.findViewById<SwitchMaterial>(R.id.Switch_cambiar_idioma)
        val botonExportar = view.findViewById<ImageButton>(R.id.boton_exportar)


        //Logica para exportar contraseñas (PENDIENTE)
        botonExportar.setOnClickListener {

            val toast = Toast.makeText(requireContext(), "Exportando contraseñas...", Toast.LENGTH_SHORT)
            toast.show()

        }

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