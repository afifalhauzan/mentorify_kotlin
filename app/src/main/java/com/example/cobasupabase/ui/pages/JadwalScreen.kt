package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val schedules by viewModel.schedules.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Les", color = Color.White) },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Routes.AddJadwal) }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah Jadwal",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B9BD5)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(schedules) { schedule ->
                ScheduleCard(
                    schedule = schedule,
                    onClick = {
                        navController.navigate("jadwal_detail/${schedule.id}")
                    }
                )
            }
        }
    }
}