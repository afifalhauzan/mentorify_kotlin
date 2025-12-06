package com.example.cobasupabase.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController // for preview
import com.example.cobasupabase.domain.model.Todo
import com.example.cobasupabase.ui.common.UiResult
import com.example.cobasupabase.ui.viewmodel.TodoViewModel
import com.example.cobasupabase.ui.nav.Routes

@Composable
fun BerandaScreen(
    todoViewModel: TodoViewModel = viewModel(),
    navController: NavHostController // Add NavHostController for navigation to DetailScreen
) {
    val todosState by todoViewModel.todos.collectAsState()

    LaunchedEffect(Unit) {
        todoViewModel.loadTodos()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (todosState) {
                is UiResult.Loading -> CircularProgressIndicator()
                is UiResult.Error -> Text(
                    (todosState as UiResult.Error).message,
                    modifier = Modifier.padding(16.dp)
                )
                is UiResult.Success -> TodoList(
                    list = (todosState as UiResult.Success<List<Todo>>).data,
                    onDelete = { todoViewModel.deleteTodo(it) },
                    onDetail = { id -> navController.navigate(Routes.Detail.replace("{id}", id)) }, // Corrected navigation
                    modifier = Modifier.fillMaxSize()
                )
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
                androidx.compose.foundation.layout.Row( // Fully qualify Row to avoid ambiguity with Column
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