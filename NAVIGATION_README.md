# Navigation Implementation Notes

## Get Started Button Navigation

### Home Screen → Intern Registration Flow

The "Get Started" button on the home screen navigates to the Intern Registration flow with the following behavior:

#### Navigation Setup
- **Route**: `"intern"` → maps to `"intern_registration"`
- **Default Tab**: Opens **Resume/Manual** tab (not Basic Info)
- **Implementation**: Uses `startStep = InternStep.Resume` in `PMISNavigation.kt`

#### Code Location
```kotlin
// In PMISNavigation.kt
composable("intern_registration") {
    InternRegistrationScreen(
        navController = navController,
        authManager = authManager,
        startStep = InternStep.Resume  // Opens Resume/Manual tab by default
    )
}
```

#### Resume/Manual Tab Behavior
The Resume/Manual tab provides two options:
1. **Resume Upload** (default) - Allows users to upload PDF and auto-fill fields
2. **Manual** button - Navigates to Basic Info tab when clicked

#### Manual Button Navigation
```kotlin
// In InternRegistrationScreen.kt - ResumeStep
OutlinedButton(
    onClick = { onNavigateToStep(InternStep.BasicInfo) }
) {
    Text("Fill Details Manually")
}
```

### Key Features
- ✅ **Accessibility**: Button includes `contentDescription = "Get Started"`
- ✅ **Theme Support**: Works in both light and dark modes
- ✅ **Visual Feedback**: Proper elevation and ripple effects
- ✅ **Typography**: Bold, large text with proper contrast
- ✅ **Gradient Background**: Orange to amber gradient matching reference image

### Testing Checklist
- [ ] Get Started button opens Resume/Manual tab (not Basic Info)
- [ ] Manual button in Resume/Manual navigates to Basic Info
- [ ] Button has proper accessibility attributes
- [ ] Visual styling matches reference image
- [ ] Works in light and dark themes
- [ ] Proper touch feedback and elevation
