package com.example.ai36.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ai36.model.CartItemModel
import com.example.ai36.model.WishlistItemModel
import com.example.ai36.repository.CartRepositoryImpl
import com.example.ai36.repository.WishlistRepositoryImpl
import com.example.ai36.viewmodel.CartViewModel
import com.example.ai36.viewmodel.CartViewModelFactory
import com.example.ai36.viewmodel.WishlistViewModel
import com.example.ai36.viewmodel.WishlistViewModelFactory
import androidx.compose.foundation.shape.RoundedCornerShape

class WishlistActivity : ComponentActivity() {
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize WishlistViewModel
        wishlistViewModel = ViewModelProvider(
            this,
            WishlistViewModelFactory(WishlistRepositoryImpl)
        )[WishlistViewModel::class.java]

        // Initialize CartViewModel
        cartViewModel = ViewModelProvider(
            this,
            CartViewModelFactory(CartRepositoryImpl())
        )[CartViewModel::class.java]

        setContent {
            WishlistScreen(wishlistViewModel, cartViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val wishlistItems by wishlistViewModel.wishlistItems.collectAsState(initial = emptyList())

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF5E1), Color(0xFFFAD689))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Wishlist", fontSize = 20.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF8B4513))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {
            if (wishlistItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Your wishlist is empty.",
                        color = Color(0xFF8B4513),
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(wishlistItems) { item ->
                        WishlistItemCard(
                            item = item,
                            onRemove = {
                                wishlistViewModel.removeFromWishlist(item)
                                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                            },
                            onAddToCart = {
                                val cartItem = CartItemModel(
                                    id = "", // Generate ID if needed
                                    productName = item.productName,
                                    productPrice = item.productPrice,
                                    image = item.image,
                                    quantity = 1
                                )
                                cartViewModel.addToCart(cartItem)
                                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    item: WishlistItemModel,
    onRemove: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.productName, fontSize = 18.sp, color = Color.Black)
            Text(text = "Price: Rs. ${item.productPrice}", color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
                ) {
                    Text("Add to Cart", color = Color.White)
                }
                Button(
                    onClick = onRemove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Remove", color = Color.White)
                }
            }
        }
    }
}