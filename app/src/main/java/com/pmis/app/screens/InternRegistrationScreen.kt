package com.pmis.app.screens

import android.content.res.Configuration
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

private class InternFormState {
    // Basic Info
    var fullName: String by mutableStateOf("")
    var verifiedId: String by mutableStateOf("")
    var collegeName: String by mutableStateOf("")
    var yearOfStudy: String by mutableStateOf("")
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
fun InternRegistrationScreen() {
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
                InternStep.Consent -> formState.preferredChannel.isNotBlank() && formState.consentAllowed
            }

            Button(
                onClick = {
                    if (currentStep < steps.lastIndex) {
                        currentStep += 1
                    } else {
                        // Final submit - here you can integrate submission
                    }
                },
                enabled = isValid
            ) {
                Text(if (currentStep == steps.lastIndex) "Activate My Smart Recommendations 🚀" else "Next")
                Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
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
    state: InternFormState,
    onNavigateToStep: (InternStep) -> Unit = {}
) {
    val context = LocalContext.current
    var isExtracting by remember { mutableStateOf(false) }
    var uploadStatus by remember { mutableStateOf("") }
    var showWarning by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
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

            var selectedUri by remember { mutableStateOf<Uri?>(null) }
            
            // File picker launcher
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let { 
                    selectedUri = it
                    state.resumePath = it.toString()
                    uploadStatus = "Resume uploaded successfully!"
                    isExtracting = true
                    showWarning = false // Hide warning when resume is uploaded
                }
            }
            
            // Handle extraction when a file is selected
            LaunchedEffect(selectedUri) {
                selectedUri?.let { uri ->
                    try {
                        val extractor = DocumentExtractor(context)
                        val extractedContent = extractor.extractFromUri(uri)
                        state.extractedEducation = extractedContent.education
                        state.extractedSkills = extractedContent.skills
                        state.extractedExperience = extractedContent.experience
                        uploadStatus = "Resume processed! Review and edit the extracted information below."
                    } catch (e: Exception) {
                        uploadStatus = "Error processing resume: ${e.message}"
                    } finally {
                        isExtracting = false
                    }
                }
            }

            // Upload section
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        if (!isExtracting) {
                            filePickerLauncher.launch("*/*") // Accept all file types
                        }
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isExtracting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Processing resume...", 
                                color = MaterialTheme.colorScheme.primary, 
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                "📁 Click to browse and upload resume", 
                                color = MaterialTheme.colorScheme.primary, 
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    if (uploadStatus.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uploadStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uploadStatus.contains("Error")) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (state.resumePath.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📄 Resume: ${File(state.resumePath).name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Warning message for missing resume
            if (showWarning) {
                AnimatedVisibility(
                    visible = showWarning,
                    enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                        initialOffsetX = { -it / 2 },
                        animationSpec = tween(300)
                    ),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Resume Required",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "Please upload your resume document to proceed to the next section.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Extracted content section
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
                Column {
                    Text(text = "Extracted info (editable)", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.extractedEducation,
                            onValueChange = { state.extractedEducation = it },
                            label = { Text("Education") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                        
                        OutlinedTextField(
                            value = state.extractedSkills,
                            onValueChange = { state.extractedSkills = it },
                            label = { Text("Skills") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                        
                        OutlinedTextField(
                            value = state.extractedExperience,
                            onValueChange = { state.extractedExperience = it },
                            label = { Text("Experience") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                    }
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
                        if (state.resumePath.isEmpty()) {
                            showWarning = true
                        } else {
                            onNavigateToStep(InternStep.Skills)
                        }
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


