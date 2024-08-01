package com.valdoang.kateringconnect.model

data class Keranjang(
    var id: String? = null,
    var foto: String? = null,
    var kategoriId: String? = null,
    var menuId: String? = null,
    var namaMenu: String? = null,
    var jumlah: String? = null,
    var namaOpsi: String? = null,
    var catatan: String? = null,
    var hargaPerPorsi: String? = null,
    var subtotal: String? = null
)
