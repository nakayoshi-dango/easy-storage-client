package com.example.easy_storage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.easy_storage.api.auth.AuthRepository
import com.example.easy_storage.api.RetrofitInstance
import com.example.easy_storage.api.auth.TokenManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var authRepository: AuthRepository
    private var logged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.topAppBar)
        bottomNav = findViewById(R.id.bottomNav)

        // Inicialmente, no hacemos nada hasta saber si está logueado o no
        tokenHandle()
    }

    private fun setupBottomNavListener() {
        bottomNav.setOnItemSelectedListener { item ->
            if (!logged) {
                false
            } else {
                when (item.itemId) {
                    R.id.nav_productos -> {
                        toolbar.title = getString(R.string.productos)
                        loadFragment(ProductosFragment())
                        true
                    }

                    R.id.nav_usuario -> {
                        toolbar.title = getString(R.string.usuarios)
                        loadFragment(UsuarioFragment(0))
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun tokenHandle() {
        val sharedPref =
            this.getSharedPreferences("config", android.content.Context.MODE_PRIVATE)
        val savedIp = sharedPref.getString("ip", "")
        val savedPort = sharedPref.getString("port", "")
        if (!savedIp.isNullOrBlank() && !savedPort.isNullOrBlank()) {
            RetrofitInstance.init(
                savedIp.toString(),
                savedPort.toString()
            )
        }
        if (RetrofitInstance.isInit()) {
            authRepository = AuthRepository(RetrofitInstance.authApi)
            authRepository.validateToken { isValid ->
                if (isValid) {
                    logged = true
                    setupBottomNavListener()
                    loadFragment(ProductosFragment())
                    toolbar.title = getString(R.string.productos)
                } else {
                    authenticate()
                }
            }
        } else {
            authenticate()
        }
    }

    private fun authenticate() {
        logged = false
        setupBottomNavListener()
        TokenManager.clearToken()
        loadFragment(LoginRegisterFragment())
        toolbar.title = "Autenticación"
    }

    fun onLoginSuccess() {
        logged = true
        setupBottomNavListener()
        toolbar.title = getString(R.string.productos)
        bottomNav.selectedItemId = R.id.nav_productos
        loadFragment(ProductosFragment())
    }

}
