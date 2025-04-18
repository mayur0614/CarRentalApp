package com.example.carrentalapp

import android.content.Context
import android.service.autofill.OnClickAction
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

@Composable
fun LoginScreen(navController: NavHostController, padding: Modifier) {


    var passsword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("normal_user") } // Default role

    var haveAccount by remember { mutableStateOf(true) }
    var isLoading = remember { mutableStateOf(false) }

    val context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if(haveAccount)
                "Login"
            else
                "Register",
            fontSize = 30.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = email,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                email=it
            },
            label = {
                Text("Email")
            }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = passsword,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                passsword=it
            },
            label = {
                Text("Password")
            }
        )
        Spacer(Modifier.height(8.dp))
        if(!haveAccount)
        {
            Row (

            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =Arrangement.Center
                ){
                    RadioButton(selected = userType == "normal_user",
                        onClick = {
                            userType = "normal_user"
                        })
                    Text("Normal User")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =Arrangement.Center
                ){
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = userType =="car_owner",
                        onClick ={
                            userType = "car_owner"
                        } )
                    Text("Car Owner")
                }

            }
        }

        Button(
            onClick = {
                if(haveAccount)
                {
                    Constant.ownerName = email
                    loginUser(email,passsword,context,navController,isLoading)
                }else{
                    registerUser(email,passsword,userType,context,navController)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if(isLoading.value){
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White
                )

            }else{
                Text(if(haveAccount) "Login" else "Register ")
            }

        }
        Spacer(Modifier.height(8.dp))
        Text(
            if(haveAccount)
                "Don't have an account ? Sign in "
            else
                "Already An account ? Login ",
            modifier = Modifier.clickable {
                haveAccount = !haveAccount
            }
        )




//        Button(
//            onClick = {
//                Constant.ownerName = username
//                navController.navigate("main")
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Login")
//        }

    }
}

fun loginUser(email: String, password: String, context: Context, navController: NavHostController,isLoading:MutableState<Boolean>) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    isLoading.value = true
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val role = document.getString("role")

                            when (role) {
                                "car_owner" -> navController.navigate("main")// call owner main screen
                                "normal_user" -> navController.navigate("userScreen")//call user screen
                            }
                        } else {
                            Toast.makeText(context, "User data not found!", Toast.LENGTH_SHORT).show()
                        }
                        isLoading.value = false
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching user role", e)
                    }
            } else {
                isLoading.value= false
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}


fun registerUser(email: String, password: String, role: String, context: Context, navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val user = hashMapOf(
                    "email" to email,
                    "role" to role
                )

                firestore.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") // Navigate to login screen
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error saving user", e)
                    }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
