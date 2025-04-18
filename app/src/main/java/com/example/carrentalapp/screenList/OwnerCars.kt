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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.carrentalapp.Car
import com.example.carrentalapp.Constant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson

val db = FirebaseFirestore.getInstance()
var isPosting  = false


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCars(navController: NavHostController) {
    val cars = remember { mutableStateOf<List<Car>>(emptyList()) }
    var listenerRegistration: ListenerRegistration? = null
    LaunchedEffect(navController.currentBackStackEntry) {
        isPosting = false
    }

    LaunchedEffect(Unit) {
        val username = Constant.ownerName
        if(username == null ) return@LaunchedEffect
        listenerRegistration =
            db.collection("cars")
                .whereEqualTo("ownerName",username)
                .addSnapshotListener{snapshot,e ->
                    if(e != null){
                        Log.w("homeScreen","Listen failed",e)
                        return@addSnapshotListener
                    }
                    if(snapshot!=null && !snapshot.isEmpty){
                        var carList = snapshot.documents.map { document ->
                            Car(
                                id = document.id,
                                name = document.getString("carName") ?:"",
                                ownerName = document.getString("ownerName") ?: "", // ✅ Change this
                                seats = document.getString("seats") ?: "",
                                fuelMode = document.getString("fuelMode") ?: "",
                                contactInfo = document.getString("contactInfo") ?: "",
                                rate = document.getString("rate") ?: ""
                            )

                        }
                        cars.value = carList
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
                        CarCard(
                            car,
                            navController = navController,
                        )
                    }
                }
            }
            }
            if (!isPosting) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("addCarScreen")
                        isPosting = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "")
                }
            }
        }
    }
}

@Composable
fun CarCard(car: Car, navController: NavHostController) {
    Card(
       modifier = Modifier
           .padding(16.dp)
           .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.LightGray,Color.White,Color.White,Color.White,Color.White,Color.White,Color.White,Color.White),
                    start = Offset(x = Float.POSITIVE_INFINITY, y = 0f),  // Top-Right
                    end = Offset(x = 0f, y = Float.POSITIVE_INFINITY)  // Bottom-Left
                ),
                shape = RoundedCornerShape(8.dp)
            ))
        {


        Box(modifier = Modifier.fillMaxSize())
        {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
            {
                Column(
                ) {
                    Row(
                    ) {
                        editPost(navController, modifier = Modifier, car)
                    }

                }
            }

        }
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
                       // Text(text = "owner:${car.ownerName}")
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
                            val carJson = Uri.encode(Gson().toJson(car)) // ✅ Convert Car to JSON
                            navController.navigate("carDetail/$carJson")
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("View Details")
                    }

                    Spacer(modifier = Modifier.size(16.dp))


                    // editPost(navController, modifier = Modifier.weight(0.3f), car)

                    deletePost(car, modifier = Modifier.padding(8.dp))


                }
            }
        }

    }
    }
}

@Composable
fun editPost(navController: NavController,modifier: Modifier,car: Car)
{
    IconButton(
        onClick = {
            navController.navigate("editCarScreen/${car.id}")
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
    }
}

@Composable
fun deletePost(car: Car, modifier: Modifier)
{

    IconButton(
        onClick = {
            deleteCarFromFirestore(car.id)
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black)
    }
}
fun deleteCarFromFirestore(carId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("cars").document(carId)
        .delete()
        .addOnSuccessListener {
            Log.d("DeleteCar", "Car successfully deleted!")
        }
        .addOnFailureListener { e ->
            Log.e("DeleteCar", "Error deleting car", e)
        }
}

fun validateIndianPhoneNumber(number: String): Boolean {
    val regex = Regex("^[6-9]\\d{9}$") // ✅ Starts with 6-9, followed by 9 digits
    return number.matches(regex)
}

@Composable
fun AddCarScreen(navController: NavHostController) {
    var carName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var fuelMode by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }



    // UI for adding car details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Car Details", fontSize = 35.sp)
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
        Spacer(modifier = Modifier.height(16.dp))




        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val carDetails = hashMapOf(
                    "carName" to carName,
                    "ownerName" to Constant.ownerName,
                    "seats" to seats,
                    "fuelMode" to fuelMode,
                    "contactInfo" to contactInfo,
                    "rate" to rate,
                       )

                db.collection("cars")
                    .add(carDetails)
                    .addOnSuccessListener {
                        navController.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddCarScreen", "Error adding car details", e)
                    }
            },
       enabled = isValid,
            modifier = Modifier.fillMaxWidth(),

        ) {
            Text("Post Car")
        }
    }
}



