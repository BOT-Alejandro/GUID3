package com.alexdev.guid3.Fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alexdev.guid3.R


class Acerca_de : Fragment(R.layout.fragment_acerca_de) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtVersion = view.findViewById<TextView>(R.id.texto_version_app)
        val versionName = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        txtVersion.text = versionName
        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        toolbar?.title = "Acerca de"
    }


}