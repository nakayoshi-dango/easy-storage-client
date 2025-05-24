package com.example.easy_storage

import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easy_storage.models.CollectionDTO

class ColeccionesAdapter(
    private var colecciones: List<CollectionDTO>,
    private val onOptionSelected: (coleccion: CollectionDTO, option: String) -> Unit = { _, _ -> },
    private val onItemClick: (coleccion: CollectionDTO) -> Unit = {},
    private val currentUsername: String? = null,
    private val isAdmin: Boolean = false
) : RecyclerView.Adapter<ColeccionesAdapter.ColeccionViewHolder>() {

    inner class ColeccionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImageURL: ImageView = view.findViewById(R.id.imgColeccion)
        val txtNombre: TextView = view.findViewById(R.id.txtNombreColeccion)
        val txtId: TextView = view.findViewById(R.id.txtIdColeccion)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenuColeccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColeccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coleccion, parent, false)
        return ColeccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColeccionViewHolder, position: Int) {
        val coleccion = colecciones[position]

        if (!coleccion.imageURL.isNullOrBlank() && isValidUrl(coleccion.imageURL)) {
            Glide.with(holder.itemView.context).load(coleccion.imageURL).into(holder.ivImageURL)
        } else {
            holder.ivImageURL.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.txtNombre.text = coleccion.name ?: "Nombre desconocido"
        holder.txtId.text = "ID: ${coleccion.id}"

        val esPropietario = coleccion.ownerUsername == currentUsername

        if (isAdmin || esPropietario) {
            holder.btnMenu.visibility = View.VISIBLE
            holder.btnMenu.isEnabled = true
            holder.btnMenu.setOnClickListener {
                val popup = PopupMenu(holder.itemView.context, holder.btnMenu)
                MenuInflater(holder.itemView.context).inflate(R.menu.menu_item_producto, popup.menu)
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.option_editar -> onOptionSelected(coleccion, "editar")
                        R.id.option_eliminar -> onOptionSelected(coleccion, "eliminar")
                    }
                    true
                }
                popup.show()
            }
        } else {
            holder.btnMenu.visibility = View.GONE
            holder.btnMenu.isEnabled = false
        }

        holder.itemView.setOnClickListener {
            onItemClick(coleccion)
        }
    }

    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val uri = Uri.parse(url)
            (uri.scheme == "http" || uri.scheme == "https")
        } catch (e: Exception) {
            false
        }
    }

    override fun getItemCount(): Int = colecciones.size
}
