package com.alexdev.guid3.Fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.alexdev.guid3.R
import com.bumptech.glide.Glide

class PopUpCrearContra : Fragment(R.layout.pop_up_crear_contra) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val presetFacebook = view.findViewById<ImageView>(R.id.presetFacebook)
        val presetGmail = view.findViewById<ImageView>(R.id.presetGmail)
        val presetYoutube = view.findViewById<ImageView>(R.id.presetYoutube)
        val presetInstagram = view.findViewById<ImageView>(R.id.presetInstagram)
        val presetBBVA = view.findViewById<ImageView>(R.id.presetBBVA)
        val presetX = view.findViewById<ImageView>(R.id.presetX)

        Glide.with(this)
            .load(R.drawable.logo_facebook)
            .into(presetFacebook)

        Glide.with(this)
            .load(R.drawable.logo_gmail)
            .into(presetGmail)

        Glide.with(this)
            .load(R.drawable.logo_youtube)
            .into(presetYoutube)

        Glide.with(this)
            .load(R.drawable.logo_instagram)
            .into(presetInstagram)

        Glide.with(this)
            .load(R.drawable.logo_bbva)
            .into(presetBBVA)

        Glide.with(this)
            .load(R.drawable.logo_x)
            .into(presetX)



    }

}