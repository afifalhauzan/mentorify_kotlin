package com.example.cobasupabase.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cobasupabase.ui.pages.*
import com.example.cobasupabase.ui.viewmodel.AuthViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cobasupabase.ui.pages.JadwalScreen

object Graph {
    const val ROOT = "root_graph"
}

object Routes {
    const val Login = "login_route"
    const val Register = "register_route"
    const val MainHomeRoute = "main_home_route"
    const val Beranda = "beranda_route"
    const val Cari = "cari_route"
    const val Berita = "berita_route"
    const val BeritaEdit = "news_edit_route/{newsId}"
    const val BeritaDetail = "news_detail/{newsId}"
    const val AddNews = "add_news_route"
    const val Profil = "profil_route"
    const val TeacherDetail = "teacher_detail_route/{teacherId}"
    const val Jadwal = "jadwal_route"
    const val Tempat = "tempat_route"
    const val CreateTeacher = "create_teacher_route"
    const val EditTeacher = "edit_teacher_route/{teacherId}"
    const val ReviewList = "review_list/{teacherId}"
    const val AllReviewsList = "all_reviews_list"
    const val ReviewAdd = "review_add/{teacherId}"

    // 🔹 ROUTE TAMBAHAN (SCHEDULE)
    const val AddJadwal = "add_jadwal"
    const val EditJadwal = "edit_jadwal/{id}"
    fun buildEditJadwalRoute(id: Long) = "edit_jadwal/$id"

    fun buildReviewListRoute(id: Int) = "review_list/$id"
    fun buildReviewAddRoute(id: Int) = "review_add/$id"
    fun buildTeacherDetailRoute(teacherId: Int) = "teacher_detail_route/$teacherId"
    fun buildTeacherEditRoute(teacherId: Int) = "edit_teacher_route/$teacherId"
    fun buildBeritaDetailRoute(newsId: Int) = "news_detail/$newsId"
    fun buildBeritaEditRoute(newsId: Int) = "news_edit_route/$newsId"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    startDestination: String = Routes.Login
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val startDest = if (isAuthenticated) Routes.MainHomeRoute else Routes.Login

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Routes.Login) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.MainHomeRoute) {
                popUpTo(Graph.ROOT) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDest,
        route = Graph.ROOT
    ) {

        composable(Routes.Login) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = { navController.navigate(Routes.Register) }
            )
        }

        composable(Routes.Register) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MainHomeRoute) {
            MainHomeScreen(navController = navController)
        }

        composable(Routes.CreateTeacher) {
            CreateTeacherScreen(navController = navController)
        }

        composable(
            route = Routes.EditTeacher,
            arguments = listOf(navArgument("teacherId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            val id = it.arguments?.getInt("teacherId") ?: -1
            EditTeacherScreen(teacherId = id, navController = navController)
        }

        composable(
            route = Routes.TeacherDetail,
            arguments = listOf(navArgument("teacherId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            val id = it.arguments?.getInt("teacherId") ?: -1
            TeacherDetailScreen(
                teacherId = id,
                navController = navController,
                onNavigateToEditTeacher = { navController.navigate(Routes.buildTeacherEditRoute(it)) },
                onNavigateToReviewList = { navController.navigate(Routes.buildReviewListRoute(it)) }
            )
        }

        composable(
            route = Routes.BeritaDetail,
            arguments = listOf(navArgument("newsId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            val id = it.arguments?.getInt("newsId") ?: -1
            DetailBeritaScreen(
                newsId = id,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(Routes.buildBeritaEditRoute(it))
                }
            )
        }

        composable(
            route = Routes.BeritaEdit,
            arguments = listOf(navArgument("newsId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            val id = it.arguments?.getInt("newsId") ?: -1
            EditBeritaScreen(newsId = id, onBack = { navController.popBackStack() })
        }

        composable(Routes.AddNews) {
            AddNewsScreen(onBack = { navController.popBackStack() })
        }

        // ✅ JADWAL
        composable(Routes.Jadwal) {
            JadwalScreen(navController = navController)
        }

        composable(
            route = "jadwal_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            val id = it.arguments?.getLong("id")!!
            JadwalDetailScreen(navController, id)
        }

        composable(Routes.AddJadwal) {
            AddEditJadwalScreen(navController = navController)
        }

        composable(
            route = Routes.EditJadwal,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            val id = it.arguments?.getLong("id")
            AddEditJadwalScreen(
                navController = navController,
                id = id
            )
        }


        composable(Routes.Tempat) {
            TempatScreen(navController = navController)
        }

        composable(
            route = Routes.ReviewList,
            arguments = listOf(navArgument("teacherId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            val id = it.arguments?.getInt("teacherId") ?: 0
            ReviewListScreen(navController = navController, teacherId = id)
        }

        composable(Routes.AllReviewsList) {
            ReviewListScreen(navController = navController, teacherId = null)
        }

        composable(
            route = Routes.ReviewAdd,
            arguments = listOf(navArgument("teacherId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            val id = it.arguments?.getInt("teacherId") ?: 0
            AddReviewScreen(
                onBack = { navController.popBackStack() },
                teacherId = id
            )
        }
    }
}
