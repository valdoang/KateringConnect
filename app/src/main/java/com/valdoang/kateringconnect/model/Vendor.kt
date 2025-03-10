package com.valdoang.kateringconnect.model

data class Vendor(
    var id: String? = null,
    val foto: String? = null,
    val nama: String? = null,
    val alamat: String? = null,
    val email: String? = null,
    val jenis: String? = null,
    val kota: String? = null,
    val telepon: String? = null,
    var kategoriMenu: String? = null,
    var ongkir: Long? = 0L,
    var sizeNilai: Int? = 0,
    var nilai: Double? = 0.0

)
