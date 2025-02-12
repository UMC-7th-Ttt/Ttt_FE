package com.example.fe.bookclub_book.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fe.R
import com.example.fe.databinding.ItemCertifyPhotoBinding

class CertifyPhotoRVAdapter(private val images: List<Uri>) : RecyclerView.Adapter<CertifyPhotoRVAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photo_item_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_certify_photo, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageURI(images[position])
    }

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(private val binding: ItemCertifyPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            Glide.with(binding.root.context)
                .load(uri)
                .into(binding.photoItemIv)
        }
    }
}
