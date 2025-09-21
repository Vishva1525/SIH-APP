package com.pmis.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pmis.app.screens.InternFormState

/**
 * Global app state for sharing data between screens
 */
object AppState {
    var internFormData by mutableStateOf(InternFormState())
        private set
    
    fun updateInternFormData(newData: InternFormState) {
        internFormData = newData
    }
    
    fun clearInternFormData() {
        internFormData = InternFormState()
    }
}
