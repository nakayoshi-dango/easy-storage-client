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
import com.example.easy_storage.models.ProductDTO
import android.util.Log

class ProductosAdapter(
    private var productDTOS: List<ProductDTO>,
    private val onOptionSelected: (productDTO: ProductDTO, option: String) -> Unit = { _, _ -> },
    private val onItemClick: (productDTO: ProductDTO) -> Unit = {}
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {


    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto)
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtId: TextView = view.findViewById(R.id.txtId)
        val menuButton: ImageButton = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productDTOS[position]
        Log.e("ProductosAdapter", "Producto en la posición $position: $producto")
        holder.txtNombre.text = producto.name?: "Nombre desconocido"
        holder.txtId.text = "ID: ${producto.id}"

        if (!producto.imageURL.isNullOrBlank() && isValidUrl(producto.imageURL)) {
            Glide.with(holder.itemView.context).load(producto.imageURL).into(holder.imgProducto)
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.itemView.setOnClickListener {
            onItemClick(producto)
        }

        holder.menuButton.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            MenuInflater(view.context).inflate(R.menu.menu_item_producto, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.option_editar -> onOptionSelected(producto, "editar")
                    R.id.option_eliminar -> onOptionSelected(producto, "eliminar")
                }
                true
            }
            popup.show()
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

    override fun getItemCount(): Int = productDTOS.size
}
