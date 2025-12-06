package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cobasupabase.domain.model.Todo
import com.example.cobasupabase.ui.common.UiResult
import com.example.cobasupabase.ui.nav.Routes
import com.example.cobasupabase.ui.viewmodel.AuthViewModel // Import AuthViewModel
import com.example.cobasupabase.ui.viewmodel.TodoViewModel

@Composable
fun BerandaScreen(
    todoViewModel: TodoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(), // Inject AuthViewModel
    navController: NavHostController
) {
    val todosState by todoViewModel.todos.collectAsState()
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState() // Collect current user email

    LaunchedEffect(Unit) {
        todoViewModel.loadTodos()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.AddTodo) }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
        ) {
            // --- Header Section ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2B3D6A)) // Dark blue color from image
                    .padding(horizontal = 24.dp, vertical = 50.dp)
            ) {
                Column {
                    Text(
                        "Selamat Datang,",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        currentUserEmail ?: "Pengguna!", // Display email or "Pengguna!"
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp) // Pull it up to overlap the dark blue box
                    .padding(horizontal = 24.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFF58B2E0) // Light blue color from image
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "Cari guru terbaikmu.",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            "Cari atau pilih dari berbagai tingkat pendidikan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
            // --- End Header Section ---

            // Remaining content (Todo List) takes the rest of the space
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take remaining space
                    .offset(y = (-40).dp) // Adjust for the negative offset of the card
                    .padding(horizontal = 12.dp), // Add some horizontal padding for the list
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (todosState) {
                    is UiResult.Idle -> {}
                    is UiResult.Loading -> CircularProgressIndicator()
                    is UiResult.Error -> Text(
                        (todosState as UiResult.Error).message,
                        modifier = Modifier.padding(16.dp)
                    )
                    is UiResult.Success -> TodoList(
                        list = (todosState as UiResult.Success<List<Todo>>).data,
                        onDelete = { todoViewModel.deleteTodo(it) },
                        onDetail = { id -> navController.navigate(Routes.Detail.replace("{id}", id)) },
                        modifier = Modifier.fillMaxSize() // This modifier on LazyColumn inside TodoList will be applied correctly now
                    )
                }
            }
        }
    }
}

@Composable
private fun TodoList(
    list: List<Todo>,
    onDelete: (String) -> Unit,
    onDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list) { todo ->
            ElevatedCard(onClick = { onDetail(todo.id) }) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                        Text(todo.title, style = MaterialTheme.typography.titleMedium)
                        todo.description?.let { Text(it, maxLines = 2) }
                    }
                    IconButton(onClick = { onDelete(todo.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BerandaScreenPreview() {
    BerandaScreen(navController = rememberNavController())
}