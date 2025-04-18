package com.example.carrentalapp.screenList

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.carrentalapp.Car
import com.example.carrentalapp.Constant
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var carRental = remember { mutableStateOf(true) }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(carRental,navController, drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if(carRental.value){
                            Text("Car Rental")
                        }else{
                            Text("Your Cars")
                        }

                            },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Constant.userReq = false
                NavigationHost(navController)
            }
        }
    }
}




@Composable
fun DrawerContent(carRental:MutableState<Boolean>,navController: NavHostController, drawerState: DrawerState, scope: CoroutineScope) {
    ModalDrawerSheet {
        Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)

        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.DirectionsCar, contentDescription = "All Cars") },
            label = { Text("All Cars") },
            selected = navController.currentDestination?.route == "allCars",
            onClick = {
                carRental.value = true
                navController.navigate("allCars") {
                    popUpTo("allCars") { inclusive = true }
                }
                scope.launch { drawerState.close() }
            }
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.AccountBox, contentDescription = "Owner Cars") },
            label = { Text("Owner Cars") },
            selected = navController.currentDestination?.route == "ownerCars",
            onClick = {
                carRental.value = false
                navController.navigate("ownerCars") {
                    popUpTo("ownerCars") { inclusive = true }
                }
                scope.launch { drawerState.close() }
            }
        )
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "allCars") {
        composable("ownerCars") { OwnerCarsScreen(navController) }
        composable("allCars") { AllCarsScreen(navController) }

        composable("addCarScreen"){
            AddCarScreen(navController)
        }
        composable(
            route = "carDetail/{carJson}",
            arguments = listOf(navArgument("carJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val carJson = backStackEntry.arguments?.getString("carJson") ?: ""
            val car = Gson().fromJson(carJson, Car::class.java)  // ✅ Convert JSON back to Car
            CarDetailScreen(navController, car)
        }
        composable(
            route = "userCarDetail/{carJson}",
            arguments = listOf(navArgument("carJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val carJson = backStackEntry.arguments?.getString("carJson") ?: ""
            val car = Gson().fromJson(carJson, Car::class.java)  // ✅ Convert JSON back to Car
            UserCarDetailScreen(navController, car)
        }
        composable("editCarScreen/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            EditCarScreen(navController, carId)
        }
        composable("userScreen"){
            UserScreen(navController)
        }


    }
}

@Composable
fun OwnerCarsScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        OwnerCars(navController)
    }
}

@Composable
fun AllCarsScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        AllCars(navController)
    }
}
