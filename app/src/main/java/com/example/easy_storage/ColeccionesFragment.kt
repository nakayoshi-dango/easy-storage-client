package com.example.easy_storage

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.easy_storage.api.RetrofitInstance
import com.example.easy_storage.api.collections.CollectionsRepository
import com.example.easy_storage.models.CollectionDTO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import androidx.appcompat.app.AlertDialog
import com.example.easy_storage.api.products.ProductsRepository
import com.example.easy_storage.api.users.UsersRepository
import com.example.easy_storage.models.ProductCollectionDTO
import com.example.easy_storage.models.UserDTO


class ColeccionesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var adapter: ColeccionesAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val productsRepository = ProductsRepository(RetrofitInstance.productsApi)
    private val usersRepository = UsersRepository(RetrofitInstance.usersApi)
    private val collectionsRepository = CollectionsRepository(RetrofitInstance.collectionsApi)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_colecciones, container, false)

        recyclerView = view.findViewById(R.id.recyclerColecciones)
        fabAgregar = view.findViewById(R.id.fabCrearColeccion)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutColecciones)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ColeccionesAdapter(emptyList())
        recyclerView.adapter = adapter

        fabAgregar.setOnClickListener {
            mostrarDialogoCrear()
        }

        swipeRefreshLayout.setOnRefreshListener {
            loadColecciones()
        }

        loadColecciones()

        return view
    }

    private fun loadColecciones() {
        swipeRefreshLayout.isRefreshing = true

        collectionsRepository.getVisibleCollections { colecciones ->
            if (!isAdded || activity == null) return@getVisibleCollections

            requireActivity().runOnUiThread {
                if (!isAdded || view == null) return@runOnUiThread

                swipeRefreshLayout.isRefreshing = false

                if (colecciones != null) {
                    adapter = ColeccionesAdapter(
                        colecciones,
                        onOptionSelected = { coleccion, opcion ->
                            when (opcion) {
                                "editar" -> mostrarDialogoEditar(coleccion)
                                "eliminar" -> eliminarColeccion(coleccion)
                            }
                        },
                        onItemClick = { coleccion ->
                            var miembros: List<UserDTO>? = null
                            var productos: List<ProductCollectionDTO>? = null

                            fun intentarMostrarDialogo() {
                                // Solo se ejecuta cuando ambos valores no son null
                                if (miembros != null && productos != null) {
                                    mostrarDialogoDetalles(
                                        coleccion,
                                        miembros!!,
                                        productos!!,
                                        onEliminarMiembro = { collectionName, usernameToDelete ->
                                            usersRepository.deleteFromCollection(collectionName, usernameToDelete) { success ->
                                                requireActivity().runOnUiThread {
                                                    if (success) {
                                                        Toast.makeText(requireContext(), "Miembro eliminado", Toast.LENGTH_SHORT).show()
                                                        loadColecciones()
                                                    } else {
                                                        Toast.makeText(requireContext(), "Error al eliminar miembro", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        onEliminarProducto = { productId, collectionName ->
                                            productsRepository.deleteFromCollection(productId, collectionName) { success ->
                                                requireActivity().runOnUiThread {
                                                    if (success) {
                                                        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                                                        loadColecciones()
                                                    } else {
                                                        Toast.makeText(requireContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            // Obtener miembros
                            collectionsRepository.getUsersInCollection(coleccion.name) { users ->
                                requireActivity().runOnUiThread {
                                    miembros = users ?: emptyList()
                                    intentarMostrarDialogo()
                                }
                            }



                            // Obtener productos
                            collectionsRepository.getProductsInCollection(coleccion.name) { products ->
                                requireActivity().runOnUiThread {
                                    productos = products ?: emptyList()
                                    intentarMostrarDialogo()
                                }
                            }

                        }
                    )
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar colecciones",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun mostrarDialogoDetalles(
        coleccion: CollectionDTO,
        miembros: List<UserDTO>,
        productos: List<ProductCollectionDTO>,
        onEliminarMiembro: (String, String) -> Unit,
        onEliminarProducto: (String, String) -> Unit
    ) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_detalles_coleccion, null)

        // Elementos de la cabecera
        val tvId = dialogView.findViewById<TextView>(R.id.tvIdColeccion)
        val tvNombre = dialogView.findViewById<TextView>(R.id.tvNombreColeccion)
        val tvDescripcion = dialogView.findViewById<TextView>(R.id.tvDescripcionColeccion)

        tvId.text = "ID: ${coleccion.id}"
        tvNombre.text = "Nombre: ${coleccion.name}"
        tvDescripcion.text = "Descripción: ${coleccion.description}"

        // Recycler de miembros
        val rvMiembros = dialogView.findViewById<RecyclerView>(R.id.rvMiembros)
        rvMiembros.layoutManager = LinearLayoutManager(requireContext())
        val miembroAdapter = MiembroColeccionAdapter(miembros.toMutableList(), coleccion.name, onEliminarMiembro)
        rvMiembros.adapter = miembroAdapter

        // Recycler de productos
        val rvProductos = dialogView.findViewById<RecyclerView>(R.id.rvProductos)
        rvProductos.layoutManager = LinearLayoutManager(requireContext())
        val productoAdapter = ProductoColeccionAdapter(productos.toMutableList(), coleccion.name, onEliminarProducto)
        rvProductos.adapter = productoAdapter

        // Botones flotantes
        val btnAddMiembro = dialogView.findViewById<ImageButton>(R.id.btnAddMiembro)
        val btnAddProducto = dialogView.findViewById<ImageButton>(R.id.btnAddProducto)

        btnAddMiembro.setOnClickListener {
            collectionsRepository.getNonUsersInCollection(coleccion.name) { noMiembros ->
                requireActivity().runOnUiThread {
                    mostrarDialogoAddMiembro(
                        coleccion.name,
                        usuariosDisponibles = noMiembros ?: emptyList(),
                        onMiembrosActualizados = { nuevosMiembros ->
                            // Actualizamos todos los miembros desde backend para estar seguros
                            collectionsRepository.getUsersInCollection(coleccion.name) { actualizados ->
                                requireActivity().runOnUiThread {
                                    miembroAdapter.actualizarLista(actualizados ?: emptyList())
                                }
                            }
                        }
                    )
                }
            }
        }



        btnAddProducto.setOnClickListener {
            mostrarDialogoAddProducto(
                coleccion.name,
                onProductosActualizados = { nuevosProductos ->
                    productoAdapter.actualizarLista(nuevosProductos)
                }
            )
        }


        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .show()
    }

    private fun mostrarDialogoAddMiembro(
        nombreColeccion: String,
        usuariosDisponibles: List<UserDTO>,
        onMiembrosActualizados: (List<UserDTO>) -> Unit
    ) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_miembro_coleccion, null)

        val rvUsuarios = dialogView.findViewById<RecyclerView>(R.id.rvUsuariosDisponibles)
        val btnConfirmar = dialogView.findViewById<ImageButton>(R.id.btnConfirmarMiembros)

        rvUsuarios.layoutManager = LinearLayoutManager(requireContext())

        val adapter = AddMiembroAdapter(usuariosDisponibles)
        rvUsuarios.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnConfirmar.setOnClickListener {
            val seleccionados = adapter.getUsuariosSeleccionados()

            val usuariosAñadidos = mutableListOf<UserDTO>()
            var pendientes = seleccionados.size
            if (pendientes == 0) {
                Toast.makeText(requireContext(), "No has seleccionado ningún usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            for (user in seleccionados) {
                usersRepository.addToCollection(nombreColeccion, user.username) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            usuariosAñadidos.add(user)
                        }
                        pendientes--
                        if (pendientes == 0) {
                            Toast.makeText(requireContext(), "Miembros añadidos", Toast.LENGTH_SHORT).show()
                            onMiembrosActualizados(usuariosAñadidos)
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        dialog.show()
    }


    private fun mostrarDialogoAddProducto(
        nombreColeccion: String,
        onProductosActualizados: (List<ProductCollectionDTO>) -> Unit
    ) {
        productsRepository.getAllProducts { productos ->
            requireActivity().runOnUiThread {
                if (productos != null) {
                    val dialogView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_add_producto_coleccion, null)

                    val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvProductosDisponibles)
                    val btnConfirmar = dialogView.findViewById<ImageButton>(R.id.btnConfirmarProductos)

                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    val adapter = AddProductoAdapter(productos)
                    recyclerView.adapter = adapter

                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()

                    btnConfirmar.setOnClickListener {
                        val cambios = adapter.obtenerCambios()
                        if (cambios.isEmpty()) {
                            Toast.makeText(requireContext(), "No hay cambios", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        var pendientes = cambios.size
                        var algunoFalló = false

                        for ((productId, cantidad) in cambios) {
                            productsRepository.addToCollection(productId, nombreColeccion, cantidad) { success ->
                                requireActivity().runOnUiThread {
                                    if (!success) algunoFalló = true
                                    pendientes--

                                    if (pendientes == 0) {
                                        dialog.dismiss()
                                        if (algunoFalló) {
                                            Toast.makeText(requireContext(), "Hubo errores al añadir productos", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(requireContext(), "Productos añadidos", Toast.LENGTH_SHORT).show()
                                        }

                                        // Refrescamos los productos
                                        collectionsRepository.getProductsInCollection(nombreColeccion) { nuevosProductos ->
                                            requireActivity().runOnUiThread {
                                                if (nuevosProductos != null) {
                                                    onProductosActualizados(nuevosProductos)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    dialog.show()
                } else {
                    Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun mostrarDialogoCrear() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_crear_coleccion, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Nueva colección")
            .setView(dialogView)
            .create()

        val inputNombre = dialogView.findViewById<EditText>(R.id.inputNombreColeccion)
        val inputDescripcion = dialogView.findViewById<EditText>(R.id.inputDescripcionColeccion)

        dialogView.findViewById<Button>(R.id.btnGuardarColeccion).setOnClickListener {
            val nombre = inputNombre.text.toString()
            val descripcion = inputDescripcion.text.toString()

            if (nombre.isNotBlank() && nombre.length < 255) {
                val nueva = JsonObject().apply {
                    addProperty("name", nombre)
                    addProperty("description", descripcion)
                }

                collectionsRepository.createCollection(nueva) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(requireContext(), "Colección creada", Toast.LENGTH_SHORT)
                                .show()
                            loadColecciones()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error. Ya existe una colección con ese nombre.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Nombre inválido o demasiado largo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoEditar(coleccion: CollectionDTO) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_crear_coleccion, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Editar colección")
            .setView(dialogView)
            .create()

        val inputNombre = dialogView.findViewById<EditText>(R.id.inputNombreColeccion)
        val inputDescripcion = dialogView.findViewById<EditText>(R.id.inputDescripcionColeccion)

        inputNombre.setText(coleccion.name)
        inputDescripcion.setText(coleccion.description)

        dialogView.findViewById<Button>(R.id.btnGuardarColeccion).setOnClickListener {
            val nombre = inputNombre.text.toString()
            val descripcion = inputDescripcion.text.toString()

            if (nombre.isNotBlank() && nombre.length < 255) {
                val json = JsonObject().apply {
                    addProperty("id", coleccion.id)
                    addProperty("name", nombre)
                    addProperty("description", descripcion)
                }

                collectionsRepository.updateCollection(json) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(
                                requireContext(),
                                "Colección actualizada",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadColecciones()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al actualizar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Nombre inválido o demasiado largo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun eliminarColeccion(coleccion: CollectionDTO) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar colección")
            .setMessage("¿Estás seguro de que quieres eliminar \"${coleccion.name}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                collectionsRepository.deleteCollection(coleccion.name) { success ->
                    requireActivity().runOnUiThread {
                        if (success) {
                            Toast.makeText(
                                requireContext(),
                                "Colección eliminada",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadColecciones()
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
