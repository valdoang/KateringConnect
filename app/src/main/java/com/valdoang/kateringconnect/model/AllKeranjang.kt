package com.valdoang.kateringconnect.model

data class AllKeranjang(
    var vendorId: String? = null,
    var ongkir: String? = null,
    var jarak: String? = null,
    var keranjangList: ArrayList<Keranjang>? = null
)
