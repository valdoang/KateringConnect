package com.valdoang.kateringconnect.model

data class Menu(
    var id: String? = null,
    val foto: String? = null,
    val nama: String? = null,
    val keterangan: String? = null,
    val harga: String? = null,
    val storageKeys: String? = null,
    val grupOpsiId: ArrayList<String>? = null
)
