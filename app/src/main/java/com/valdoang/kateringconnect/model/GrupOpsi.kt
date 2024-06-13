package com.valdoang.kateringconnect.model

data class GrupOpsi(
    var id: String? = null,
    var nama: String? = null,
    var menuId: ArrayList<String>? = null,
    var opsi: ArrayList<Opsi>? = null
)
