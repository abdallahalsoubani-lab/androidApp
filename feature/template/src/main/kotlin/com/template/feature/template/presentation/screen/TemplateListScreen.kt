package com.template.feature.template.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.template.feature.template.domain.model.TemplateItem
import com.template.feature.template.presentation.viewmodel.TemplateListUiEffect
import com.template.feature.template.presentation.viewmodel.TemplateListUiEvent
import com.template.feature.template.presentation.viewmodel.TemplateListViewModel
import com.template.ui.components.EmptyScreen
import com.template.ui.components.ErrorScreen
import com.template.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: TemplateListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is TemplateListUiEffect.NavigateToDetail -> onNavigateToDetail(effect.itemId)
                is TemplateListUiEffect.NavigateToCreate -> onNavigateToCreate()
                is TemplateListUiEffect.ShowToast -> {
                    // Handle toast
                }

                is TemplateListUiEffect.ShowDeleteConfirmation -> {
                    // Show confirmation dialog
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Templates") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TemplateListUiEvent.CreateItemClicked) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    LoadingScreen()
                }

                uiState.error != null && uiState.items.isEmpty() -> {
                    ErrorScreen(
                        message = uiState.error,
                        onRetry = { viewModel.onEvent(TemplateListUiEvent.RefreshItems) }
                    )
                }

                uiState.items.isEmpty() -> {
                    EmptyScreen("No templates found")
                }

                else -> {
                    LazyColumn {
                        items(uiState.items) { item ->
                            TemplateItemCard(
                                item = item,
                                onItemClick = {
                                    viewModel.onEvent(TemplateListUiEvent.ItemClicked(item))
                                },
                                onDeleteClick = {
                                    viewModel.onEvent(
                                        TemplateListUiEvent.DeleteItemClicked(item.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateItemCard(
    item: TemplateItem,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}
