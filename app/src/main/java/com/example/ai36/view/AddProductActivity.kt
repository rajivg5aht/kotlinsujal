package com.example.ai36.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.example.ai36.R
import com.example.ai36.Utils.ImageUtils
import com.example.ai36.model.ProductModel
import com.example.ai36.repository.ProductRepositoryImpl
import com.example.ai36.viewmodel.ProductViewModel

class AddProductActivity : ComponentActivity() {

    private var selectedImageUri: Uri? = null
    lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
            setContent {
                AddProductBody(
                    selectedImageUri = selectedImageUri,
                    onPickImage = { imageUtils.launchImagePicker() }
                )
            }
        }

        setContent {
            AddProductBody(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@Composable
fun AddProductBody(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {

    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            item {

                // Image Picker Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onPickImage() }
                        .padding(10.dp)
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.imgplchol),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product Name
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Product Name") },
                    value = productName,
                    onValueChange = { productName = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF8B4513),
                        unfocusedIndicatorColor = Color(0xFFD9B382),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF8B4513)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Product Description
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Product Description") },
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF8B4513),
                        unfocusedIndicatorColor = Color(0xFFD9B382),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF8B4513)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Product Price
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Product Price") },
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF8B4513),
                        unfocusedIndicatorColor = Color(0xFFD9B382),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF8B4513)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (selectedImageUri != null) {
                            viewModel.uploadImage(context, selectedImageUri) { imageUrl ->
                                if (imageUrl != null) {
                                    val model = ProductModel(
                                        "",
                                        productName,
                                        productPrice.toDoubleOrNull() ?: 0.0,
                                        productDescription,
                                        imageUrl
                                    )
                                    viewModel.addProduct(model) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        if (success) activity?.finish()
                                    }
                                } else {
                                    Log.e("Upload Error", "Failed to upload image")
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Please select an image first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4513),
                        contentColor = Color.White
                    )
                ) {
                    Text("Submit")
                }
            }
        }
    }
}


