package com.example.ukt_day1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ukt_day1.R
import com.example.ukt_day1.response.BookItem

class UserAdapter(
    private val buku: MutableList<BookItem>,
    private val onEdit: (BookItem) -> Unit,
    private val onDelete: (BookItem) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvId            = view.findViewById<TextView>(R.id.tvId)
        val tvIsbn          = view.findViewById<TextView>(R.id.tvIsbn)
        val tvTitle         = view.findViewById<TextView>(R.id.tvTitle)
        val tvAuthor        = view.findViewById<TextView>(R.id.tvAuthor)
        val tvPublisher     = view.findViewById<TextView>(R.id.tvPublisher)
        val tvPublishedDate = view.findViewById<TextView>(R.id.tvPublished_date)
        val tvGenre         = view.findViewById<TextView>(R.id.tvGenre)
        val tvLanguage      = view.findViewById<TextView>(R.id.tvLanguage)
        val tvDescription   = view.findViewById<TextView>(R.id.tvDescription)
        val ivCover = view.findViewById<ImageView>(R.id.ivCover)
        val btnEdit         = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete       = view.findViewById<Button>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
    )

    override fun getItemCount(): Int = buku.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val boks = buku[position]
        holder.tvId.text            = "ID: ${boks.id}"
        holder.tvIsbn.text          = "ISBN: ${boks.isbn}"
        holder.tvTitle.text         = "Title: ${boks.title}"
        holder.tvAuthor.text        = "Author: ${boks.author}"
        holder.tvPublisher.text     = "Publisher: ${boks.publisher}"
        holder.tvPublishedDate.text = "Published: ${boks.published_date}"
        holder.tvGenre.text         = "Genre: ${boks.genre}"
        holder.tvLanguage.text      = "Language: ${boks.language}"
        holder.tvDescription.text   = "Description: ${boks.description}"
        // For now show medium cover URL; later can replace with ImageView
        holder.ivCover.setImageResource(R.drawable.placeholder_image)

        holder.btnEdit.setOnClickListener { onEdit(boks) }
        holder.btnDelete.setOnClickListener { onDelete(boks) }
    }

    fun setData(newList: List<BookItem>) {
        buku.clear()
        buku.addAll(newList)
        notifyDataSetChanged()
    }
}
