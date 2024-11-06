package com.valdoang.kateringconnect.model

data class Pesanan(
    var id: String? = null,
    var userId: String? = null,
    var userNama: String? = null,
    var userKota: String? = null,
    var userAlamat: String? = null,
    var userTelepon: String? = null,
    var userFoto: String? = null,
    var vendorFoto: String? = null,
    var vendorId: String? = null,
    var vendorNama: String? = null,
    var status: String? = null,
    var jadwal: String? = null,
    var metodePembayaran: String? = null,
    var ongkir: String? = null,
    var nilai: Boolean? = null,
    var fotoBuktiPengiriman: String? = null
)
