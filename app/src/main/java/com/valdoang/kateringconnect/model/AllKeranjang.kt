package com.valdoang.kateringconnect.model

data class AllKeranjang(
    var vendorId: String? = null,
    var nama: String? = null,
    var keranjangList: ArrayList<Keranjang>? = null
)
