package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemUserBerandaBinding
import com.valdoang.kateringconnect.model.Star
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.utils.roundOffDecimal
import com.valdoang.kateringconnect.utils.withNumberingFormat
import java.util.stream.Collectors
import kotlin.math.roundToLong

class UserBerandaAdapter(
    private val context: Context, private val alamatUser: String
) : RecyclerView.Adapter<UserBerandaAdapter.MyViewHolder>() {

    private val vendorList = ArrayList<Vendor>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var starList: ArrayList<Star> = ArrayList()
    private var kategoriMenuList: ArrayList<String> = ArrayList()
    private var totalNilai = 0.0
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Vendor>) {
        vendorList.clear()
        vendorList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemUserBerandaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(vendor: Vendor) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(vendor)
            }

            binding.apply {
                Glide.with(context).load(vendor.foto).error(R.drawable.default_vendor_profile).into(ivKatering)
                tvKateringName.text = vendor.nama

                //Hitung Ongkos Kirim
                val coder = Geocoder(context)
                try {
                    val userAddress : List<Address> = coder.getFromLocationName(alamatUser,5)!!
                    val userLocation = userAddress[0]
                    val userLat = userLocation.latitude
                    val userLon = userLocation.longitude

                    val vendorAddress : List<Address> = coder.getFromLocationName(vendor.alamat!!,5)!!
                    val vendorLocation = vendorAddress[0]
                    val vendorLat = vendorLocation.latitude
                    val vendorLon = vendorLocation.longitude

                    val userPoint = Location("locationA")
                    userPoint.latitude = userLat
                    userPoint.longitude = userLon

                    val vendorPoint = Location("locationB")
                    vendorPoint.latitude = vendorLat
                    vendorPoint.longitude = vendorLon

                    val jarak = userPoint.distanceTo(vendorPoint) / 1000

                    val ongkir = jarak.roundToLong() * 3000
                    tvKateringOngkir.text = context.getString(R.string.rupiah_text, ongkir.withNumberingFormat())

                } catch (e: Exception) {
                    Log.d(ContentValues.TAG, e.localizedMessage as String)
                }

                val nilaiRef = db.collection("nilai").whereEqualTo("vendorId", vendor.id)
                nilaiRef.addSnapshotListener { snapshot,_ ->
                    if (snapshot != null) {
                        starList.clear()
                        totalNilai = 0.0
                        for (data in snapshot.documents) {
                            val star: Star? = data.toObject(Star::class.java)
                            if (star != null) {
                                starList.add(star)
                            }
                        }

                        for (i in starList) {
                            val nilai = i.nilai?.toDouble()
                            if (nilai != null) {
                                totalNilai += nilai
                            }
                        }

                        val sizeNilai = starList.size
                        val nilaiStar = totalNilai/sizeNilai

                        if (sizeNilai == 0) {
                            ivKateringStar.visibility = View.GONE
                            tvKateringStar.visibility = View.GONE
                        }
                        else {
                            ivKateringStar.visibility = View.VISIBLE
                            tvKateringStar.visibility = View.VISIBLE
                            tvKateringStar.text = context.getString(R.string.vendor_star, nilaiStar.roundOffDecimal(), sizeNilai.toString().withNumberingFormat())
                        }

                    }
                }

                if (vendor.kategoriMenu?.isEmpty() == true) {
                    ivKateringKategori.visibility = View.GONE
                    tvKateringKategori.visibility = View.GONE
                } else {
                    ivKateringKategori.visibility = View.VISIBLE
                    tvKateringKategori.visibility = View.VISIBLE
                    tvKateringKategori.text = vendor.kategoriMenu
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemUserBerandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return vendorList.size
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(vendorList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Vendor)
    }
}