package com.valdoang.kateringconnect.model

data class Riwayat(
    var id: String? = null,
    var menuId: String? = null,
    var menuNama: String? = null,
    var menuHarga: String? = null,
    var menuKeterangan: String? = null,
    var userId: String? = null,
    var userNama: String? = null,
    var userKota: String? = null,
    var userAlamat: String? = null,
    var userTelepon: String? = null,
    var vendorId: String? = null,
    var vendorNama: String? = null,
    var status: String? = null,
    var jumlah: String? = null,
    var catatan: String? = null,
    var jadwal: String? = null,
    var metodePembayaran: String? = null,
    var totalPembayaran: String? = null,
)
