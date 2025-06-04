package com.example.ukt_day1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ukt_day1.R
import com.example.ukt_day1.response.BookItem

class UserAdapter(
    private val buku: MutableList<BookItem>,
    private val onItemClick: (BookItem) -> Unit,
    private val onEdit: (BookItem) -> Unit,
    private val onDelete: (BookItem) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.coverCardView)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val tvPublisher: TextView = view.findViewById(R.id.tvPublisher)
        val tvGenre: TextView = view.findViewById(R.id.tvGenre)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = buku.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val boks = buku[position]

        // Klik seluruh card untuk Detail
        holder.itemView.setOnClickListener { onItemClick(boks) }

        // Load cover image with Glide dan corner radius
        Glide.with(holder.itemView.context)
            .load(boks.cover_image.medium)
            .apply(RequestOptions().transform(RoundedCorners(12)))
            .placeholder(R.drawable.no_image_placeholder_svg)
            .error(R.drawable.no_image_placeholder_svg)
            .into(holder.ivCover)

        // Set detail teks
        holder.tvTitle.text = boks.title
        holder.tvAuthor.text = "By ${boks.author}"
        holder.tvPublisher.text = "Publisher: ${boks.publisher}"
        holder.tvGenre.text = boks.genre

        // Edit & Delete
        holder.btnEdit.setOnClickListener { onEdit(boks) }
        holder.btnDelete.setOnClickListener { onDelete(boks) }
    }

    fun setData(newList: List<BookItem>) {
        buku.clear()
        buku.addAll(newList)
        notifyDataSetChanged()
    }
}
