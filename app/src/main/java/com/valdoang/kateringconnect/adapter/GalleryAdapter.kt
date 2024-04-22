package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.databinding.ItemVendorGaleriBinding
import com.valdoang.kateringconnect.model.Gallery

class GalleryAdapter(
    private val context: Context
) : RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    private val galleryList = ArrayList<Gallery>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Gallery>) {
        galleryList.clear()
        galleryList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemVendorGaleriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(gallery: Gallery) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(gallery)
            }

            binding.apply {
                Glide.with(context).load(gallery.foto).into(ivUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemVendorGaleriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return galleryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(galleryList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Gallery)
    }
}