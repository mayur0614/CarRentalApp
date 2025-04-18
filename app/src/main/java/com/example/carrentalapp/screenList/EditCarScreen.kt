package com.example.carrentalapp.screenList

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.carrentalapp.Constant
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditCarScreen(navController: NavHostController, carId: String) {
    var carName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var fuelMode by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    var isValid by remember { mutableStateOf(true) }

    // Fetch the car details
    LaunchedEffect(carId) {
        db.collection("cars").document(carId).get()
            .addOnSuccessListener { document ->
                carName = document.getString("carName") ?: ""
                ownerName = document.getString("ownerName") ?: ""
                seats = document.getString("seats") ?: ""
                fuelMode = document.getString("fuelMode") ?: ""
                contactInfo = document.getString("contactInfo") ?: ""
                rate = document.getString("rate") ?: ""
            }
            .addOnFailureListener { e ->
                Log.e("EditCarScreen", "Error fetching car details", e)
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Car Details", fontSize = 30.sp)

        OutlinedTextField(
            value = carName,
            onValueChange = { carName = it },
            label = { Text("Car Name") },
            leadingIcon = { Icon(Icons.Filled.DirectionsCar, contentDescription = "Fuel") },
            modifier = Modifier.fillMaxWidth()
        )
//        Spacer(modifier = Modifier.height(8.dp))
//        OutlinedTextField(
//            value = ownerName,
//            onValueChange = { ownerName = it },
//            label = { Text("owner name ") },
//            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Fuel") },
//            modifier = Modifier.fillMaxWidth()
//        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = seats,
            onValueChange = { seats = it },
            label = { Text("Seats") },
            leadingIcon = { Icon(Icons.Filled.EventSeat, contentDescription = "Fuel") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = fuelMode,
            onValueChange = { fuelMode = it },
            label = { Text("Fuel Mode") },
            leadingIcon = { Icon(Icons.Filled.LocalGasStation, contentDescription = "Fuel") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contactInfo,
            onValueChange = {
                contactInfo = it
                if(validateIndianPhoneNumber(it))
                {
                    isValid= true
                }else{
                    isValid=false
                }
            },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
            label = {
                Text("Phone Number ")
            },
            isError = !isValid,
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = rate,
            onValueChange = { rate = it },
            label = { Text("Rate") },
            leadingIcon = { Icon(Icons.Filled.CurrencyRupee, contentDescription = "Phone") },

            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {

                val updatedCarDetails = hashMapOf(
                    "carName" to carName,
                    "ownerName" to Constant.ownerName,
                    "seats" to seats,
                    "fuelMode" to fuelMode,
                    "contactInfo" to contactInfo,
                    "rate" to rate
                )

                db.collection("cars").document(carId)
                    .update(updatedCarDetails as Map<String, Any>)
                    .addOnSuccessListener {
                        navController.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditCarScreen", "Error updating car details", e)
                    }
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Car")
        }
    }
}
