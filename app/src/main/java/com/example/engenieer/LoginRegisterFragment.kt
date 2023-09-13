package com.example.engenieer

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.engenieer.databinding.FragmentLoginRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class LoginRegisterFragment : Fragment() {

    private lateinit var binding: FragmentLoginRegisterBinding
    private var isLogin = true
    private lateinit var emailField: TextInputLayout
    private lateinit var passwordField: TextInputLayout
    private lateinit var passwordConfirmationField: TextInputLayout
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isLogin = true
        bindElements()
        setupButtons()
    }

    private fun bindElements(){
        emailField = binding.email
        passwordField = binding.password
        passwordConfirmationField = binding.passwordConfirmation
        loginButton = binding.loginButton
        registerButton = binding.registerButton
    }

    private fun setupButtons(){
        setupLoginListener()
        setupRegisterListener()
    }

    private fun setupRegisterListener() {
        registerButton.setOnClickListener {
            if (isLogin){
                loginButton.setText(R.string.register_button)
                registerButton.setText(R.string.login_button)
                passwordConfirmationField.visibility = View.VISIBLE
                isLogin = false
            }else{
                loginButton.setText(R.string.login_button)
                registerButton.setText(R.string.register_button)
                passwordConfirmationField.visibility = View.GONE
                isLogin = true
            }
        }
    }

    private fun setupLoginListener() {
        loginButton.setOnClickListener {
            if (isLogin) login()
            else register()
        }
    }

    private fun login(){
        val isValid: Boolean = validateLoginFields()
        val email: String = emailField.editText?.text.toString()
        val password: String = passwordField.editText?.text.toString()
        if(isValid){
            FirebaseHandler.Authentication.login(email,password).apply{
                addOnSuccessListener {
                    displayLoginSuccessMessage()
                    //dokonczyc logowanie
                }

                addOnFailureListener {
                    displayLoginFailureMessage()
                }
            }
        }
    }

    private fun displayLoginFailureMessage() {
        Snackbar.make(
            binding.root,
            R.string.login_failed_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun displayLoginSuccessMessage() {
        Snackbar.make(
            binding.root,
            R.string.login_success_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateLoginFields(): Boolean {
        val isEmailValid: Boolean = validateEmail()
        val isPasswordValid: Boolean = validatePasswordField()
        return isEmailValid && isPasswordValid
    }

    private fun validatePasswordField(): Boolean {
        val password: String = passwordField.editText?.text.toString()
        val passwordMinLength: Int = 6
        return password.length >= passwordMinLength
    }

    private fun register() {
        val isValid: Boolean = validateRegistrationFields()
        val email: String = emailField.editText?.text.toString()
        val password: String = passwordField.editText?.text.toString()
        if (isValid){
            FirebaseHandler.Authentication.register(email,password).apply {
                addOnSuccessListener {
                    displayRegisterSuccessMessage()
                    //dokonczyc rejestracje
                    FirebaseHandler.RealtimeDatabase.registerNewUserInDatabase()
                }

                addOnFailureListener {
                    displayRegisterFailureMessage()
                }
            }
        }
    }

    private fun displayRegisterFailureMessage() {
        Snackbar.make(
            binding.root,
            R.string.register_failed_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun displayRegisterSuccessMessage() {
        Snackbar.make(
            binding.root,
            R.string.register_success_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateRegistrationFields(): Boolean {
        val areValidPasswordFields = validatePasswordFields()
        val isValidEmail = validateEmail()
        return areValidPasswordFields && isValidEmail
    }

    private fun validateEmail(): Boolean {
        val email: String = emailField.editText?.text.toString()
        return if (email.contains("@")) Patterns.EMAIL_ADDRESS.matcher(email).matches()
                else false
    }

    private fun validatePasswordFields(): Boolean {
        val passwordString = passwordField.editText?.text.toString()
        val passwordConfirmationString = passwordConfirmationField.editText?.text.toString()
        val passwordMinLength: Int = 6
        return (passwordString == passwordConfirmationString && passwordString.length >= passwordMinLength)
    }
}