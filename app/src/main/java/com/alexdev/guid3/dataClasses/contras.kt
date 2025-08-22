package com.alexdev.guid3.dataClasses

import com.google.firebase.firestore.Exclude
import com.google.firebase.Timestamp

data class contras(
    var imgSeleccionada: Int = 0,
    var titulo: String = "",
    var correo: String = "",
    var contra: String = "",
    var iconoPersonalizado: String? = null,
    var esVisible: Boolean = false,
    var uid: String? = null,
    @get:Exclude @set:Exclude var id: String? = null,
    var iv: String? = null,
    var createdAt: Timestamp? = null,
    var updatedAt: Timestamp? = null
) {

    constructor() : this(0, "", "", "", null, false, null, null, null, null, null)
}