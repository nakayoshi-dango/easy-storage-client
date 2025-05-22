package com.example.easy_storage

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easy_storage.models.ProductDTO

class AddProductoAdapter(
    private val productos: List<ProductDTO>
) : RecyclerView.Adapter<AddProductoAdapter.ViewHolder>() {

    private val cantidades = mutableMapOf<String, Int>()  // productId -> cantidad

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.ivImagenProducto)
        val nombre: TextView = view.findViewById(R.id.txtNombreProducto)
        val id: TextView = view.findViewById(R.id.txtIdProducto)
        val btnRestar: ImageButton = view.findViewById(R.id.btnRestar)
        val btnSumar: ImageButton = view.findViewById(R.id.btnSumar)
        val inputCantidad: EditText = view.findViewById(R.id.inputCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_producto_coleccion, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = productos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.name
        holder.id.text = producto.id
        holder.inputCantidad.setText("0")

        // Opcional: imagen estática
        holder.imagen.setImageResource(R.drawable.munequito)

        // Inicializamos cantidad
        cantidades[producto.id] = 0

        holder.btnSumar.setOnClickListener {
            val actual = cantidades[producto.id] ?: 0
            val nuevo = actual + 1
            cantidades[producto.id] = nuevo
            holder.inputCantidad.setText(nuevo.toString())
        }

        holder.btnRestar.setOnClickListener {
            val actual = cantidades[producto.id] ?: 0
            val nuevo = actual - 1
            cantidades[producto.id] = nuevo
            holder.inputCantidad.setText(nuevo.toString())
        }

        holder.inputCantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val cantidad = s.toString().toIntOrNull() ?: 0
                cantidades[producto.id] = cantidad
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun obtenerCambios(): Map<String, Int> {
        return cantidades.filter { it.value != 0 }
    }
}
