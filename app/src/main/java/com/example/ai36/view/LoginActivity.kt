package com.example.ai36.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai36.repository.UserRepositoryImpl
import com.example.ai36.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import com.example.ai36.R

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {

    val repo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repo) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🌞 Light brownish-yellow gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF5E1), // light cream
            Color(0xFFFAD689)  // soft yellow-brown
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))



            Image(
                painter = painterResource(id = R.drawable.skill),
                contentDescription = null,
                modifier = Modifier
                    .height(140.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)) // lighter cream card
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter email") },
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter password") },
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation =
                            if (passwordVisibility) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFFAD689)
                                )
                            )
                            Text("Remember me")
                        }

                        Text(
                            text = "Forgot Password?",
                            color = Color(0xFF8B4513),
                            modifier = Modifier.clickable {
                                context.startActivity(
                                    Intent(context, ForgetPasswordActivity::class.java)
                                )
                                activity.finish()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            userViewModel.login(email, password) { success, message ->
                                if (success) {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                                    if (email == "admin@gmail.com") {
                                        context.startActivity(
                                            Intent(context, DashboardActivity::class.java)
                                        )
                                    } else {
                                        context.startActivity(
                                            Intent(context, UserDashboardActivity::class.java)
                                        )
                                    }

                                    if (rememberMe) {
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.apply()
                                    }

                                    activity.finish()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Invalid login: $message")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFAD689) // soft yellow-brown
                        )
                    ) {
                        Text("Login", fontSize = 18.sp, color = Color(0xFF8B4513))
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        "Don't have an account? Signup",
                        color = Color(0xFF8B4513),
                        modifier = Modifier.clickable {
                            context.startActivity(
                                Intent(context, RegistrationActivity::class.java)
                            )
                            activity.finish()
                        }
                    )
                }
            }
        }
    }
}