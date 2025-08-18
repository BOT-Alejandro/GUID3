package com.alexdev.guid3.Ventanas_al_Iniciar

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.alexdev.guid3.Fragments.Acerca_de
import com.alexdev.guid3.Fragments.Ajustes
import com.alexdev.guid3.Fragments.Ayuda
import com.alexdev.guid3.Fragments.Inicio
import com.alexdev.guid3.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.alexdev.guid3.Fragments.AuthFragment

class VentanaKNav_View : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ventana_nav)

        drawerLayout = findViewById(R.id.ventana_menu_lateral)
        val img_usuario = findViewById<ImageView>(R.id.img_usuario)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.menu_lateral)
        navigationView.setNavigationItemSelectedListener(this)
        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir_Nav, R.string.cerrar_Nav)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        // Verificar usuario activo con Firebase Auth
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        if (usuarioActual == null) {
            // No hay usuario, mostrar AuthFragment
            supportFragmentManager.beginTransaction().replace(R.id.fragmento_contenedor, AuthFragment()).commit()
            navigationView.setCheckedItem(0) // Desmarcar menú
        } else {
            // Usuario autenticado, mostrar Inicio
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragmento_contenedor, Inicio()).commit()
                navigationView.setCheckedItem(R.id.home)
            }
        }

        img_usuario.setOnClickListener {
            val fragmentAjustes = Ajustes()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmento_contenedor, fragmentAjustes)
                .addToBackStack(null)
                .commit()

        }
    }



    // Funcionalidad del menu lateral al seleccionar cada item dentro del menu te redireccione a la actividad o ventana correspondiente
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmento_contenedor, Inicio()).addToBackStack(null).commit()
            R.id.ayuda -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmento_contenedor, Ayuda()).addToBackStack(null).commit()
            R.id.ajustes -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmento_contenedor, Ajustes()).addToBackStack(null).commit()
            R.id.acerca_de -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmento_contenedor, Acerca_de()).addToBackStack(null).commit()
            R.id.salir -> Toast.makeText(this, "Cerrando la Aplicacion", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}