package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cobasupabase.ui.common.UiResult
import com.example.cobasupabase.ui.components.PlaceCard
import com.example.cobasupabase.ui.viewmodel.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceListScreen(
    navController: NavController,
    viewModel: PlaceViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreatePlace: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPlaces()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Tempat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToCreatePlace() }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tempat")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is UiResult.Idle -> {}

                is UiResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiResult.Success -> {
                    val places = (state as UiResult.Success).data

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(places) { place ->
                            PlaceCard(
                                place = place,
                                onClick = { onNavigateToDetail(place.id) },
                                onDelete = { viewModel.deletePlace(place.id) }
                            )
                        }
                    }
                }

                is UiResult.Error -> {
                    Text(
                        text = (state as UiResult.Error).message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}