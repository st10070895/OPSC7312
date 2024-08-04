package com.student.guildwarsapi2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MaterialAdapter(private var materials: List<MaterialItem>) :
    RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    // ViewHolder class to hold references to the views
    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialImageView: ImageView = itemView.findViewById(R.id.materialImageView)
        val materialNameTextView: TextView = itemView.findViewById(R.id.materialNameTextView)
        val materialCountTextView: TextView = itemView.findViewById(R.id.materialCountTextView)
        val materialBuyPriceTextView: TextView = itemView.findViewById(R.id.materialBuyPriceTextView)
        val materialSellPriceTextView: TextView = itemView.findViewById(R.id.materialSellPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        holder.materialNameTextView.text = material.name
        holder.materialCountTextView.text = "Count: ${material.count}"
        holder.materialBuyPriceTextView.text = "Buy Price: ${material.buyPrice}"
        holder.materialSellPriceTextView.text = "Sell Price: ${material.sellPrice}"

        Glide.with(holder.itemView.context)
            .load(material.icon)
            .into(holder.materialImageView)
    }

    override fun getItemCount() = materials.size

    // Method to update data and notify adapter
    fun updateData(newMaterials: List<MaterialItem>) {
        materials = newMaterials
        notifyDataSetChanged()
    }
}
