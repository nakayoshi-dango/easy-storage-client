package com.example.easy_storage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easy_storage.models.UserDTO

class AddMiembroAdapter(private val usuarios: List<UserDTO>) :
    RecyclerView.Adapter<AddMiembroAdapter.UsuarioViewHolder>() {

    private val seleccionados = mutableSetOf<UserDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_miembro_coleccion, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int = usuarios.size

    fun getUsuariosSeleccionados(): List<UserDTO> = seleccionados.toList()

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagen = itemView.findViewById<ImageView>(R.id.ivFotoUsuario)
        private val nombre = itemView.findViewById<TextView>(R.id.tvNombreUsuario)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.checkboxUsuario)

        fun bind(usuario: UserDTO) {

            nombre.text = usuario.username

            if (!usuario.imageURL.isNullOrBlank() && isValidUrl(usuario.imageURL)) {
                Glide.with(imagen.context).load(usuario.imageURL).into(imagen)
            } else {
                imagen.setImageResource(R.drawable.ic_launcher_foreground)
            }

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = seleccionados.contains(usuario)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) seleccionados.add(usuario)
                else seleccionados.remove(usuario)
            }
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
}
