package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cobasupabase.ui.common.UiResult
import com.example.cobasupabase.ui.nav.Routes
import com.example.cobasupabase.ui.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewListScreen(
    navController: NavController,
    teacherId: Int? = 0, // Changed defaultValue to 0 for consistency with Int IDs
    viewModel: ReviewViewModel = viewModel()
) {
    LaunchedEffect(teacherId) {
        if (teacherId != null && teacherId != 0) { // Check for non-zero teacherId
            viewModel.loadReviews(teacherId)
        } else {
            viewModel.loadAllReviews()
        }
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (teacherId != null && teacherId != 0) "Ulasan Guru" else "Semua Ulasan") }, // Adjusted title
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (teacherId != null && teacherId != 0) { // Only show FAB if a valid teacherId is provided
                FloatingActionButton(onClick = { navController.navigate(Routes.buildReviewAddRoute(teacherId)) }) {
                    Icon(Icons.Default.Add, "Tambah")
                }
            }
        }
    ) { p ->
        Box(Modifier.padding(p)) {
            when(val res = state) {
                is UiResult.Loading -> CircularProgressIndicator()
                is UiResult.Success -> {
                    LazyColumn {
                        items(res.data) { review ->
                            Text(text = "${review.reviewerName}: ${review.comment}", modifier = Modifier.padding(16.dp))
                            Divider()
                        }
                    }
                }
                is UiResult.Error -> Text("Error: ${res.message}")
                else -> {}
            }
        }
    }
}