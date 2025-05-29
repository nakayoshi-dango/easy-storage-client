package com.example.easy_storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.easy_storage.api.auth.AuthRepository
import com.example.easy_storage.api.RetrofitInstance
import androidx.core.content.edit

class LoginRegisterFragment : Fragment() {

    private lateinit var tvTitle: TextView
    private lateinit var etIp: EditText
    private lateinit var etPort: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnToggleMode: Button
    var isExpanded = false
    private var isLoginMode = true
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tvTitle = view.findViewById(R.id.tvTitle)
        etIp = view.findViewById(R.id.etIp)
        etPort = view.findViewById(R.id.etPort)
        etUsername = view.findViewById(R.id.etUsername)
        etPassword = view.findViewById(R.id.etPassword)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnToggleMode = view.findViewById(R.id.btnToggleMode)

        updateUI()

        val sharedPref =
            requireActivity().getSharedPreferences("config", android.content.Context.MODE_PRIVATE)
        val savedIp = sharedPref.getString("ip", "")
        val savedPort = sharedPref.getString("port", "")

        etIp.setText(savedIp)
        etPort.setText(savedPort)

        val header = view.findViewById<LinearLayout>(R.id.header)
        val contenido = view.findViewById<LinearLayout>(R.id.contenidoOculto)
        val arrow = view.findViewById<ImageView>(R.id.arrow)

        header.setOnClickListener {
            isExpanded = !isExpanded
            contenido.visibility = if (isExpanded) View.VISIBLE else View.GONE
            arrow.setImageResource(if (isExpanded) R.drawable.ic_drop_up else R.drawable.ic_drop_down)
        }

        btnToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        btnSubmit.setOnClickListener {
            val ip = etIp.text.toString().trim()
            val port = etPort.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (ip.isEmpty() || port.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce IP y puerto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitInstance.init(ip, port)

            authRepository = AuthRepository(RetrofitInstance.authApi)

            if (isLoginMode) {
                authRepository.login(username, password) { success ->
                    if (success) {
                        val sharedPref = requireActivity().getSharedPreferences(
                            "config",
                            android.content.Context.MODE_PRIVATE
                        )
                        sharedPref.edit() {
                            putString("ip", ip)
                                .putString("port", port)
                        }
                        Toast.makeText(requireContext(), "Sesión iniciada", Toast.LENGTH_SHORT)
                            .show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, ProductosFragment())
                            .commit()
                        (activity as? MainActivity)?.onLoginSuccess()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                authRepository.register(username, password) { success ->
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "Registrado correctamente. Inicia sesión.",
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoginMode = true
                        updateUI()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error: usuario ya existe",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        if (isLoginMode) {
            tvTitle.text = "Iniciar sesión"
            btnSubmit.text = "Entrar"
            btnToggleMode.text = "¿No tienes cuenta? Regístrate"
        } else {
            tvTitle.text = "Registrarse"
            btnSubmit.text = "Registrarse"
            btnToggleMode.text = "¿Ya tienes cuenta? Inicia sesión"
        }
    }
}
