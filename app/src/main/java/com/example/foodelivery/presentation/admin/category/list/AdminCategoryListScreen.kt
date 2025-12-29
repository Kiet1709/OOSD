package com.example.foodelivery.presentation.admin.category.list

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodelivery.presentation.admin.category.list.components.CategoryItem
import com.example.foodelivery.presentation.admin.category.list.contract.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CategoryListEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                CategoryListEffect.NavigateToAddScreen -> onNavigateToAdd()
                is CategoryListEffect.NavigateToEditScreen -> onNavigateToEdit(effect.id)
            }
        }
    }

    // --- DIALOG XÓA ---
    if (state.categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDelete() },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa '${state.categoryToDelete?.name}'?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmDelete() }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDelete() }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý danh mục", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.processIntent(CategoryListIntent.ClickAddCategory) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Add") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.processIntent(CategoryListIntent.SearchCategory(it)) },
                placeholder = { Text("Tìm kiếm...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (state.categories.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Trống", color = Color.Gray) }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.categories) { cat ->
                        CategoryItem(
                            item = cat,
                            onEdit = { viewModel.processIntent(CategoryListIntent.ClickEditCategory(cat.id)) },
                            onDelete = { viewModel.processIntent(CategoryListIntent.ClickDeleteCategory(cat.id)) }
                        )
                    }
                }
            }
        }
    }
}