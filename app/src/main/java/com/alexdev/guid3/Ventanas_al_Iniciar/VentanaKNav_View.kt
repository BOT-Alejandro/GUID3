package com.alexdev.guid3.Ventanas_al_Iniciar

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import com.alexdev.guid3.Fragments.Acerca_de
import com.alexdev.guid3.Fragments.Ajustes
import com.alexdev.guid3.Fragments.Ayuda
import com.alexdev.guid3.Fragments.Inicio
import com.alexdev.guid3.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.alexdev.guid3.Fragments.AuthFragment
import com.bumptech.glide.Glide
import com.alexdev.guid3.util.AvatarUtils

class VentanaKNav_View : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var toggle: ActionBarDrawerToggle
    private var headerName: TextView? = null
    private var headerEmail: TextView? = null
    private var headerImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ventana_nav)

        drawerLayout = findViewById(R.id.ventana_menu_lateral)
        navigationView = findViewById(R.id.menu_lateral)
        toolbar = findViewById(R.id.barra_de_herramientas)
        setSupportActionBar(toolbar)
        navigationView.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir_Nav, R.string.cerrar_Nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Inicializar header temprano
        val header = navigationView.getHeaderView(0)
        headerName = header.findViewById(R.id.header_user_name)
        headerEmail = header.findViewById(R.id.header_user_email)
        headerImage = header.findViewById(R.id.header_user_image)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                    } else {
                        finish()
                    }
                }
            }
        })

        firebaseAuth = FirebaseAuth.getInstance()
        authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                lockDrawerForAuth()
                clearHeader()
                val current = supportFragmentManager.findFragmentById(R.id.fragmento_contenedor)
                if (current !is AuthFragment) {
                    supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmento_contenedor, AuthFragment())
                        .commit()
                }
            } else {
                unlockDrawer()
                updateHeader(user.displayName ?: "", user.email ?: "", user.photoUrl?.toString())
                val current = supportFragmentManager.findFragmentById(R.id.fragmento_contenedor)
                if (current is AuthFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmento_contenedor, Inicio())
                        .commit()
                    navigationView.setCheckedItem(R.id.home)
                }
            }
        }
        firebaseAuth.addAuthStateListener(authListener)

        if (firebaseAuth.currentUser == null) {
            lockDrawerForAuth()
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragmento_contenedor, AuthFragment()).commit()
            }
        } else {
            unlockDrawer()
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragmento_contenedor, Inicio()).commit()
                navigationView.setCheckedItem(R.id.home)
            }
            refreshHeaderFromAuth()
        }
    }

    private fun lockDrawerForAuth() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    private fun clearHeader() {
        headerName?.text = getString(R.string.app_name)
        headerEmail?.text = ""
        headerImage?.setImageResource(R.drawable.foto)
    }

    private fun updateHeader(name: String, email: String, photoUrl: String?) {
        if (headerName == null || headerEmail == null || headerImage == null) return
        val finalName = if (name.isBlank()) email.ifBlank { getString(R.string.app_name) } else name
        headerName?.text = finalName
        headerEmail?.text = email
        if (!photoUrl.isNullOrBlank()) {
            Glide.with(this).load(photoUrl).placeholder(R.drawable.foto).error(R.drawable.foto).into(headerImage!!)
        } else {
            headerImage?.setImageBitmap(AvatarUtils.createInitialAvatar(this, finalName))
        }
    }

    fun refreshHeaderFromAuth() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            updateHeader(user.displayName ?: "", user.email ?: "", user.photoUrl?.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::firebaseAuth.isInitialized) {
            firebaseAuth.removeAuthStateListener(authListener)
        }
    }

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
            R.id.salir -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_logout_title))
                    .setMessage(getString(R.string.confirm_logout_message))
                    .setPositiveButton(getString(R.string.logout_accept)) { _, _ -> FirebaseAuth.getInstance().signOut() }
                    .setNegativeButton(getString(R.string.logout_cancel), null)
                    .show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}