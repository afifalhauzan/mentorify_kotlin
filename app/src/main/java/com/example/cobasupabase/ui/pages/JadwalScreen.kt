package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack // Import ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cobasupabase.ui.components.ScheduleCard
import com.example.cobasupabase.ui.nav.Routes
import com.example.cobasupabase.ui.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = viewModel()
) {
    // Ambil backStackEntry saat ini untuk memicu refresh yang andal
    val backStackEntry = navController.currentBackStackEntry

    // Refresh data setiap kali layar kembali menjadi fokus
    LaunchedEffect(backStackEntry) {
        if (backStackEntry != null) {
            viewModel.fetchSchedules()
        }
    }

    val schedules by viewModel.schedules.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Jadwal Les", color = Color.White) },
                // ✅ TAMBAH: Navigation Icon untuk kembali ke layar sebelumnya (Home)
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali ke Beranda",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B9BD5)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.AddJadwal) },
                containerColor = Color(0xFF2C3E7C),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Jadwal")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                // Filter: Hanya tampilkan jadwal yang TIDAK memiliki status yang dilarang
                val filteredSchedules = schedules.filter { schedule ->
                    !listOf("available", "open slot", "confirmed").contains(schedule.status.lowercase().trim())
                }

                if (filteredSchedules.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 32.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "Tidak ada jadwal aktif yang tersedia.",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSchedules, key = { it.id }) { schedule ->
                            ScheduleCard(
                                schedule = schedule,
                                onClick = {
                                    navController.navigate(Routes.buildJadwalDetailRoute(schedule.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}