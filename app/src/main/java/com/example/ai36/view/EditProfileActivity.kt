package com.example.ai36.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.example.ai36.R
import com.example.ai36.Utils.ImageUtils
import com.example.ai36.repository.ProductRepositoryImpl
import com.example.ai36.viewmodel.ProductViewModel
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.TextFieldDefaults

class EditProfileActivity : ComponentActivity() {
    private lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        imageUtils = ImageUtils(this, this)

        setContent {
            var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

            LaunchedEffect(Unit) {
                imageUtils.registerLaunchers { uri -> selectedImageUri = uri }
            }

            EditProfileScreen(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() },
                onRemoveImage = { selectedImageUri = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var showImageOptionsMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity
    val currentUser = Firebase.auth.currentUser

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    LaunchedEffect(Unit) {
        currentUser?.let {
            name = it.displayName ?: ""
            email = it.email ?: ""
        }
    }

    // Gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF5E1), Color(0xFFFAD689))
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF8B4513))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showImageOptionsMenu = true }
                        .background(Color(0xFFD9B382), shape = CircleShape)
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val photoUrl = currentUser?.photoUrl
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.profilepicplaceholder),
                                contentDescription = "Default Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showImageOptionsMenu,
                        onDismissRequest = { showImageOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Choose from Gallery", color = Color(0xFF8B4513)) },
                            onClick = {
                                showImageOptionsMenu = false
                                onPickImage()
                            }
                        )
                        if (selectedImageUri != null) {
                            DropdownMenuItem(
                                text = { Text("Remove Photo", color = Color(0xFF8B4513)) },
                                onClick = {
                                    showImageOptionsMenu = false
                                    onRemoveImage()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Text Fields
                ProfileTextField(name, { name = it }, "Full Name")
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(email, { email = it }, "Email Address", KeyboardType.Email)
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(phone, { phone = it }, "Phone Number", KeyboardType.Phone)

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { showPasswordDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Change Password", color = Color(0xFF8B4513))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentUser != null) {
                            val updateProfile: (String?) -> Unit = { photoUrl ->
                                val request = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .apply { if (photoUrl != null) setPhotoUri(Uri.parse(photoUrl)) }
                                    .build()

                                currentUser.updateProfile(request)
                                    .addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            currentUser.reload().addOnCompleteListener {
                                                currentUser.updateEmail(email).addOnCompleteListener { emailTask ->
                                                    if (emailTask.isSuccessful) {
                                                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                                        activity?.finish()
                                                    } else {
                                                        Toast.makeText(context, "Failed to update email", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }

                            if (selectedImageUri != null) {
                                viewModel.uploadImage(context, selectedImageUri) { imageUrl ->
                                    if (imageUrl != null) updateProfile(imageUrl)
                                    else Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                }
                            } else updateProfile(null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            }

            // Password Dialog
            if (showPasswordDialog) {
                AlertDialog(
                    onDismissRequest = { showPasswordDialog = false },
                    title = { Text("Change Password", color = Color(0xFF8B4513)) },
                    text = {
                        Column {
                            ProfileTextField(newPassword, { newPassword = it; passwordError = null }, "New Password", KeyboardType.Password)
                            Spacer(modifier = Modifier.height(8.dp))
                            ProfileTextField(confirmPassword, { confirmPassword = it; passwordError = null }, "Confirm Password", KeyboardType.Password)
                            if (passwordError != null) {
                                Text(passwordError ?: "", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (newPassword.isBlank() || confirmPassword.isBlank()) {
                                passwordError = "Password fields cannot be empty"
                                return@TextButton
                            }
                            if (newPassword != confirmPassword) {
                                passwordError = "Passwords do not match"
                                return@TextButton
                            }
                            val user = Firebase.auth.currentUser
                            user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    showPasswordDialog = false
                                    newPassword = ""
                                    confirmPassword = ""
                                } else {
                                    Toast.makeText(context, "Failed to change password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }) {
                            Text("Change", color = Color(0xFF8B4513))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showPasswordDialog = false
                            newPassword = ""
                            confirmPassword = ""
                            passwordError = null
                        }) {
                            Text("Cancel", color = Color(0xFF8B4513))
                        }
                    }
                )
            }
        }
    }
}

// Small reusable text field composable
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {


}