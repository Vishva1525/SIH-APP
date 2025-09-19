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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
// import androidx.compose.ui.text.input.KeyboardOptions // not used currently
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmis.app.ui.theme.PMISAppTheme

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
            InternStep.Resume -> ResumeStep(formState)
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
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
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
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
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

    OutlinedTextField(
        value = state.fullName,
        onValueChange = { state.fullName = it },
        label = { Text("Full Name *") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.verifiedId,
        onValueChange = { state.verifiedId = it },
        label = { Text("Verified ID (Aadhaar/DigiLocker) — coming soon") },
        placeholder = { Text("Enter ID or leave blank") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.collegeName,
        onValueChange = { state.collegeName = it },
        label = { Text("College Name *") },
        placeholder = { Text("Start typing your college") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(12.dp))

    val years = listOf("Select", "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "Graduated")
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(state.yearOfStudy.ifBlank { "Select" }) }
    
    // Update text field when state changes
    LaunchedEffect(state.yearOfStudy) {
        textFieldValue = state.yearOfStudy.ifBlank { "Select" }
    }
    
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                // Allow typing and search functionality
                if (newValue.isNotBlank() && newValue != "Select") {
                    state.yearOfStudy = newValue
                }
            },
            readOnly = false, // Allow typing
            label = { Text("Year of Study") },
            placeholder = { Text("Type to search or select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Filter options based on typed text
            val filteredOptions = if (textFieldValue.isNotBlank() && textFieldValue != "Select") {
                years.filter { it.contains(textFieldValue, ignoreCase = true) }
            } else {
                years
            }
            
            filteredOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    textFieldValue = option
                    state.yearOfStudy = if (option == "Select") "" else option
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun ResumeStep(state: InternFormState) {
    SectionTitle(text = "Resume (optional)", description = "Upload your resume. We'll auto-extract details you can edit.")
    Spacer(modifier = Modifier.height(16.dp))

    // Placeholder for file upload (implementation pending)
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Choose file", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Resume upload is optional in this preview.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Extracted info (editable)", fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.extractedEducation,
            onValueChange = { state.extractedEducation = it },
            label = { Text("Education") },
            modifier = Modifier.weight(1f),
            minLines = 4
        )
        OutlinedTextField(
            value = state.extractedSkills,
            onValueChange = { state.extractedSkills = it },
            label = { Text("Skills") },
            modifier = Modifier.weight(1f),
            minLines = 4
        )
        OutlinedTextField(
            value = state.extractedExperience,
            onValueChange = { state.extractedExperience = it },
            label = { Text("Experience") },
            modifier = Modifier.weight(1f),
            minLines = 4
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


