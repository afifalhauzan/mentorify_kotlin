package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cobasupabase.ui.nav.Routes

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Beranda : BottomNavItem(Routes.Beranda, Icons.Default.Home, "Beranda")
    object Cari : BottomNavItem(Routes.Cari, Icons.Default.Search, "Cari")
    object Berita : BottomNavItem(Routes.Berita, Icons.Default.Info, "Berita")
    object Profil : BottomNavItem(Routes.Profil, Icons.Default.Person, "Profil")
}

val bottomNavItems = listOf(
    BottomNavItem.Beranda,
    BottomNavItem.Cari,
    BottomNavItem.Berita,
    BottomNavItem.Profil,
)

@Composable
fun MainHomeScreen(navController: NavHostController) {
    // Controller khusus untuk Bottom Bar (Beranda, Cari, dll)
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry = bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationRoute!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        // NavHost KHUSUS untuk menu Bottom Bar
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Beranda.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // --- 1. BERANDA ---
            composable(Routes.Beranda) {
                BerandaScreen(
                    navController = navController, // Root controller
                    onNavigateToCari = { bottomNavController.navigate(Routes.Cari) },
                    onNavigateToJadwal = { bottomNavController.navigate(Routes.Jadwal) },
                    onNavigateToTempat = { bottomNavController.navigate(Routes.Tempat) },
                    onNavigateToBerita = { bottomNavController.navigate(Routes.Berita) },
                    // [PENTING] Gunakan navController (ROOT) agar Detail Guru menutupi Bottom Bar
                    // dan bisa mengakses halaman Review
                    onNavigateToTeacherDetail = { teacherId ->
                        navController.navigate(Routes.buildTeacherDetailRoute(teacherId))
                    },
                    // Sama, gunakan root controller jika ada tombol review langsung
                    onNavigateToReview = {
                        // navController.navigate(...)
                    }
                )
            }

            // --- 2. MENU LAIN ---
            composable(Routes.Cari) { CariScreen(navController = bottomNavController) }
            composable(Routes.Berita) { BeritaScreen() }
            composable(Routes.Profil) { ProfilScreen() }

            // --- 3. SUB-MENU (Jadwal & Tempat) ---
            // Ini tetap di bottom nav agar user bisa lihat menu bawah
            composable(Routes.Jadwal) { JadwalScreen(navController = bottomNavController) }
            composable(Routes.Tempat) { TempatScreen(navController = bottomNavController) }

            // [HAPUS] TeacherDetail dan Review dari sini!
            // Kita biarkan AppNavHost (Root) yang menanganinya agar navigasinya lancar.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainHomeScreenPreview() {
    MainHomeScreen(navController = rememberNavController())
}