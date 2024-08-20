package com.valdoang.kateringconnect.model

data class Pesanan(
    var id: String? = null,
    /*var menuId: String? = null,
    var kategoriId: String? = null,
    var menuNama: String? = null,
    var namaOpsi: String? = null,
    var menuKeterangan: String? = null,*/
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
    /*var jumlah: String? = null,
    var catatan: String? = null,*/
    var jadwal: String? = null,
    var metodePembayaran: String? = null,
    /*var totalHarga: String? = null,
    var subtotal: String? = null,
    var hargaPerPorsi: String? = null,*/
    var ongkir: String? = null,
    var nilai: Boolean? = null
)
