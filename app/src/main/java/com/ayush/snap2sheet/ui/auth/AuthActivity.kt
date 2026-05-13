package com.ayush.snap2sheet.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayush.snap2sheet.MainActivity
import com.ayush.snap2sheet.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // If already logged in, skip to main
        if (auth.currentUser != null) {
            navigateToMain()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener { handleSubmit() }
        binding.tvToggle.setOnClickListener { toggleMode() }
    }

    private fun toggleMode() {
        isLoginMode = !isLoginMode
        if (isLoginMode) {
            binding.tvFormTitle.text = "Login"
            binding.btnSubmit.text = "Login"
            binding.tvToggle.text = "Don't have an account? Sign Up"
            binding.tilName.visibility = View.GONE
        } else {
            binding.tvFormTitle.text = "Sign Up"
            binding.btnSubmit.text = "Create Account"
            binding.tvToggle.text = "Already have an account? Login"
            binding.tilName.visibility = View.VISIBLE
        }
        binding.tvError.visibility = View.GONE
    }

    private fun handleSubmit() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isBlank() || password.isBlank()) {
            showError("Please fill in all fields")
            return
        }

        if (!isLoginMode) {
            val name = binding.etName.text.toString().trim()
            if (name.isBlank()) {
                showError("Please enter your name")
                return
            }
        }

        if (password.length < 6) {
            showError("Password must be at least 6 characters")
            return
        }

        setLoading(true)

        if (isLoginMode) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    navigateToMain()
                }
                .addOnFailureListener { e ->
                    setLoading(false)
                    showError(e.localizedMessage ?: "Login failed")
                }
        } else {
            val name = binding.etName.text.toString().trim()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    // Set display name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    result.user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener {
                            navigateToMain()
                        }
                }
                .addOnFailureListener { e ->
                    setLoading(false)
                    showError(e.localizedMessage ?: "Sign up failed")
                }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSubmit.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        binding.etEmail.isEnabled = !loading
        binding.etPassword.isEnabled = !loading
        binding.etName.isEnabled = !loading
        binding.tvToggle.isEnabled = !loading
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
}
