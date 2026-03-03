package com.example.ai36.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext



import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai36.model.AddressModel
import com.example.ai36.repository.AddressRepositoryImpl
import com.example.ai36.viewmodel.AddressViewModel
import java.util.*

class AddressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddressScreen(userId = "demo_user_id") // Replace with actual userId
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(userId: String) {
    val viewModel = remember { AddressViewModel(AddressRepositoryImpl()) }
    val context = LocalContext.current

    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var addresses by remember { mutableStateOf(emptyList<AddressModel>()) }

    fun loadAddresses() {
        viewModel.getAddresses(userId) {
            addresses = it
        }
    }

    LaunchedEffect(Unit) { loadAddresses() }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF5E1), Color(0xFFFAD689))
    )
    val skillLinkBrown = Color(0xFF8B4513)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skill Link - Addresses", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = skillLinkBrown)
            )
        },
        modifier = Modifier.background(gradient)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                text = if (selectedId == null) "Add Address" else "Update Address",
                fontSize = 22.sp,
                color = skillLinkBrown
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input Fields
            OutlinedTextField(
                value = addressLine,
                onValueChange = { addressLine = it },
                label = { Text("Address Line") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = skillLinkBrown,
                    cursorColor = skillLinkBrown
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = skillLinkBrown,
                    cursorColor = skillLinkBrown
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = skillLinkBrown,
                    cursorColor = skillLinkBrown
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Postal Code") },

                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = skillLinkBrown,
                    cursorColor = skillLinkBrown
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val id = selectedId ?: UUID.randomUUID().toString()
                    val address = AddressModel(
                        id = id,
                        userId = userId,
                        street = addressLine,
                        city = city,
                        district = district,
                        postalCode = postalCode
                    )
                    val action = if (selectedId == null) viewModel::addAddress else viewModel::updateAddress
                    action(address) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            addressLine = ""
                            city = ""
                            district = ""
                            postalCode = ""
                            selectedId = null
                            loadAddresses()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = skillLinkBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (selectedId == null) "Save Address" else "Update Address", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Saved Addresses",
                fontSize = 18.sp,
                color = skillLinkBrown
            )

            Spacer(modifier = Modifier.height(12.dp))

            addresses.forEach { address ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Address: ${address.street}")
                        Text("City: ${address.city}")
                        Text("District: ${address.district}")
                        Text("Postal Code: ${address.postalCode}")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                "Edit",
                                color = skillLinkBrown,
                                modifier = Modifier.clickable {
                                    selectedId = address.id
                                    addressLine = address.street
                                    city = address.city
                                    district = address.district
                                    postalCode = address.postalCode
                                }
                            )
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable {
                                    viewModel.deleteAddress(address.id) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (success) loadAddresses()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}