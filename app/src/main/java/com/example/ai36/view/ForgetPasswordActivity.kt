package com.example.ai36.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ai36.repository.UserRepositoryImpl
import com.example.ai36.viewmodel.UserViewModel

class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgetPasswordBody()
        }
    }
}

@Composable
fun ForgetPasswordBody() {
    val repo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repo) }

    val context = LocalContext.current
    val activity = context as Activity

    var email by remember { mutableStateOf("") }

    // Gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF5E1), Color(0xFFFAD689))
    )

    Scaffold(containerColor = Color.Transparent) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("abc@gmail.com", color = Color(0xFF8B4513)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF8B4513),
                        unfocusedIndicatorColor = Color(0xFFD9B382),
                        cursorColor = Color(0xFF8B4513),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Button(
                    onClick = {
                        userViewModel.forgetPassword(email) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            if (success) activity.finish()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
                ) {
                    Text("Submit", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgetPassword() {
    ForgetPasswordBody()
}