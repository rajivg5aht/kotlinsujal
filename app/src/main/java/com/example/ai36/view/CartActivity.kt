package com.example.ai36.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.example.ai36.model.CartItemModel
import com.example.ai36.model.OrderModel
import com.example.ai36.repository.CartRepositoryImpl
import com.example.ai36.repository.OrderRepositoryImpl
import com.example.ai36.viewmodel.CartViewModel
import com.example.ai36.viewmodel.CartViewModelFactory
import com.example.ai36.viewmodel.OrderViewModel
import com.example.ai36.viewmodel.OrderViewModelFactory
import androidx.compose.ui.draw.clip

class CartActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepo = CartRepositoryImpl()
        val orderRepo = OrderRepositoryImpl()

        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        cartViewModel.loadCartItems()

        setContent {
            CartScreen(cartViewModel = cartViewModel, orderViewModel = orderViewModel)
        }
    }
}

// Skill Link colors
private val dashboardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFF5E1),
        Color(0xFFFAD689)
    )
)
private val cardColor = Color(0xFFFFF8E7)
private val skillLinkBrown = Color(0xFF8B4513)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel, orderViewModel: OrderViewModel) {
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val errorMessage by cartViewModel.error.observeAsState()
    val orderError by orderViewModel.error.observeAsState()
    val context = LocalContext.current

    val totalPrice = cartItems.sumOf { it.productPrice * it.quantity }

    // Show Toast for order errors or success
    LaunchedEffect(orderError) {
        orderError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            orderViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", fontSize = 20.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = skillLinkBrown)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dashboardGradient)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = { cartViewModel.updateQuantity(item.id, item.quantity + 1) },
                            onDecrease = {
                                if (item.quantity > 1) {
                                    cartViewModel.updateQuantity(item.id, item.quantity - 1)
                                }
                            },
                            onRemove = { cartViewModel.removeCartItem(item.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total:",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = skillLinkBrown
                    )
                    Text(
                        "Rs. ${"%.2f".format(totalPrice)}",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleLarge,
                        color = skillLinkBrown
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    onClick = {
                        if (cartItems.isNotEmpty()) {
                            val userId = "USR001"  // Replace with actual user ID from auth
                            val order = OrderModel(
                                orderId = "",
                                userId = userId,
                                items = cartItems,
                                totalAmount = totalPrice,
                                orderStatus = "Pending"
                            )
                            orderViewModel.placeOrder(order)
                            Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = skillLinkBrown,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed to Checkout", fontSize = 16.sp)
                }
            }
        }
    )
}

@Composable
fun CartItemCard(
    item: CartItemModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.image),
                contentDescription = item.productName,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = skillLinkBrown
                )
                Text(
                    text = "Rs. ${item.productPrice}",
                    fontSize = 14.sp,
                    color = skillLinkBrown
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(
                        onClick = onDecrease,
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = skillLinkBrown, contentColor = Color.White)
                    ) { Text("-") }

                    Text(
                        text = "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 16.sp,
                        color = skillLinkBrown
                    )

                    Button(
                        onClick = onIncrease,
                        contentPadding = PaddingValues(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = skillLinkBrown, contentColor = Color.White)
                    ) { Text("+") }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}