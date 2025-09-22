package com.pmis.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.data.AppState
import android.widget.Toast
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.SnackbarDuration

import androidx.compose.foundation.layout.size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.background
import com.pmis.app.ui.theme.PMISAppTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.pmis.app.utils.DocumentExtractor
import java.io.File
import android.net.Uri
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.heightIn

enum class InternStep(val label: String) {
    Resume("Resume or Manual"),
    BasicInfo("Basic Info"),
    AcademicBackground("Academic Background"),
    LocationPreferences("Location"),
    Skills("Skills & Interests"),
    InternshipPreferences("Internship Preferences")
}

class InternFormState {
    // Basic Information
    var fullName: String by mutableStateOf("")
    var email: String by mutableStateOf("")
    var phoneNumber: String by mutableStateOf("")
    
    // Academic Background
    var collegeName: String by mutableStateOf("")
    var collegeTier: String by mutableStateOf("")
    var cgpa: String by mutableStateOf("")
    var stream: String by mutableStateOf("")
    var yearOfStudy: String by mutableStateOf("")
    
    // Location Preferences
    var currentLocation: String by mutableStateOf("")
    var preferredWorkLocation: MutableList<String> = mutableStateListOf()
    var ruralUrbanClassification: String by mutableStateOf("")
    
    // Skills & Interests
    var technicalSkills: MutableList<String> = mutableStateListOf()
    var careerInterests: MutableList<String> = mutableStateListOf()
    var domainInterests: MutableList<String> = mutableStateListOf()
    
    // Internship Preferences
    var internshipDuration: String by mutableStateOf("")
    var stipendExpectations: String by mutableStateOf("")
    
    // Legacy fields (keeping for backward compatibility)
    var verifiedId: String by mutableStateOf("")
    var percentage: String by mutableStateOf("")
    var location: String by mutableStateOf("")
    var resumePath: String by mutableStateOf("")
    var extractedEducation: String by mutableStateOf("")
    var extractedSkills: String by mutableStateOf("")
    var extractedExperience: String by mutableStateOf("")
    var skills: MutableList<String> = mutableStateListOf()
    var preferredDomain: String by mutableStateOf("")
    var careerGoals: MutableList<String> = mutableStateListOf()
}

@Composable
fun InternRegistrationScreen(
    navController: NavController? = null,
    onNavigateToScreen: ((String) -> Unit)? = null,
    authManager: com.pmis.app.auth.AuthenticationManager? = null,
    startStep: InternStep = InternStep.BasicInfo
) {
    val context = LocalContext.current
    val steps = InternStep.values().toList()
    var currentStep by rememberSaveable { mutableStateOf(startStep.ordinal) }
    val formState = remember { InternFormState() }
    val coroutineScope = rememberCoroutineScope()

    val progress = (currentStep + 1) / steps.size.toFloat()

    // Load existing form data if user is authenticated
    LaunchedEffect(Unit) {
        authManager?.let { auth ->
            try {
                val savedFormData = auth.loadFormResponses()
                if (savedFormData != null) {
                    // Copy saved data to current form state
                    formState.fullName = savedFormData.fullName
                    formState.email = savedFormData.email
                    formState.phoneNumber = savedFormData.phoneNumber
                    formState.collegeName = savedFormData.collegeName
                    formState.collegeTier = savedFormData.collegeTier
                    formState.cgpa = savedFormData.cgpa
                    formState.stream = savedFormData.stream
                    formState.yearOfStudy = savedFormData.yearOfStudy
                    formState.currentLocation = savedFormData.currentLocation
                    formState.ruralUrbanClassification = savedFormData.ruralUrbanClassification
                    formState.technicalSkills.clear()
                    formState.technicalSkills.addAll(savedFormData.technicalSkills)
                    formState.careerInterests.clear()
                    formState.careerInterests.addAll(savedFormData.careerInterests)
                    formState.domainInterests.clear()
                    formState.domainInterests.addAll(savedFormData.domainInterests)
                    formState.internshipDuration = savedFormData.internshipDuration
                    formState.stipendExpectations = savedFormData.stipendExpectations
                    
                    Toast.makeText(context, "Your previous responses have been loaded", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("InternForm", "Failed to load form data", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        Text(
            text = "Just a few quick questions to get you started 🚀",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress }, 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Step labels (lightweight breadcrumb)
        LazyRow {
            items(steps) { step ->
                val index = step.ordinal
                val selected = index == currentStep
                FilterChip(
                    selected = selected,
                    onClick = { /* Non-clickable breadcrumb */ },
                    label = { Text(step.label) },
                    leadingIcon = if (selected) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
                    } else null,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (InternStep.values()[currentStep]) {
            InternStep.Resume -> ResumeStep(
                state = formState,
                onNavigateToStep = { step ->
                    currentStep = step.ordinal
                }
            )
            InternStep.BasicInfo -> BasicInfoStep(formState)
            InternStep.AcademicBackground -> AcademicBackgroundStep(formState)
            InternStep.LocationPreferences -> LocationPreferencesStep(formState)
            InternStep.Skills -> SkillsStep(formState)
            InternStep.InternshipPreferences -> InternshipPreferencesStep(formState)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { if (currentStep > 0) currentStep -= 1 },
                enabled = currentStep > 0
            ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back")
            }

            val isValid = when (InternStep.values()[currentStep]) {
                InternStep.Resume -> true // optional - can skip to manual entry
                InternStep.BasicInfo -> formState.fullName.isNotBlank() && formState.email.isNotBlank() && formState.phoneNumber.isNotBlank()
                InternStep.AcademicBackground -> formState.collegeName.isNotBlank() && formState.collegeTier.isNotBlank() && formState.cgpa.isNotBlank() && formState.stream.isNotBlank()
                InternStep.LocationPreferences -> formState.currentLocation.isNotBlank() && formState.ruralUrbanClassification.isNotBlank()
                InternStep.Skills -> formState.technicalSkills.isNotEmpty() || formState.careerInterests.isNotEmpty() || formState.domainInterests.isNotEmpty()
                InternStep.InternshipPreferences -> formState.internshipDuration.isNotBlank() && formState.stipendExpectations.isNotBlank()
            }

            Button(
                onClick = {
                    // Show toast to verify click is working
                    Toast.makeText(context, "Button clicked! Step: $currentStep", Toast.LENGTH_SHORT).show()
                    
                    android.util.Log.d("InternForm", "Button clicked - currentStep: $currentStep, lastIndex: ${steps.lastIndex}, isValid: $isValid")
                    android.util.Log.d("InternForm", "navController: $navController")
                    android.util.Log.d("InternForm", "formState - fullName: ${formState.fullName}, internshipDuration: ${formState.internshipDuration}")
                    
                    if (currentStep < steps.lastIndex) {
                        currentStep += 1
                    } else {
                        // Final submit - save form data and navigate to ML recommendations
                        Toast.makeText(context, "Getting AI Recommendations...", Toast.LENGTH_LONG).show()
                        
                        android.util.Log.d("InternForm", "Saving form data and navigating to ML recommendations")
                        
                        // Save to global app state
                        AppState.updateInternFormData(formState)
                        
                        // Save to user's database record if authenticated
                        coroutineScope.launch {
                            authManager?.let { auth ->
                                try {
                                    val saved = auth.saveFormResponses(formState)
                                    if (saved) {
                                        android.util.Log.d("InternForm", "Form data saved to user profile")
                                    } else {
                                        android.util.Log.e("InternForm", "Failed to save form data to user profile")
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("InternForm", "Error saving form data", e)
                                }
                            }
                        }
                        
                        if (navController != null) {
                            android.util.Log.d("InternForm", "Navigating to ml_recommendations via NavController")
                            try {
                                navController.navigate("ml_recommendations")
                                android.util.Log.d("InternForm", "Navigation command sent successfully")
                            } catch (e: Exception) {
                                android.util.Log.e("InternForm", "Navigation failed: ${e.message}", e)
                                Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } else if (onNavigateToScreen != null) {
                            android.util.Log.d("InternForm", "Navigating to ml_recommendations via callback")
                            try {
                                onNavigateToScreen("ml_recommendations")
                                android.util.Log.d("InternForm", "Navigation callback sent successfully")
                            } catch (e: Exception) {
                                android.util.Log.e("InternForm", "Navigation callback failed: ${e.message}", e)
                                Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            android.util.Log.e("InternForm", "No navigation method available!")
                            Toast.makeText(context, "Navigation not available", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = isValid
            ) {
                Text(if (currentStep == steps.lastIndex) "Get AI Recommendations 🚀" else "Next")
                Spacer(modifier = Modifier.width(8.dp))
                if (currentStep == steps.lastIndex) {
                    Icon(Icons.Default.Star, contentDescription = null)
                } else {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, description: String? = null) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasicInfoStep(state: InternFormState) {
    SectionTitle(text = "Basic Information", description = "Tell us a little about yourself.")
    Spacer(modifier = Modifier.height(16.dp))

    val keyboard = LocalSoftwareKeyboardController.current
    val nameFocus = remember { FocusRequester() }
    
    // Full Name
    OutlinedTextField(
        value = state.fullName,
        onValueChange = { state.fullName = it },
        label = { Text("Full Name *") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { keyboard?.hide() }),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(nameFocus)
            .onFocusChanged { if (it.isFocused) keyboard?.show() }
    )
    Spacer(modifier = Modifier.height(12.dp))

    // Email
    OutlinedTextField(
        value = state.email,
        onValueChange = { state.email = it },
        label = { Text("Email Address *") },
        placeholder = { Text("your.email@example.com") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { keyboard?.hide() }),
        modifier = Modifier.fillMaxWidth(),
        isError = state.email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()
    )
    if (state.email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
        Text(
            text = "Please enter a valid email address",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Phone Number
    OutlinedTextField(
        value = state.phoneNumber,
        onValueChange = { state.phoneNumber = it },
        label = { Text("Phone Number *") },
        placeholder = { Text("+91 9876543210") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
        modifier = Modifier.fillMaxWidth(),
        isError = state.phoneNumber.isNotEmpty() && state.phoneNumber.length < 10
    )
    if (state.phoneNumber.isNotEmpty() && state.phoneNumber.length < 10) {
        Text(
            text = "Please enter a valid phone number",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcademicBackgroundStep(state: InternFormState) {
    SectionTitle(text = "Academic Background", description = "Tell us about your educational background.")
    Spacer(modifier = Modifier.height(16.dp))

    // College Name
    OutlinedTextField(
        value = state.collegeName,
        onValueChange = { state.collegeName = it },
        label = { Text("University/College Name *") },
        placeholder = { Text("Start typing your college") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    // College Tier (exactly 3 options)
    val collegeTiers = listOf("Select", "Tier 1", "Tier 2", "Tier 3")
    var tierExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = tierExpanded,
        onExpandedChange = { tierExpanded = !tierExpanded }
    ) {
        OutlinedTextField(
            value = state.collegeTier.ifBlank { "Select" },
            onValueChange = { },
            readOnly = true,
            label = { Text("College Tier *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tierExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = tierExpanded,
            onDismissRequest = { tierExpanded = false }
        ) {
            collegeTiers.drop(1).forEach { tier ->
                DropdownMenuItem(
                    text = { Text(tier) },
                    onClick = {
                        state.collegeTier = tier
                        tierExpanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // CGPA
    OutlinedTextField(
        value = state.cgpa,
        onValueChange = { state.cgpa = it },
        label = { Text("Current CGPA *") },
        placeholder = { Text("8.5") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        isError = state.cgpa.isNotEmpty() && (state.cgpa.toDoubleOrNull()?.let { it < 0 || it > 10 } ?: false)
    )
    if (state.cgpa.isNotEmpty() && (state.cgpa.toDoubleOrNull()?.let { it < 0 || it > 10 } ?: false)) {
        Text(
            text = "Please enter a valid CGPA (0-10)",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Stream/Field of Study - Autocomplete with comprehensive list and accessibility
    val allStreams = listOf(
        // Engineering & Technology
        "Computer Science & Engineering",
        "Information Technology",
        "Artificial Intelligence & Data Science",
        "Electronics & Communication Engineering",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering",
        "Chemical Engineering",

        // Science
        "Physics",
        "Chemistry",
        "Biotechnology",
        "Data Science & Machine Learning",
        "Cybersecurity",
        "Environmental & Sustainability Studies",
        "Cognitive Science",
        "Bioinformatics",
        "Biotechnology Entrepreneurship",

        // Business & Management
        "Accounting",
        "Finance",
        "Business Administration (BBA/MBA)",
        "Economics",
        "Banking & Insurance",
        "Marketing",
        "Human Resource Management",
        "International Business",
        "Entrepreneurship",
        "Supply Chain Management",

        // Arts & Humanities
        "Literature (English, Regional Languages)",
        "History",
        "Political Science",
        "Sociology",
        "Philosophy",
        "Anthropology",
        "Psychology",
        "Linguistics",
        "Fine Arts (Painting, Sculpture)",
        "Performing Arts (Music, Dance, Theatre)",

        // Law & Social Sciences
        "Law (LLB/LLM)",
        "Criminology",
        "International Relations",
        "Public Administration",
        "Social Work",
        "Gender Studies",
        "Development Studies",

        // Media & Communication
        "Journalism",
        "Mass Communication",
        "Film Studies",
        "Media Production",
        "Digital Marketing",
        "Advertising & Public Relations",

        // Education
        "Education (B.Ed, M.Ed)",
        "Special Education",
        "Curriculum Development",
        "Educational Psychology",

        // Emerging Fields
        "Sports Science",
        "Design Thinking & Innovation",
        "Space Studies"
    )

    var streamExpanded by remember { mutableStateOf(false) }
    var streamText by remember { mutableStateOf(state.stream) }
    var highlightedIndex by remember { mutableStateOf(0) }

    val filteredStreams = remember(streamText) {
        if (streamText.isBlank()) allStreams
        else allStreams.filter { stream ->
            // Filter by first letter or word start
            stream.split(" ").any { word -> 
                word.startsWith(streamText, ignoreCase = true)
            }
        }
    }

    // Ensure highlighted index stays in bounds when list changes
    LaunchedEffect(filteredStreams.size) {
        if (highlightedIndex >= filteredStreams.size) highlightedIndex = 0
    }

    ExposedDropdownMenuBox(
        expanded = streamExpanded,
        onExpandedChange = { streamExpanded = !streamExpanded }
    ) {
        OutlinedTextField(
            value = streamText,
            onValueChange = { newValue ->
                streamText = newValue
                state.stream = newValue
                streamExpanded = true
            },
            readOnly = false,
            label = { Text("Stream/Field of Study *") },
            placeholder = { Text("Type to search or select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = streamExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = streamExpanded,
            onDismissRequest = { streamExpanded = false }
        ) {
            if (filteredStreams.isEmpty()) {
                DropdownMenuItem(text = { Text("No matching field found") }, onClick = { })
            } else {
                filteredStreams.forEachIndexed { index, stream ->
                    DropdownMenuItem(
                        text = { Text(stream) },
                        onClick = {
                            streamText = stream
                            state.stream = stream
                            streamExpanded = false
                        },
                        // lightweight visual cue for keyboard highlight
                        leadingIcon = if (index == highlightedIndex) {
                            { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Year of Study
    val years = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, 
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = state.yearOfStudy.ifBlank { "Select Year of Study" },
            onValueChange = { /* Read-only field */ },
            readOnly = true,
            label = { Text("Year of Study *") },
            trailingIcon = { 
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year) },
                    onClick = {
                        state.yearOfStudy = year
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
private fun ResumeStep(
    state: InternFormState,
    onNavigateToStep: (InternStep) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resumeUploadViewModel = remember { com.pmis.app.viewmodel.ResumeUploadViewModel() }
    
    val uiState by resumeUploadViewModel.uiState.collectAsState()
    
    // Handle extracted data
    LaunchedEffect(uiState.extractedData) {
        uiState.extractedData?.let { extractedData ->
            // Map extracted data to form state
            extractedData.name.takeIf { it.isNotEmpty() }?.let { state.fullName = it }
            extractedData.email.takeIf { it.isNotEmpty() }?.let { state.email = it }
            extractedData.phoneNumber.takeIf { it.isNotEmpty() }?.let { state.phoneNumber = it }
            extractedData.location.takeIf { it.isNotEmpty() }?.let { state.currentLocation = it }
            extractedData.institution.takeIf { it.isNotEmpty() }?.let { state.collegeName = it }
            extractedData.degree.takeIf { it.isNotEmpty() }?.let { state.stream = it }
            extractedData.graduationYear.takeIf { it.isNotEmpty() }?.let { 
                // Map graduation year to year of study
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                val gradYear = extractedData.graduationYear.toIntOrNull() ?: currentYear
                val yearDiff = gradYear - currentYear
                state.yearOfStudy = when {
                    yearDiff <= -4 -> "Graduated"
                    yearDiff == -3 -> "4th Year"
                    yearDiff == -2 -> "3rd Year"
                    yearDiff == -1 -> "2nd Year"
                    yearDiff == 0 -> "1st Year"
                    else -> "1st Year"
                }
            }
            extractedData.cgpa.takeIf { it.isNotEmpty() }?.let { state.cgpa = it }
            extractedData.skills.takeIf { it.isNotEmpty() }?.let { skills ->
                state.technicalSkills.clear()
                state.technicalSkills.addAll(skills)
            }
            
            // Show success message
            snackbarHostState.showSnackbar(
                message = "Resume data extracted and filled successfully!",
                duration = SnackbarDuration.Long
            )
        }
    }
    
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxHeight = maxHeight
        
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp) // Space for sticky buttons
        ) {
            SectionTitle(text = "Resume Upload", description = "Upload your resume to automatically fill your profile details.")
            Spacer(modifier = Modifier.height(16.dp))

            // Resume Upload Component
            com.pmis.app.ui.components.ResumeUploadComponent(
                viewModel = resumeUploadViewModel,
                onDataExtracted = { extractedData ->
                    // Data is already handled in LaunchedEffect above
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            
            // Manual entry option
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Or Fill Manually",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can skip resume upload and fill in your details manually in the following steps.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    
        // Sticky navigation buttons at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = maxHeight - 80.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    androidx.compose.foundation.shape.RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button with animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { -it / 2 },
                    animationSpec = tween(300)
                ),
                exit = fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                    targetOffsetX = { -it / 2 },
                    animationSpec = tween(200)
                )
            ) {
                OutlinedButton(
                    onClick = { onNavigateToStep(InternStep.BasicInfo) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft, 
                        contentDescription = "Back",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back")
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Next button with animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { it / 2 },
                    animationSpec = tween(300)
                ),
                exit = fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                    targetOffsetX = { it / 2 },
                    animationSpec = tween(200)
                )
            ) {
                Button(
                    onClick = { 
                        onNavigateToStep(InternStep.BasicInfo)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                        contentDescription = "Next",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        // Snackbar for user feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPreferencesStep(state: InternFormState) {
    SectionTitle(text = "Location Preferences", description = "Tell us about your location preferences.")
    Spacer(modifier = Modifier.height(16.dp))

    // Current Location - Indian states autocomplete (starts-with, case-insensitive)
    val indianStates = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal",
        // Union Territories
        "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu",
        "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
    )

    var locationExpanded by remember { mutableStateOf(false) }
    var locationText by remember { mutableStateOf(state.currentLocation) }
    var highlightedIndex by remember { mutableStateOf(0) }

    val filteredStates = remember(locationText) {
        val q = locationText.trim()
        if (q.isBlank()) emptyList() else indianStates.filter { it.startsWith(q, ignoreCase = true) }
    }

    LaunchedEffect(filteredStates.size) {
        if (highlightedIndex >= filteredStates.size) highlightedIndex = 0
    }

    ExposedDropdownMenuBox(
        expanded = locationExpanded,
        onExpandedChange = { locationExpanded = !locationExpanded }
    ) {
        OutlinedTextField(
            value = locationText,
            onValueChange = { newValue ->
                locationText = newValue
                state.currentLocation = newValue
                // Only expand when there's some input
                locationExpanded = newValue.isNotBlank()
            },
            readOnly = false,
            label = { Text("Current Location *") },
            placeholder = { Text("Type your state name") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = locationExpanded,
            onDismissRequest = { locationExpanded = false }
        ) {
            if (filteredStates.isEmpty()) {
                DropdownMenuItem(text = { Text("No state found") }, onClick = { })
            } else {
                filteredStates.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        text = { Text(s) },
                        onClick = {
                            locationText = s
                            state.currentLocation = s
                            locationExpanded = false
                        },
                        leadingIcon = if (index == highlightedIndex) {
                            { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Rural / Urban Classification (strict two-option dropdown)
    val ruralUrbanOptions = listOf("Rural", "Urban")
    var ruralUrbanExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = ruralUrbanExpanded,
        onExpandedChange = { ruralUrbanExpanded = !ruralUrbanExpanded }
    ) {
        OutlinedTextField(
            value = state.ruralUrbanClassification.ifBlank { "Select" },
            onValueChange = { },
            readOnly = true,
            label = { Text("Rural / Urban Classification *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ruralUrbanExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = ruralUrbanExpanded,
            onDismissRequest = { ruralUrbanExpanded = false }
        ) {
            ruralUrbanOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        state.ruralUrbanClassification = option
                        ruralUrbanExpanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    // Preferred Work Locations (Multi-select)
    Text(
        text = "Preferred Work Locations (Select all that apply)",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
    
    val workLocations = listOf(
        "Same as current location", "Delhi", "Mumbai", "Bangalore", "Chennai", 
        "Hyderabad", "Pune", "Kolkata", "Remote", "Anywhere"
    )
    
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(workLocations) { location ->
            val isSelected = state.preferredWorkLocation.contains(location)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        state.preferredWorkLocation.remove(location)
                    } else {
                        state.preferredWorkLocation.add(location)
                    }
                },
                label = { Text(location) }
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LinkedInStyleSkillPicker(
    selectedSkills: MutableList<String>,
    onSkillsChange: () -> Unit = {},
    label: String = "Technical Skills",
    placeholder: String = "Add a skill"
) {
    val commonSkills = listOf(
        "Python", "Java", "JavaScript", "SQL", "Machine Learning", "Data Analysis",
        "React", "Node.js", "Deep Learning", "Web Development", "Mobile Development",
        "Database Management", "C++", "C#", "Go", "Rust", "Scala", "Ruby", "PHP",
        "Spring", "Django", "Flask", "Laravel", "Rails", "ASP.NET",
        "TensorFlow", "PyTorch", "Keras", "Pandas", "NumPy", "Scikit-learn",
        "HTML", "CSS", "Bootstrap", "Sass", "Less", "Webpack", "Babel",
        "Docker", "Kubernetes", "AWS", "Azure", "GCP", "Git", "GitHub", "GitLab",
        "Jenkins", "CI/CD", "Android", "iOS", "Flutter", "React Native", "Kotlin", "Swift",
        "MongoDB", "PostgreSQL", "MySQL", "Redis", "GraphQL", "REST API", "Microservices",
        "Angular", "Vue.js", "TypeScript", "Tailwind CSS", "Material UI", "Firebase"
    )
    
    var inputText by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    var showDuplicateMessage by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val filteredSuggestions = remember(inputText) {
        if (inputText.isBlank()) emptyList()
        else commonSkills.filter { 
            it.contains(inputText, ignoreCase = true) && 
            !selectedSkills.contains(it)
        }.take(8)
    }
    
    // Hide duplicate message after 2 seconds
    LaunchedEffect(showDuplicateMessage) {
        if (showDuplicateMessage) {
            kotlinx.coroutines.delay(2000)
            showDuplicateMessage = false
        }
    }
    
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Selected Skills Chips
        if (selectedSkills.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                selectedSkills.forEach { skill ->
                    AssistChip(
                        onClick = { 
                            selectedSkills.remove(skill)
                            onSkillsChange()
                        },
                        label = { 
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.bodySmall
                            ) 
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove $skill",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Input Field with Suggestions
        Box {
            OutlinedTextField(
                value = inputText,
                onValueChange = { newValue ->
                    inputText = newValue
                    showSuggestions = newValue.isNotBlank()
                    showDuplicateMessage = false
                },
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        showSuggestions = focusState.isFocused && inputText.isNotBlank()
                    },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val skill = inputText.trim()
                        if (skill.isNotEmpty()) {
                            if (selectedSkills.contains(skill)) {
                                showDuplicateMessage = true
                            } else {
                                selectedSkills.add(skill)
                                inputText = ""
                                showSuggestions = false
                                onSkillsChange()
                            }
                        }
                        keyboardController?.hide()
                    }
                ),
                singleLine = true,
                isError = showDuplicateMessage
            )
            
            // Suggestions Dropdown
            if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 56.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(filteredSuggestions) { suggestion ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodyMedium
                                    ) 
                                },
                                onClick = {
                                    selectedSkills.add(suggestion)
                                    inputText = ""
                                    showSuggestions = false
                                    onSkillsChange()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        
        // Error/Feedback Messages
        if (showDuplicateMessage) {
            Text(
                text = "Skill already added",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        // Helper text
        if (selectedSkills.isEmpty()) {
            Text(
                text = "Start typing to see suggestions or press Enter to add custom skills",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkillsStep(state: InternFormState) {
    SectionTitle(text = "Skills & Interests", description = "Select your technical skills and career interests.")
    Spacer(modifier = Modifier.height(16.dp))

    // Technical Skills Section - LinkedIn Style
    LinkedInStyleSkillPicker(
        selectedSkills = state.technicalSkills,
        onSkillsChange = { /* State is automatically updated */ },
        label = "Technical Skills",
        placeholder = "Add a skill (e.g., Python, React, Machine Learning)"
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Career Interests Section
    Text(
        text = "Career Interests (Select all that apply)",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
    
    val careerInterests = listOf(
        "Data Science", "Software Development", "AI/ML", "Web Development", 
        "Mobile Apps", "Database Administration", "Cloud Computing", "Cybersecurity",
        "DevOps", "UI/UX Design", "Product Management", "Business Analysis",
        "Quality Assurance", "System Administration", "Network Engineering"
    )
    
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(careerInterests) { interest ->
            val isSelected = state.careerInterests.contains(interest)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        state.careerInterests.remove(interest)
                    } else {
                        state.careerInterests.add(interest)
                    }
                },
                label = { Text(interest) }
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Domain Interests Section
    Text(
        text = "Domain Interests (Select all that apply)",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
    
    val domainInterests = listOf(
        "Data Science", "Software Development", "AI/ML", "Web Development", 
        "Mobile Apps", "Database", "Cloud Computing", "Cybersecurity",
        "FinTech", "HealthTech", "EdTech", "E-commerce", "Gaming", "IoT"
    )
    
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(domainInterests) { domain ->
            val isSelected = state.domainInterests.contains(domain)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        state.domainInterests.remove(domain)
                    } else {
                        state.domainInterests.add(domain)
                    }
                },
                label = { Text(domain) }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val domains = listOf(
        "Software Development",
        "Data Science",
        "Design",
        "Marketing",
        "Operations",
        "HR",
        "Finance"
    )
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(state.preferredDomain.ifBlank { "Select" }) }
    
    // Update text field when state changes
    LaunchedEffect(state.preferredDomain) {
        textFieldValue = state.preferredDomain.ifBlank { "Select" }
    }
    
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                // Allow typing and search functionality
                if (newValue.isNotBlank() && newValue != "Select") {
                    state.preferredDomain = newValue
                }
            },
            readOnly = false, // Allow typing
            label = { Text("Preferred Internship Domain") },
            placeholder = { Text("Type to search or select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Filter options based on typed text
            val filteredOptions = if (textFieldValue.isNotBlank() && textFieldValue != "Select") {
                domains.filter { it.contains(textFieldValue, ignoreCase = true) }
            } else {
                domains
            }
            
            filteredOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    textFieldValue = option
                    state.preferredDomain = option
                    expanded = false
                })
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    var newGoal by remember { mutableStateOf("") }
    OutlinedTextField(
        value = newGoal,
        onValueChange = { newGoal = it },
        label = { Text("Career Goal Keywords (max 3)") },
        placeholder = { Text("e.g., Data Scientist, UX Designer") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                val goal = newGoal.trim()
                if (goal.isNotEmpty() && state.careerGoals.size < 3 && !state.careerGoals.contains(goal)) {
                    state.careerGoals.add(goal)
                    newGoal = ""
                }
            },
            enabled = newGoal.isNotBlank() && state.careerGoals.size < 3
        ) { Text("Add") }
        Text(text = "${'$'}{state.careerGoals.size}/3 selected", style = MaterialTheme.typography.bodySmall)
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.careerGoals) { g ->
            FilterChip(selected = true, onClick = { state.careerGoals.remove(g) }, label = { Text(g) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternshipPreferencesStep(state: InternFormState) {
    SectionTitle(text = "Internship Preferences", description = "Tell us about your internship preferences.")
    Spacer(modifier = Modifier.height(16.dp))

    // Internship Duration
    val durations = listOf("3 months", "6 months", "12 months")
    var durationExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = durationExpanded,
        onExpandedChange = { durationExpanded = !durationExpanded }
    ) {
        OutlinedTextField(
            value = state.internshipDuration.ifBlank { "Select Duration" },
            onValueChange = { },
            readOnly = true,
            label = { Text("Internship Duration *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = durationExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = durationExpanded,
            onDismissRequest = { durationExpanded = false }
        ) {
            durations.forEach { duration ->
                DropdownMenuItem(
                    text = { Text(duration) },
                    onClick = {
                        state.internshipDuration = duration
                        durationExpanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // Stipend Expectation (Numeric Input)
    OutlinedTextField(
        value = state.stipendExpectations,
        onValueChange = { newValue ->
            // Only allow numeric input, max 6 digits
            val numericValue = newValue.filter { it.isDigit() }
            if (numericValue.length <= 6) {
                state.stipendExpectations = numericValue
            }
        },
        label = { Text("Stipend Expectation *") },
        placeholder = { Text("Enter expected stipend") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Text(
                text = "₹",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        supportingText = {
            Text(
                text = "Enter amount in rupees (max 6 digits)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    )
    Spacer(modifier = Modifier.height(16.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(label: String, options: List<String>, selectedValue: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(selectedValue) }
    
    // Update text field when selection changes
    LaunchedEffect(selectedValue) {
        textFieldValue = selectedValue.ifBlank { "Select" }
    }
    
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                // Allow typing and search functionality
                if (newValue.isNotBlank() && newValue != "Select") {
                    onSelected(newValue)
                }
            },
            readOnly = false, // Allow typing
            label = { Text(label) },
            placeholder = { Text("Type to search or select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Filter options based on typed text
            val filteredOptions = if (textFieldValue.isNotBlank() && textFieldValue != "Select") {
                options.filter { it.contains(textFieldValue, ignoreCase = true) }
            } else {
                options
            }
            
            filteredOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    textFieldValue = option
                    onSelected(if (option == "Select") "" else option)
                    expanded = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InternRegistrationPreview() {
    PMISAppTheme {
        Surface { InternRegistrationScreen() }
    }
}


