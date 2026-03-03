package com.example.ai36.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ai36.model.OrderModel
import com.example.ai36.repository.OrderRepositoryImpl
import com.example.ai36.ui.theme.AI36Theme
import com.example.ai36.viewmodel.OrderViewModel
import com.example.ai36.viewmodel.OrderViewModelFactory

class OrderActivity : ComponentActivity() {

    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderRepo = OrderRepositoryImpl()
        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel =
            ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        orderViewModel.loadAllOrders()

        setContent {
            AI36Theme {
                OrderScreen(orderViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(orderViewModel: OrderViewModel) {

    val orders by orderViewModel.orders.observeAsState(emptyList())
    val error by orderViewModel.error.observeAsState()

    Scaffold(
        containerColor = Color(0xFFFDF6EC), // Light background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Orders",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B4513) // Brown
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            if (!error.isNullOrEmpty()) {
                Text(
                    text = error ?: "",
                    color = Color.Red
                )
            }

            if (orders.isEmpty()) {
                Text(
                    "No orders found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF8B4513)
                )
            } else {
                LazyColumn {
                    items(orders) { order ->
                        OrderCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Order ID: ${order.orderId}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF8B4513)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Status: ${order.orderStatus}",
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: Rs. ${order.totalAmount}",
                color = Color(0xFF8B4513)
            )
        }
    }
}