package com.example.ai36.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ai36.repository.ProductRepositoryImpl
import com.example.ai36.viewmodel.ProductViewModel

class UpdateProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UpdateProductBody()
        }
    }
}

@Composable
fun UpdateProductBody() {

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val repo = remember { ProductRepositoryImpl() }
    val viewmodel = remember { ProductViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    val productId: String? = activity?.intent?.getStringExtra("productId")

    val products = viewmodel.product.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        viewmodel.getProductById(productId.toString())
    }

    name = products.value?.productName ?: ""
    price = products.value?.productPrice?.toString() ?: ""
    description = products.value?.productDescription ?: ""

    Scaffold(
        containerColor = Color(0xFFFDF6EC) // Light cream background
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
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
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Product Price") },
                    modifier = Modifier.fillMaxWidth(),
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
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    modifier = Modifier.fillMaxWidth(),
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
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Button(
                    onClick = {

                        val data = mutableMapOf<String, Any?>()
                        data["productDesc"] = description
                        data["productPrice"] = price.toDoubleOrNull() ?: 0.0
                        data["productName"] = name
                        data["productId"] = productId

                        viewmodel.updateProduct(
                            productId.toString(),
                            data
                        ) { success, message ->

                            if (success) {
                                activity?.finish()
                            } else {
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4513),
                        contentColor = Color.White
                    )
                ) {
                    Text("Update Product")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdateProductBody() {
    UpdateProductBody()
}