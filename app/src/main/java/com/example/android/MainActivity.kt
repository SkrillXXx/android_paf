package com.example.android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.AnyRes
import androidx.annotation.AnyThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.ExternalAuthAction
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.admin.AdminUserBuilder
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.supabaseJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Any
import io.github.jan.supabase.auth.OtpType.Email as email
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle as supabaseComposeAuthComposableRememberSignInWithGoogle
import kotlinx.coroutines.withContext as withContext1
import kotlin.Any as KotlinAny

@Suppress("IMPLICIT_CAST_TO_ANY")
class MainActivity : AppCompatActivity() {
    private lateinit var auth: Any
    private lateinit var supabase: SupabaseClient
    private lateinit var login: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var buttonShowPassword: Button
    private lateinit var buttonShowConfirmPassword: Button
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private lateinit var password: EditText
    private lateinit var conPassword: EditText
    private lateinit var sign: Button
    private lateinit var gogl: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        // Создание клиента Supabase
        supabase = createSupabaseClient(
            supabaseUrl = "https://aoalhopgqgxxatdyjnfi.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYWxob3BncWd4eGF0ZHlqbmZpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzAyNzI0ODEsImV4cCI6MjA0NTg0ODQ4MX0.Gdt5L08dGPXWtHfS_wzt7ctuQuIqeqYKZn6cMsTRHww"
        ) {
            install(Auth)
            install(Postgrest)
            install(ComposeAuth){
                googleNativeLogin("396611084298-bse5a7v2uaes6qi8lfh31l4qags68kup.apps.googleusercontent.com")
            }
        }




        // Инициализация полей ввода и кнопки
        login = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        email = findViewById(R.id.email)
        password = findViewById(R.id.pass)
        conPassword = findViewById(R.id.conpass)
        sign = findViewById(R.id.sign)
        buttonShowPassword = findViewById(R.id.togglePasswordVisibilityButton)
        buttonShowConfirmPassword = findViewById(R.id.toggleConfirmPasswordVisibilityButton2)
        gogl = findViewById(R.id.imageView)



        gogl.setOnClickListener(){
            CoroutineScope(Dispatchers.Main).launch {
                val auth = supabase.auth.signInWith(Google)
                }
            }



        findViewById<TextView>(R.id.Sign_in).setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFieldsForEmptyValues()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        login.addTextChangedListener(textWatcher)
        phone.addTextChangedListener(textWatcher)
        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
        conPassword.addTextChangedListener(textWatcher)

        buttonShowPassword.setOnClickListener { togglePasswordVisibility() }
        buttonShowConfirmPassword.setOnClickListener { toggleConfirmPasswordVisibility() }

        sign.setOnClickListener {
            val userLogin = login.text.toString().trim()
            val userPhone = phone.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val pass = password.text.toString().trim()
            val conPass = conPassword.text.toString().trim()

            val errorMessage = when {
                userLogin.isEmpty() || userPhone.isEmpty() || userEmail.isEmpty() || pass.isEmpty() || conPass.isEmpty() -> {
                    "Не все поля заполнены"
                }

                pass != conPass -> {
                    "Пароли не совпадают"
                }

                !userEmail.endsWith("@mail.ru") -> {
                    "Ошибка регистрации, неправильно введена почта(@mail.ru)."
                }

                pass.length < 6 -> {
                    "Пароль слишком малень"
                }

                else -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val user = supabase.auth.signUpWith(provider = Email) {
                            email = userEmail
                            password = pass
                        }
                    }
                    Toast.makeText(this,"Успешная регистрация", Toast.LENGTH_SHORT).show()

                    null
                }
            }
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // Очищаем поля ввода
            login.text.clear()
            phone.text.clear()
            email.text.clear()
            password.text.clear()
            conPassword.text.clear()
            startActivity(Intent(this, AuthActivity::class.java))

        }

        sign.isEnabled = false
        sign.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }
    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        password.apply {
            inputType = if (isPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSelection(length())
        }
        buttonShowPassword.text = if (isPasswordVisible) "Hide" else "Show"
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        conPassword.apply {
            inputType = if (isConfirmPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSelection(length())
        }
        buttonShowConfirmPassword.text = if (isConfirmPasswordVisible) "Hide" else "Show"
    }

    private fun checkFieldsForEmptyValues() {
        val isEmailFilled = email.text.isNotEmpty()
        val isPasswordFilled = password.text.isNotEmpty()
        val isLoginFilled = login.text.isNotEmpty()
        val isPhoneFilled = phone.text.isNotEmpty()
        val isConfirmPasswordFilled = conPassword.text.isNotEmpty()

        sign.isEnabled = isEmailFilled && isPasswordFilled && isLoginFilled && isPhoneFilled && isConfirmPasswordFilled
        sign.setBackgroundColor(
            if (sign.isEnabled) {
                resources.getColor(android.R.color.holo_blue_light) // Цвет кнопки при доступности
            } else {
                resources.getColor(android.R.color.darker_gray) // Цвет кнопки при недоступности
            }
        )

    }







}









