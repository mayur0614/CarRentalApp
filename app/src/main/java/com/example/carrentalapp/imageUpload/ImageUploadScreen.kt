package com.example.carrentalapp.imageUpload.ImageUploadScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

@Composable
fun ImageUploadScreen() {
    val context = LocalContext.current
    var imageBitmaps by remember { mutableStateOf(listOf<Pair<String, Bitmap>>()) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageName by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var isFetching by remember { mutableStateOf(true) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(context, it)
            bitmap?.let { selectedBitmap = it }
        }
    }

    LaunchedEffect(Unit) {
        fetchImagesFromFirestore {
            imageBitmaps = it
            isFetching = false
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Upload Image with Name", fontSize = 20.sp)

        OutlinedTextField(
            value = imageName,
            onValueChange = { imageName = it },
            label = { Text("Enter Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select an Image")
        }

        Button(
            onClick = {
                if (selectedBitmap != null && imageName.isNotBlank()) {
                    isUploading = true
                    uploadImageToFirestore(imageName, selectedBitmap!!) {
                        isUploading = false
                        fetchImagesFromFirestore { imageBitmaps = it }
                    }
                }
            },
            modifier = Modifier.padding(8.dp),
            enabled = selectedBitmap != null && imageName.isNotBlank()
        ) {
            Text("Upload")
        }

        if (isUploading) CircularProgressIndicator()
        if (isFetching) CircularProgressIndicator()

        LazyColumn {
            items(imageBitmaps) { (name, bitmap) ->
                ImageCard(name, bitmap)
            }
        }
    }
}

@Composable
fun ImageCard(name: String, bitmap: Bitmap) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = name, modifier = Modifier.fillMaxWidth().height(200.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontSize = 18.sp)
        }
    }
}

// Convert URI to Bitmap
fun uriToBitmap(context: android.content.Context, uri: Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
}

// Compress Bitmap
fun compressBitmap(bitmap: Bitmap, maxWidth: Int = 800, maxHeight: Int = 800, quality: Int = 70): Bitmap {
    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val (finalWidth, finalHeight) = if (bitmap.width > bitmap.height) {
        maxWidth to (maxWidth / ratio).toInt()
    } else {
        (maxHeight * ratio).toInt() to maxHeight
    }

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
}

// Convert Bitmap to Base64
fun encodeImageToBase64(bitmap: Bitmap): String {
    val compressedBitmap = compressBitmap(bitmap)
    val outputStream = ByteArrayOutputStream()
    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}

// Upload Image & Name to Firestore
fun uploadImageToFirestore(name: String, bitmap: Bitmap, onUploadComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val imageData = hashMapOf(
        "name" to name,
        "imageBase64" to encodeImageToBase64(bitmap)
    )

    db.collection("images").add(imageData)
        .addOnSuccessListener { onUploadComplete() }
        .addOnFailureListener { e -> Log.e("Firestore", "Error: ${e.message}") }
}

// Decode Base64 to Bitmap
fun decodeBase64ToImage(base64Str: String): Bitmap {
    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

// Fetch Images from Firestore
fun fetchImagesFromFirestore(onComplete: (List<Pair<String, Bitmap>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("images").get()
        .addOnSuccessListener { documents ->
            val data = documents.mapNotNull {
                val name = it.getString("name")
                val base64 = it.getString("imageBase64")
                if (name != null && base64 != null) {
                    try { name to decodeBase64ToImage(base64) } catch (e: Exception) {
                        Log.e("Firestore", "Error decoding image", e)
                        null
                    }
                } else null
            }
            onComplete(data)
        }
        .addOnFailureListener { e -> Log.e("Firestore", "Error fetching images: ${e.message}") }
}
