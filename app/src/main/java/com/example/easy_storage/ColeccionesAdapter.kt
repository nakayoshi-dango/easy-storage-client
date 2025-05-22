package com.example.easy_storage

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easy_storage.models.CollectionDTO

class ColeccionesAdapter(
    private var colecciones: List<CollectionDTO>,
    private val onOptionSelected: (coleccion: CollectionDTO, option: String) -> Unit = { _, _ -> },
    private val onItemClick: (coleccion: CollectionDTO) -> Unit = {}
) : RecyclerView.Adapter<ColeccionesAdapter.ColeccionViewHolder>() {

    inner class ColeccionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        holder.txtNombre.text = coleccion.name ?: "Nombre desconocido"
        holder.txtId.text = "ID: ${coleccion.id}"

        holder.itemView.setOnClickListener {
            onItemClick(coleccion)
        }

        holder.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            MenuInflater(view.context).inflate(R.menu.menu_item_producto, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.option_editar -> onOptionSelected(coleccion, "editar")
                    R.id.option_eliminar -> onOptionSelected(coleccion, "eliminar")
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = colecciones.size
}
