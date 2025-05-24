package com.example.easy_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.easy_storage.api.RetrofitInstance
import com.example.easy_storage.api.products.ProductsRepository
import com.example.easy_storage.api.users.UsersRepository
import com.example.easy_storage.models.ProductDTO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject

class ProductosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var adapter: ProductosAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val usersRepository = UsersRepository(RetrofitInstance.usersApi)
    private val productsRepository = ProductsRepository(RetrofitInstance.productsApi)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_productos, container, false)

        recyclerView = view.findViewById(R.id.recyclerProductos)
        fabAgregar = view.findViewById(R.id.fabCrearProducto)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        parentFragmentManager.setFragmentResultListener("scannerResult", this) { _, bundle ->
            val scannedId = bundle.getString("scannedProductId")
            mostrarDialogoCrear(scannedId)
        }

        fabAgregar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    ScannerFragment()
                ) // Usa el ID correcto de tu contenedor
                .addToBackStack(null)
                .commit()

        }

        swipeRefreshLayout.setOnRefreshListener {
            loadProductos()
        }

        // Cargar productos al iniciar
        loadProductos()

        return view
    }

    private fun loadProductos() {
        swipeRefreshLayout.isRefreshing = true

        productsRepository.getAllProducts { productos ->
            // Verifica que el fragmento esté aún asociado a su actividad antes de hacer nada en el hilo de UI
            if (!isAdded || activity == null) return@getAllProducts

            requireActivity().runOnUiThread {
                // Vuelve a verificar antes de tocar vistas
                if (!isAdded || view == null) return@runOnUiThread

                swipeRefreshLayout.isRefreshing = false
                usersRepository.getCurrentUser { user ->
                    if (productos != null) {
                        adapter = ProductosAdapter(
                            productos,
                            user!!,
                            onOptionSelected = { producto, opcion ->
                                when (opcion) {
                                    "editar" -> mostrarDialogoEditar(producto)
                                    "eliminar" -> eliminarProducto(producto)
                                }
                            },
                            onItemClick = { producto ->
                                mostrarDialogoDetalles(producto)
                            }
                        )
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar productos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun mostrarDialogoDetalles(producto: ProductDTO) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_detalles_producto, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Detalles del producto")
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .create()

        dialogView.findViewById<TextView>(R.id.tvId).text = "ID: ${producto.id}"
        dialogView.findViewById<TextView>(R.id.tvNombre).text = "Nombre: ${producto.name}"
        dialogView.findViewById<TextView>(R.id.tvDescripcion).text =
            "Descripción: ${producto.description}"
        dialogView.findViewById<TextView>(R.id.tvDondeComprar).text =
            "Dónde comprar: ${producto.whereToBuy}"

        val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
        if (!producto.imageURL.isNullOrBlank()) {
            Glide.with(requireContext()).load(producto.imageURL).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        dialog.show()
    }

    private fun mostrarDialogoEditar(producto: ProductDTO) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_producto, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Editar producto")
            .setView(dialogView)
            .create()

        val inputNombre = dialogView.findViewById<EditText>(R.id.inputNombre)
        val inputDescripcion = dialogView.findViewById<EditText>(R.id.inputDescripcion)
        val inputDondeComprar = dialogView.findViewById<EditText>(R.id.inputDondeComprar)
        val inputURLImagen = dialogView.findViewById<EditText>(R.id.inputURLImagen)

        // Rellenar campos
        inputNombre.setText(producto.name)
        inputDescripcion.setText(producto.description)
        inputDondeComprar.setText(producto.whereToBuy)
        inputURLImagen.setText(producto.imageURL)

        dialogView.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (inputNombre.text.toString().length < 255 && inputDondeComprar.text.toString().length < 255) {
                val jsonEditado = JsonObject().apply {
                    addProperty("id", producto.id)
                    addProperty("name", inputNombre.text.toString())
                    addProperty("description", inputDescripcion.text.toString())
                    addProperty("whereToBuy", inputDondeComprar.text.toString())
                    if (inputURLImagen.text.toString().isNotEmpty()) {
                        addProperty("imageURL", inputURLImagen.text.toString())
                    }
                }

                productsRepository.updateProduct(jsonEditado) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(requireContext(), "Producto editado", Toast.LENGTH_SHORT)
                                .show()
                            loadProductos()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Error al editar", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error. Dónde comprar o nombre demasiado largo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoCrear(productId: String?) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_producto, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Nuevo producto")
            .setView(dialogView)
            .create()

        val inputNombre = dialogView.findViewById<EditText>(R.id.inputNombre)
        val inputDescripcion = dialogView.findViewById<EditText>(R.id.inputDescripcion)
        val inputDondeComprar = dialogView.findViewById<EditText>(R.id.inputDondeComprar)
        val inputURLImagen = dialogView.findViewById<EditText>(R.id.inputURLImagen)

        dialogView.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (inputNombre.text.toString().length < 255 && inputDondeComprar.text.toString().length < 255) {
                val nuevoProducto = JsonObject().apply {
                    addProperty("id", productId)
                    addProperty("name", inputNombre.text.toString())
                    addProperty("description", inputDescripcion.text.toString())
                    addProperty("whereToBuy", inputDondeComprar.text.toString())
                    if (inputURLImagen.text.toString().isNotEmpty()) {
                        addProperty("imageURL", inputURLImagen.text.toString())
                    }
                }

                productsRepository.createProduct(nuevoProducto) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(requireContext(), "Producto creado", Toast.LENGTH_SHORT)
                                .show()
                            loadProductos()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error. Ya existe un producto con esta ID o nombre.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error. Dónde comprar o nombre demasiado largo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun eliminarProducto(producto: ProductDTO) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que quieres eliminar el producto \"${producto.name}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                productsRepository.deleteProduct(producto.id) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(
                                requireContext(),
                                "Producto eliminado",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadProductos()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al eliminar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}
