package com.example.carrentalapp.screenList

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.AttachMoney

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext
import com.example.carrentalapp.Car


@Composable
fun CarDetailScreen(navController: NavHostController, car: Car) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ✅ Car Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Icon(
                Icons.Filled.CarRental,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)

            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Car Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ✅ Car Name & Owner Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(car.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    // ✅ Owner Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Owner", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Owner: ${car.ownerName}", fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Car Features (Fuel, Seats, Price)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CarFeatureItem(Icons.Default.LocalGasStation, "Fuel", car.fuelMode)
                    CarFeatureItem(Icons.Default.EventSeat, "Seats", car.seats)
                    CarFeatureItem(Icons.Default.AttachMoney, "Price", "${car.rate}/day")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Contact & Booking Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${car.contactInfo}") // ✅ Open dialer with number
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(Color.Cyan),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Call")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call Owner")
            }
        }
        Button(
            onClick = { shareCarDetails(context, car) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Share, contentDescription = "Share")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Share Car Details")
        }
    }
}

fun shareCarDetails(context: Context, car: Car) {
    val shareText = """
        🚗 *Car Details* 🚗
        📌 Name: ${car.name}
        👤 Owner: ${car.ownerName}
        💺 Seats: ${car.seats}
        ⛽ Fuel: ${car.fuelMode}
        💰 Rate: ₹${car.rate}/day
        📞 Contact: ${car.contactInfo}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Car Details"))
}
// ✅ Car Feature Item (Reusable UI Component)
@Composable
fun CarFeatureItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
