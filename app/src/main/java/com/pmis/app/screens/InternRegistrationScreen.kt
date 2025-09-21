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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.data.AppState
import android.widget.Toast
import android.util.Log
import androidx.compose.ui.platform.LocalContext

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

private enum class InternStep(val label: String) {
    BasicInfo("Basic Info"),
    Resume("Resume"),
    Skills("Skills & Interests"),
    Preferences("Preferences"),
    Fairness("Fairness & Accessibility"),
    Consent("Consent")
}

class InternFormState {
    // Basic Info
    var fullName: String by mutableStateOf("")
    var verifiedId: String by mutableStateOf("")
    var collegeName: String by mutableStateOf("")
    var yearOfStudy: String by mutableStateOf("")
    var percentage: String by mutableStateOf("")
    var location: String by mutableStateOf("")
    // Resume
    var resumePath: String by mutableStateOf("")
    var extractedEducation: String by mutableStateOf("")
    var extractedSkills: String by mutableStateOf("")
    var extractedExperience: String by mutableStateOf("")
    // Skills
    var skills: MutableList<String> = mutableStateListOf()
    var preferredDomain: String by mutableStateOf("")
    var careerGoals: MutableList<String> = mutableStateListOf()
    // Preferences
    var prefLocation: String by mutableStateOf("")
    var prefDuration: String by mutableStateOf("")
    var prefWorkload: String by mutableStateOf("")
    // Fairness & Accessibility
    var fairnessBackground: String by mutableStateOf("")
    var preferredLanguage: String by mutableStateOf("")
    // Consent
    var preferredChannel: String by mutableStateOf("")
    var consentAllowed: Boolean by mutableStateOf(false)
}

@Composable
fun InternRegistrationScreen(
    navController: NavController? = null,
    onNavigateToScreen: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val steps = InternStep.values().toList()
    var currentStep by rememberSaveable { mutableStateOf(InternStep.BasicInfo.ordinal) }
    val formState = remember { InternFormState() }

    val progress = (currentStep + 1) / steps.size.toFloat()

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
            InternStep.BasicInfo -> BasicInfoStep(formState)
            InternStep.Resume -> ResumeStep(
                state = formState,
                onNavigateToStep = { step ->
                    currentStep = step.ordinal
                }
            )
            InternStep.Skills -> SkillsStep(formState)
            InternStep.Preferences -> PreferencesStep(formState)
            InternStep.Fairness -> FairnessStep(formState)
            InternStep.Consent -> ConsentStep(formState)
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
                InternStep.BasicInfo -> formState.fullName.isNotBlank() && formState.collegeName.isNotBlank() && formState.yearOfStudy.isNotBlank()
                InternStep.Resume -> true // optional
                InternStep.Skills -> formState.skills.isNotEmpty()
                InternStep.Preferences -> formState.prefLocation.isNotBlank() && formState.prefDuration.isNotBlank() && formState.prefWorkload.isNotBlank()
                InternStep.Fairness -> formState.fairnessBackground.isNotBlank() && formState.preferredLanguage.isNotBlank()
                InternStep.Consent -> {
                    val channelValid = formState.preferredChannel.isNotBlank()
                    val consentValid = formState.consentAllowed
                    android.util.Log.d("InternForm", "Consent validation - channelValid: $channelValid, consentValid: $consentValid, preferredChannel: '${formState.preferredChannel}'")
                    channelValid && consentValid
                }
            }

            Button(
                onClick = {
                    // Show toast to verify click is working
                    Toast.makeText(context, "Button clicked! Step: $currentStep", Toast.LENGTH_SHORT).show()
                    
                    android.util.Log.d("InternForm", "Button clicked - currentStep: $currentStep, lastIndex: ${steps.lastIndex}, isValid: $isValid")
                    android.util.Log.d("InternForm", "navController: $navController")
                    android.util.Log.d("InternForm", "formState - fullName: ${formState.fullName}, consentAllowed: ${formState.consentAllowed}, preferredChannel: ${formState.preferredChannel}")
                    
                    if (currentStep < steps.lastIndex) {
                        currentStep += 1
                    } else {
                        // Final submit - save form data and navigate to ML recommendations
                        Toast.makeText(context, "Getting AI Recommendations...", Toast.LENGTH_LONG).show()
                        
                        android.util.Log.d("InternForm", "Saving form data and navigating to ML recommendations")
                        AppState.updateInternFormData(formState)
                        
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
    SectionTitle(text = "Basic Info", description = "Tell us a little about yourself.")
    Spacer(modifier = Modifier.height(16.dp))

    val keyboard = LocalSoftwareKeyboardController.current
    val nameFocus = remember { FocusRequester() }
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

    OutlinedTextField(
        value = state.verifiedId,
        onValueChange = { state.verifiedId = it },
        label = { Text("Verified ID (Aadhaar/DigiLocker) — coming soon") },
        placeholder = { Text("Enter ID or leave blank") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.collegeName,
        onValueChange = { state.collegeName = it },
        label = { Text("College Name *") },
        placeholder = { Text("Start typing your college") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

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
    @Suppress("UNUSED_PARAMETER") state: InternFormState,
    onNavigateToStep: (InternStep) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    
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
            SectionTitle(text = "Resume (optional)", description = "Upload your resume. We'll auto-extract details you can edit.")
            Spacer(modifier = Modifier.height(16.dp))

            // selectedUri variable removed since resume upload is commented out
            
            // File picker launcher - COMMENTED OUT
            /*
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                // File picker functionality commented out
            }
            */
            
            // RESUME UPLOAD COMMENTED OUT - Manual entry only
            /*
            LaunchedEffect(selectedUri) {
                selectedUri?.let { uri ->
                    // Resume upload functionality commented out
                    // User will enter details manually
                }
            }
            */

            // Upload section - COMMENTED OUT
            /*
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        // Upload functionality commented out
                    }
            ) {
                // Upload UI commented out
            }
            */
            
            // Manual entry message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Manual Entry Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please fill in your education, skills, and experience details manually below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Warning message for missing resume - COMMENTED OUT
            /*
            if (showWarning) {
                // Warning UI commented out
            }
            */
            
            // Extracted content section - COMMENTED OUT
            /*
            AnimatedVisibility(
                visible = state.extractedEducation.isNotEmpty() || 
                         state.extractedSkills.isNotEmpty() || 
                         state.extractedExperience.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                    initialOffsetX = { it / 3 },
                    animationSpec = tween(300)
                ),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                // Extracted content UI commented out
            }
            */
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
                        // Resume upload is commented out, so always proceed to next step
                        onNavigateToStep(InternStep.Skills)
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
private fun SkillsStep(state: InternFormState) {
    SectionTitle(text = "Skills & Interests", description = "Pick up to 5 skills and your preferred domain.")
    Spacer(modifier = Modifier.height(16.dp))

    var newSkill by remember { mutableStateOf("") }
    OutlinedTextField(
        value = newSkill,
        onValueChange = { newSkill = it },
        label = { Text("Top 5 Skills") },
        placeholder = { Text("Type a skill and press Add") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                val skill = newSkill.trim()
                if (skill.isNotEmpty() && state.skills.size < 5 && !state.skills.contains(skill)) {
                    state.skills.add(skill)
                    newSkill = ""
                }
            },
            enabled = newSkill.isNotBlank() && state.skills.size < 5
        ) { Text("Add") }
        Text(text = "${'$'}{state.skills.size}/5 selected", style = MaterialTheme.typography.bodySmall)
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.skills) { skill ->
            FilterChip(selected = true, onClick = { state.skills.remove(skill) }, label = { Text(skill) })
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
private fun PreferencesStep(state: InternFormState) {
    SectionTitle(text = "Preferences", description = "Tell us how you prefer to work.")
    Spacer(modifier = Modifier.height(16.dp))

    val locations = listOf("Select", "Remote", "On-site", "Hybrid")
    val durations = listOf("Select", "1-3 months", "3-6 months", "6+ months")
    val workloads = listOf("Select", "Part-time", "Full-time")

    DropdownField("Location", locations, state.prefLocation) { state.prefLocation = it }
    Spacer(modifier = Modifier.height(12.dp))
    DropdownField("Duration", durations, state.prefDuration) { state.prefDuration = it }
    Spacer(modifier = Modifier.height(12.dp))
    DropdownField("Workload", workloads, state.prefWorkload) { state.prefWorkload = it }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FairnessStep(state: InternFormState) {
    SectionTitle(text = "Fairness & Accessibility", description = "Help us ensure inclusive recommendations.")
    Spacer(modifier = Modifier.height(16.dp))

    val backgrounds = listOf("Select", "General", "PWD", "First-generation learner", "Minority", "Prefer not to say")
    val languages = listOf("Select", "English", "Hindi", "Bengali", "Tamil", "Telugu", "Marathi", "Kannada", "Gujarati", "Odia")

    DropdownField("Background", backgrounds, state.fairnessBackground) { state.fairnessBackground = it }
    Spacer(modifier = Modifier.height(12.dp))
    DropdownField("Preferred Language", languages, state.preferredLanguage) { state.preferredLanguage = it }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConsentStep(state: InternFormState) {
    SectionTitle(text = "Notifications & Consent", description = "Choose how we reach you and allow recommendations.")
    Spacer(modifier = Modifier.height(16.dp))

    val channels = listOf("Select", "Email", "SMS", "Phone", "WhatsApp")
    DropdownField("Preferred channel", channels, state.preferredChannel) { state.preferredChannel = it }
    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        val checked = state.consentAllowed
        FilterChip(selected = checked, onClick = { state.consentAllowed = !checked }, label = {
            Text("I allow my data to be used for internship recommendations.")
        })
    }
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


