package com.example.codev.addpage

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codev.databinding.ImageItemBinding
import java.io.File

class ImageRVAdapter(private var context: Context, private var imageList: ArrayList<ImageItem>, private val returnData: (ImageItem) -> Unit): RecyclerView.Adapter<ImageRVAdapter.ImageViewHolder>(){
    inner class ImageViewHolder(private val viewBinding: ImageItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(nowImageItem: ImageItem){
            if(nowImageItem.imageUrl == ""){
                Glide.with(context).load(nowImageItem.imageUri).into(viewBinding.selectedImage)
            }else{
                Glide.with(context).load(nowImageItem.imageUrl).into(viewBinding.selectedImage)
            }
            viewBinding.cancelButton.setOnClickListener {
                imageList.remove(nowImageItem)
                val deleteFile = File(nowImageItem.imageCopyPath)
                deleteFile.delete()
                notifyItemRemoved(adapterPosition)
                returnData(nowImageItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val viewBinding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}