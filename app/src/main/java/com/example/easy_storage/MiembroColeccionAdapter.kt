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
import com.example.easy_storage.models.UserDTO

class MiembroColeccionAdapter(
    private var miembros: MutableList<UserDTO>,
    private val collectionName: String,
    private val onEliminarClick: (String, String) -> Unit,
    private val puedeEliminar: Boolean
) : RecyclerView.Adapter<MiembroColeccionAdapter.MiembroViewHolder>() {

    inner class MiembroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImagenUsuario: ImageView = view.findViewById(R.id.ivImagenUsuario)
        val txtNombre: TextView = view.findViewById(R.id.txtNombreMiembro)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarMiembro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiembroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_miembro_coleccion, parent, false)
        return MiembroViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiembroViewHolder, position: Int) {
        val miembro = miembros[position]

        // Imagen
        if (isValidUrl(miembro.imageURL)) {
            Glide.with(holder.itemView.context)
                .load(miembro.imageURL)
                .placeholder(R.drawable.munequito)
                .error(R.drawable.munequito)
                .into(holder.ivImagenUsuario)
        } else {
            holder.ivImagenUsuario.setImageResource(R.drawable.munequito)
        }

        holder.txtNombre.text = miembro.username

        // Mostrar o no el botón eliminar
        holder.btnEliminar.visibility = if (puedeEliminar) View.VISIBLE else View.GONE

        // Acción eliminar
        holder.btnEliminar.setOnClickListener {
            val removedPosition = holder.adapterPosition
            onEliminarClick(collectionName, miembro.username)
            miembros.removeAt(removedPosition)
            notifyItemRemoved(removedPosition)
        }
    }

    fun actualizarLista(nuevaLista: List<UserDTO>) {
        miembros.clear()
        miembros.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = miembros.size

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
