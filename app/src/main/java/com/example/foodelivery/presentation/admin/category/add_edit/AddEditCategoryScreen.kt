package com.example.foodelivery.presentation.admin.category.add_edit

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.admin.category.add_edit.contract.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditCategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.processIntent(AddEditCategoryIntent.ImageSelected(it))
    }

    LaunchedEffect(true) {
        viewModel.effect.collectLatest {
            when (it) {
                is AddEditCategoryEffect.ShowToast -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                AddEditCategoryEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Sửa danh mục" else "Thêm danh mục") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.processIntent(AddEditCategoryIntent.ClickBack) }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            // Image Picker
            Box(
                Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUri != null) AsyncImage(state.imageUri, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else if (!state.imageUrl.isNullOrEmpty()) AsyncImage(state.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.Gray)
            }
            if (state.imageError != null) Text(state.imageError!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.processIntent(AddEditCategoryIntent.NameChanged(it)) },
                label = { Text("Tên danh mục") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null
            )
            if (state.nameError != null) Text(state.nameError!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { viewModel.processIntent(AddEditCategoryIntent.Submit) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White)
                else Text("LƯU")
            }
        }
    }
}