package com.valdoang.kateringconnect.model

data class User(
    var id: String? = null,
    val foto: String? = null,
    val nama: String? = null,
    val alamat: String? = null,
    val email: String? = null,
    val jenis: String? = null,
    val kota: String? = null,
    val telepon: String? = null,
    val statusPendaftaran: String? = null,
    val fotoKtp: String? = null,
    val fotoSelfieKtp: String? = null,
)
