package com.student.guildwarsapi2


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MaterialAdapter(private val materials: List<MainActivity.MaterialItem>) :
    RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialImageView: ImageView = itemView.findViewById(R.id.materialImageView)
        val materialTextView: TextView = itemView.findViewById(R.id.materialTextView)
        val countTextView: TextView = itemView.findViewById(R.id.countTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        holder.materialTextView.text = material.name
        holder.countTextView.text = material.count.toString()

        Glide.with(holder.itemView.context)
            .load(material.icon)
            .into(holder.materialImageView)
    }

    override fun getItemCount() = materials.size
}
