package com.example.ai36.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.view.WindowCompat
import com.example.ai36.model.UserModel
import com.example.ai36.repository.UserRepositoryImpl
import com.example.ai36.viewmodel.UserViewModel
import androidx.compose.ui.text.font.FontWeight

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Modern Edge-to-Edge support
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            RegistrationBody()
        }
    }
}

@Composable
fun RegistrationBody() {
    val repo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repo) }

    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("Select Country") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val countries = listOf("Nepal", "India", "China")

    // 🌞 Light brownish-yellow gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF5E1), Color(0xFFFAD689))
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Register Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Card container for input fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)), // soft cream
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Country dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCountry,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size.toSize()
                                }
                                .clickable { expanded = true },
                            label = { Text("Country") },
                            enabled = false,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            colors = TextFieldDefaults.colors(
                                disabledContainerColor = Color(0xFFFFF8E7),
                                disabledIndicatorColor = Color.Gray
                            )
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                        ) {
                            countries.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text(country) },
                                    onClick = {
                                        selectedCountry = country
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            userViewModel.register(email, password) { success, message, userId ->
                                if (success) {
                                    val model = UserModel(
                                        userId = userId,
                                        firstName = firstName,
                                        lastName = lastName,
                                        country = selectedCountry,
                                        email = email
                                    )
                                    userViewModel.addUserToDatabase(userId, model) { successDb, dbMessage ->
                                        Toast.makeText(context, dbMessage, Toast.LENGTH_SHORT).show()
                                        if (successDb) {
                                            (context as? ComponentActivity)?.finish()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAD689))
                    ) {
                        Text("Register", color = Color(0xFF8B4513), fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Already have an account? Login",
                        color = Color(0xFF8B4513),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                context.startActivity(Intent(context, LoginActivity::class.java))
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    RegistrationBody()
}