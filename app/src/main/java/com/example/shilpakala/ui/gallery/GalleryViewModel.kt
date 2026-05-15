package com.example.shilpakala.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakala.data.repository.AuthRepository
import com.example.shilpakala.data.repository.PhotoRepository
import com.example.shilpakala.domain.model.PortfolioPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {
    private val _photos = MutableStateFlow<List<PortfolioPhoto>>(emptyList())
    val photos: StateFlow<List<PortfolioPhoto>> = _photos.asStateFlow()

    init {
        val userId = authRepository.currentUserId()
        if (userId != null) {
            viewModelScope.launch { photoRepository.observePhotos(userId).collect { _photos.value = it } }
        }
    }

    fun delete(photoId: String) = viewModelScope.launch { photoRepository.delete(photoId) }
    fun recordShare(photoId: String) = viewModelScope.launch { photoRepository.incrementShare(photoId) }
}
