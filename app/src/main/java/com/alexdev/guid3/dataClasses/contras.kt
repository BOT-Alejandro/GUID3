package com.alexdev.guid3.dataClasses

data class contras(
    var imgSeleccionada: Int,
    var titulo: String,
    var correo: String,
    var contra: String,
    var iconoPersonalizado: String? = null,
    var esVisible: Boolean = false
)
