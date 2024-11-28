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

class VentanaKNav_View : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ventana_nav)

        drawerLayout = findViewById(R.id.ventana_menu_lateral)
        val img_usuario = findViewById<ImageView>(R.id.img_usuario)

        // Variable toolbar la cual se asigna mediante el metodo findViewById a el objeto referenciado con el id barra_de_herramientas en el layout ventana_principal.xml
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.barra_de_herramientas)
        setSupportActionBar(toolbar)

        // Variable navigationView la cual se asigna mediante el metodo findViewById a el objeto NavigationView referenciado con el id menu_lateral en el layout ventana_principal.xml
        val navigationView = findViewById<NavigationView>(R.id.menu_lateral)
        navigationView.setNavigationItemSelectedListener(this)

        // Metodo para variar el menu lateral en base al estado en el que se encuentre
        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir_Nav, R.string.cerrar_Nav)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        // Condicion para reemplazar la actividad o ventana que se muestra con el contenedor establecido en el layout ventana_principal.xml para que muestre por defecto el fragmento Inicio
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmento_contenedor, Inicio()).commit()
            navigationView.setCheckedItem(R.id.home)
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

    // Funcion para regresar a las ventanas anteriores almacenadas en la cola y para cerrar el sistema de navegacion con el boton regresar del dispositivo movil
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }

    }




}