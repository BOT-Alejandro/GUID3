package com.alexdev.guid3.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alexdev.guid3.R


class Acerca_de : Fragment(R.layout.fragment_acerca_de) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = "Acerca de"



    }
}