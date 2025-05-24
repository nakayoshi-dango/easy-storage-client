package com.example.easy_storage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.easy_storage.api.RetrofitInstance
import com.example.easy_storage.api.auth.TokenManager
import com.example.easy_storage.api.products.ProductsRepository
import com.example.easy_storage.api.users.UsersRepository
import com.example.easy_storage.databinding.FragmentUsuarioBinding
import com.example.easy_storage.models.UserDTO
import java.text.SimpleDateFormat
import java.util.*

class UsuarioFragment(productsCount: Int) : Fragment() {

    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!
    private val productsRepository = ProductsRepository(RetrofitInstance.productsApi)
    private val usersRepository = UsersRepository(RetrofitInstance.usersApi)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        usersRepository.getCurrentUser { user ->
            if (!isFragmentReady()) return@getCurrentUser

            if (user != null) {
                productsRepository.getMyProductsCount { count ->
                    if (!isFragmentReady()) return@getMyProductsCount

                    requireActivity().runOnUiThread {
                        if (!isFragmentReady()) return@runOnUiThread
                        mostrarDatosUsuario(user, count ?: 0)
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    if (!isFragmentReady()) return@runOnUiThread
                    Toast.makeText(
                        requireContext(),
                        "No se pudieron cargar los datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnChangeImg.setOnClickListener {
            val nuevaUrl = etImagenURL.text.toString()

            if (!isValidUrl(nuevaUrl)) {
                Toast.makeText(requireContext(), "URL no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            usersRepository.setProfilePicture(nuevaUrl) { exito ->
                requireActivity().runOnUiThread {
                    if (exito) {
                        Toast.makeText(requireContext(), "Imagen actualizada", Toast.LENGTH_SHORT).show()
                        recargarFragmento()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        btnAyuda.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Función de ayuda aún no disponible",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnCerrarSesion.setOnClickListener {
            TokenManager.clearToken()
            reiniciarApp()
        }
    }

    private fun isFragmentReady(): Boolean {
        return isAdded && activity != null && view != null
    }


    private fun mostrarDatosUsuario(user: UserDTO, count: Int) = with(binding) {
        txtNombre.text = user.username
        if (user.role.toString() == "ROLE_ADMIN") {
            txtRol.text = "Rol: Administrador"
        } else if (user.role.toString() == "ROLE_USER") {
            txtRol.text = "Rol: Usuario"
        }
        txtProductos.text = "Productos creados: $count"
        txtFecha.text = "Fecha de creación: ${formatearFecha(user.creationDate)}"
        etImagenURL.setText(user.imageURL.toString())

        if (isValidUrl(user.imageURL)) {
            Glide.with(this@UsuarioFragment).load(user.imageURL).into(imgPerfil)
        } else {
            imgPerfil.setImageResource(R.drawable.munequito)
        }
    }

    private fun formatearFecha(date: Date): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(date)
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


    private fun reiniciarApp() {
        val intent =
            requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo reiniciar la aplicación",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun recargarFragmento() {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .detach(this)
            .attach(this)
            .commit()
    }

}
