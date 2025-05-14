package com.example.easy_storage

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.easy_storage.databinding.FragmentUsuarioBinding

class UsuarioFragment : Fragment() {

    private var _binding: FragmentUsuarioBinding? = null
    private val binding get() = _binding!!

    // Aquí puedes cargar los datos reales del usuario
    private val urlImagen = "https://cdn.discordapp.com/attachments/1371880792808099921/1372205572161671269/munequito.png?ex=6825ed86&is=68249c06&hm=4cc739945363720af26b485be617cf6549f0d6760f0b7a946574ee7732335d0d&";
    private val nombreUsuario = "Juan Pérez"
    private val rolUsuario = "Administrador"
    private val productosGuardados = 17
    private val fechaCreacion = "2024-01-15"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar datos del usuario
        binding.txtNombre.text = nombreUsuario
        binding.txtRol.text = "Rol: $rolUsuario"
        binding.txtProductos.text = "Productos guardados: $productosGuardados"
        binding.txtFecha.text = "Fecha de creación: $fechaCreacion"

        // Imagen de perfil opcional (por ejemplo con Glide)
        if (isValidUrl(urlImagen)) {
            Glide.with(this).load(urlImagen).into(binding.imgPerfil)
        } else {
            binding.imgPerfil.setImageResource(R.drawable.munequito)
        }

        // Botón ayuda
        binding.btnAyuda.setOnClickListener {
            Toast.makeText(requireContext(), "Función de ayuda aún no disponible", Toast.LENGTH_SHORT).show()
        }

        // Botón cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            // Aquí puedes llamar a tu lógica de logout, por ejemplo:
            // AuthRepository.logout()
            // startActivity(Intent(requireContext(), LoginActivity::class.java))
            // requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
