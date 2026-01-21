package com.template.feature.template.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.analytics.AnalyticsEvent
import com.template.analytics.AnalyticsTracker
import com.template.common.Result
import com.template.common.dispatcher.DispatcherProvider
import com.template.feature.template.domain.model.TemplateItem
import com.template.feature.template.domain.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UDF pattern for template list screen.
 */

data class TemplateListUiState(
    val isLoading: Boolean = false,
    val items: List<TemplateItem> = emptyList(),
    val error: String? = null,
    val page: Int = 1,
)

sealed class TemplateListUiEvent {
    object LoadItems : TemplateListUiEvent()
    object RefreshItems : TemplateListUiEvent()
    object LoadMoreItems : TemplateListUiEvent()
    data class ItemClicked(val item: TemplateItem) : TemplateListUiEvent()
    object CreateItemClicked : TemplateListUiEvent()
    data class DeleteItemClicked(val itemId: String) : TemplateListUiEvent()
    object ClearError : TemplateListUiEvent()
}

sealed class TemplateListUiEffect {
    data class NavigateToDetail(val itemId: String) : TemplateListUiEffect()
    object NavigateToCreate : TemplateListUiEffect()
    data class ShowToast(val message: String) : TemplateListUiEffect()
    data class ShowDeleteConfirmation(val itemId: String) : TemplateListUiEffect()
}

@HiltViewModel
class TemplateListViewModel @Inject constructor(
    private val repository: TemplateRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TemplateListUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TemplateListUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        onEvent(TemplateListUiEvent.LoadItems)
    }

    fun onEvent(event: TemplateListUiEvent) {
        when (event) {
            is TemplateListUiEvent.LoadItems -> loadItems()
            is TemplateListUiEvent.RefreshItems -> loadItems(refresh = true)
            is TemplateListUiEvent.LoadMoreItems -> loadMoreItems()
            is TemplateListUiEvent.ItemClicked -> onItemClicked(event.item)
            is TemplateListUiEvent.CreateItemClicked -> onCreateClicked()
            is TemplateListUiEvent.DeleteItemClicked -> onDeleteClicked(event.itemId)
            is TemplateListUiEvent.ClearError -> clearError()
        }
    }

    private fun loadItems(refresh: Boolean = false) {
        viewModelScope.launch(dispatcherProvider.io()) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAllItems(page = 1).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            items = result.data,
                            page = 1,
                            error = null,
                        )
                        analyticsTracker.trackEvent(
                            AnalyticsEvent.TemplateListViewed(result.data.size)
                        )
                    }

                    is Result.Error -> {
                        val errorMessage = result.exception.message ?: "Failed to load items"
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage,
                        )
                    }

                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun loadMoreItems() {
        viewModelScope.launch(dispatcherProvider.io()) {
            val currentState = _uiState.value
            val nextPage = currentState.page + 1

            repository.getAllItems(page = nextPage).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = currentState.copy(
                            items = currentState.items + result.data,
                            page = nextPage,
                            error = null,
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = currentState.copy(
                            error = result.exception.message ?: "Failed to load more items"
                        )
                    }

                    is Result.Loading -> {}
                }
            }
        }
    }

    private fun onItemClicked(item: TemplateItem) {
        analyticsTracker.trackEvent(
            AnalyticsEvent.TemplateDetailViewed(item.id)
        )
        viewModelScope.launch {
            _uiEffect.emit(TemplateListUiEffect.NavigateToDetail(item.id))
        }
    }

    private fun onCreateClicked() {
        viewModelScope.launch {
            _uiEffect.emit(TemplateListUiEffect.NavigateToCreate)
        }
    }

    private fun onDeleteClicked(itemId: String) {
        viewModelScope.launch {
            _uiEffect.emit(TemplateListUiEffect.ShowDeleteConfirmation(itemId))
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch(dispatcherProvider.io()) {
            val result = repository.deleteItem(itemId)
            when (result) {
                is Result.Success -> {
                    val updatedItems = _uiState.value.items.filter { it.id != itemId }
                    _uiState.value = _uiState.value.copy(items = updatedItems)
                    analyticsTracker.trackEvent(
                        AnalyticsEvent.TemplateDeleted(itemId)
                    )
                    _uiEffect.emit(TemplateListUiEffect.ShowToast("Item deleted"))
                }

                is Result.Error -> {
                    _uiEffect.emit(TemplateListUiEffect.ShowToast("Failed to delete item"))
                }

                is Result.Loading -> {}
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
