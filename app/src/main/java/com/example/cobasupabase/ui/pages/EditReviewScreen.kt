package com.example.cobasupabase.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cobasupabase.ui.common.UiResult
import com.example.cobasupabase.ui.viewmodel.EditReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReviewScreen(
    onBack: () -> Unit,
    viewModel: EditReviewViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val loadState by viewModel.loadState.collectAsState()

    // State dari ViewModel
    val rating by viewModel.ratingInput
    val comment by viewModel.commentInput
    val oldImageUrl by viewModel.imageUrlInput

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        selectedImageUri = uri
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) viewModel.onNewImageSelected(bytes)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiResult.Success) {
            Toast.makeText(context, "Review berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Ulasan") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (loadState is UiResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize()
                ) {
                    // Rating Star
                    Text("Ubah Rating:", style = MaterialTheme.typography.bodyMedium)
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(40.dp).clickable { viewModel.ratingInput.value = i }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Komentar
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { viewModel.commentInput.value = it },
                        label = { Text("Komentar") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gambar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable {
                                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (selectedImageUri != null) "Ganti Gambar Baru" else "Upload/Ganti Foto")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Logic Display Gambar
                    if (selectedImageUri != null) {
                        AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.height(150.dp))
                    } else if (oldImageUrl.isNotEmpty()) {
                        AsyncImage(model = oldImageUrl, contentDescription = null, modifier = Modifier.height(150.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.updateReview() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is UiResult.Loading
                    ) {
                        Text(if (uiState is UiResult.Loading) "Menyimpan..." else "Simpan Perubahan")
                    }
                }
            }
        }
    }
}