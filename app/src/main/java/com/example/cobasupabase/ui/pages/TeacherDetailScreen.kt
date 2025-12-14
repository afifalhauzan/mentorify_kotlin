package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cobasupabase.ui.nav.Routes
import com.example.cobasupabase.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDetailScreen(
    teacherId: String,
    navController: NavHostController,
    viewModel: TeacherViewModel = viewModel() // Jika Anda nanti ingin load data detail dari API
) {
    // Scaffold dasar halaman
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Guru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5) // Sesuaikan warna background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Agar bisa discroll
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. PROFIL GURU (Foto & Nama) ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                // Ganti model dengan URL gambar asli dari database jika sudah ada data
                // Saat ini pakai icon user/text sebagai placeholder jika tidak ada gambar
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=$teacherId&background=random",
                    contentDescription = "Foto Guru",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nama Guru ($teacherId)", // Nanti diganti data asli: teacher.name
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Matematika & Fisika", // Nanti diganti data asli: teacher.subject
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Badge
            Surface(
                color = Color(0xFFFFF4CC),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.8 (120 Ulasan)", // Nanti diganti data asli
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF664D03)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. DESKRIPSI ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tentang Pengajar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pengajar berpengalaman lebih dari 5 tahun di bidang sains. Metode mengajar yang asik dan mudah dipahami.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. TOMBOL AKSI (Menu) ---

            // Tombol Jadwal
            ActionMenuButton(
                text = "Lihat Jadwal Mengajar",
                icon = Icons.Default.Schedule,
                onClick = { navController.navigate(Routes.Jadwal) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Tempat
            ActionMenuButton(
                text = "Lokasi / Tempat Les",
                icon = Icons.Default.LocationOn,
                onClick = { navController.navigate(Routes.Tempat) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Ulasan (PENTING: Sudah diperbaiki Routenya)
            Button(
                onClick = {
                    // Mengirim Teacher ID agar list review sesuai guru ini
                    navController.navigate(Routes.buildReviewListRoute(teacherId))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B3D6A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lihat & Tulis Ulasan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Komponen Helper untuk tombol menu putih
@Composable
fun ActionMenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2B3D6A))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = Color.Black)
        }
    }
}