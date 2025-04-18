package com.example.carrentalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carrentalapp.screenList.AllCars
import com.example.carrentalapp.screenList.AllCarsScreen
import com.example.carrentalapp.screenList.CarDetailScreen
import com.example.carrentalapp.screenList.MainScreen
import com.example.carrentalapp.screenList.OwnerCars
import com.example.carrentalapp.screenList.UserCarDetailScreen
import com.example.carrentalapp.screenList.UserScreen
import com.example.carrentalapp.ui.theme.CarRentalAppTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            CarRentalAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                navController, Modifier.padding(innerPadding),

                            )
                        }


                        composable("main") {
                            MainScreen()
                        }


                        composable("ownerCars") { OwnerCars(navController) }
                        composable("allCars") { AllCars(navController) }
                        composable("userScreen"){
                            UserScreen(navController)
                        }
                        composable(
                            route = "userCarDetail/{carJson}",
                            arguments = listOf(navArgument("carJson") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val carJson = backStackEntry.arguments?.getString("carJson") ?: ""
                            val car = Gson().fromJson(carJson, Car::class.java)  // ✅ Convert JSON back to Car
                            UserCarDetailScreen(navController, car)
                        }
                        composable(
                            route = "carDetail/{carJson}",
                            arguments = listOf(navArgument("carJson") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val carJson = backStackEntry.arguments?.getString("carJson") ?: ""
                            val car = Gson().fromJson(carJson, Car::class.java)  // ✅ Convert JSON back to Car
                            CarDetailScreen(navController, car)
                        }
                        composable("allCars") { AllCarsScreen(navController) }
                    }
                }
            }
        }
    }
}



