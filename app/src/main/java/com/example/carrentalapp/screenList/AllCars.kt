package com.example.carrentalapp.screenList

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.carrentalapp.Car
import com.example.carrentalapp.Constant
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCars(navController: NavHostController) {
    val cars = remember { mutableStateOf<List<Car>>(emptyList()) }
    var listenerRegistration: ListenerRegistration? = null

    LaunchedEffect(Unit) {


        listenerRegistration = db.collection("cars")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("OwnerCars", "listen failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val carList = snapshot.documents.map { document ->
                        Car(
                            id = document.id,
                            name = document.getString("carName") ?: "",
                            ownerName = document.getString("ownerName") ?: "",
                            seats = document.getString("seats") ?: "",
                            fuelMode = document.getString("fuelMode") ?: "",
                            contactInfo = document.getString("contactInfo") ?: "",
                            rate = document.getString("rate") ?: ""
                        )
                    }
                    cars.value = carList
                } else {
                    cars.value = emptyList() // No cars found
                }
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Show message if no cars exist
        if (cars.value.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()

            }
            //Text(text = "No cars found", fontSize = 18.sp, color = Color.Gray)
        } else {


            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // 2 items per row
                contentPadding = PaddingValues(8.dp)
            ) {
                items(cars.value.reversed()) { car ->
                    CarCard1(
                        car,
                        navController = navController,
                        cars
                    )
                }
            }
        }
    }
}
@Composable
fun CarCard1(car: Car, navController: NavHostController, cars :MutableState<List<Car>>) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
           )
        {



            Box(modifier = Modifier.fillMaxSize())
            {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                    ) {
                        Icon(
                            Icons.Filled.CarRental,
                            contentDescription = "",
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Column {
                            Text(
                                text = "Fuel: ${car.fuelMode}",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Seats: ${car.seats}", fontSize = 16.sp, color = Color.Gray)
                            //Text(text = "owner:${car.ownerName}")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Text(
                            text = car.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = "Contact: ${car.contactInfo}", fontSize = 16.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rate: ₹${car.rate}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Button(
                            onClick = {
                                //navController.navigate("carDetail/${car.id}")
//Log.i("carId",car.id)
                                val carJson = Uri.encode(Gson().toJson(car)) // ✅ Convert Car to JSON
                                if(!Constant.userReq){
                                    navController.navigate("carDetail/$carJson")
                                }else{
                                    navController.navigate("UserCarDetail/$carJson")
                                }

                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("View Details")
                        }

                        Spacer(modifier = Modifier.size(8.dp))


                        // editPost(navController, modifier = Modifier.weight(0.3f), car)

                     //   deletePost(car, modifier = Modifier.padding(8.dp))


                    }
                }
            }

        }
    }
}
