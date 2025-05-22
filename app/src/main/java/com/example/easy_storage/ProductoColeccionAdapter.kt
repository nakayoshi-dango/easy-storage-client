package com.example.easy_storage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easy_storage.models.ProductCollectionDTO

class ProductoColeccionAdapter(
    private var productos: MutableList<ProductCollectionDTO>,
    private val collectionName: String,
    private val onEliminarClick: (String, String) -> Unit
) : RecyclerView.Adapter<ProductoColeccionAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImagenProducto: ImageView = view.findViewById(R.id.ivImagenProducto)
        val txtNombre: TextView = view.findViewById(R.id.txtNombreProducto)
        val txtCantidad: TextView = view.findViewById(R.id.txtCantidadProducto)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_coleccion, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        if (isValidUrl(producto.imageURL)) {
            Glide.with(holder.itemView.context)
                .load(producto.imageURL)
                .placeholder(R.drawable.munequito)
                .error(R.drawable.munequito)
                .into(holder.ivImagenProducto)
        } else {
            holder.ivImagenProducto.setImageResource(R.drawable.munequito)
        }
        holder.txtNombre.text = producto.name
        holder.txtCantidad.text = "Cantidad: ${producto.quantity}"

        holder.btnEliminar.setOnClickListener {
            val removedPosition = holder.adapterPosition
            val productoId = productos[removedPosition].productId
            onEliminarClick(productoId, collectionName)  // Lógica de backend
            productos.removeAt(removedPosition)          // Elimina del adapter
            notifyItemRemoved(removedPosition)           // Notifica el cambio
        }

    }

    override fun getItemCount(): Int = productos.size

    fun actualizarLista(nuevosProductos: List<ProductCollectionDTO>) {
        productos.clear()
        productos.addAll(nuevosProductos)
        notifyDataSetChanged()
    }


    private fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val uri = Uri.parse(url)
            uri.scheme == "http" || uri.scheme == "https"
        } catch (e: Exception) {
            false
        }
    }
}
