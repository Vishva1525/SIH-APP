# PMIS APP - PM Internship Scheme

A modern Android application built with Kotlin and Jetpack Compose for the Prime Minister's Internship Scheme.

## Features

- **Welcome Screen**: Clean, professional welcome interface with centered messaging
- **Material3 Design**: Modern UI components following Google's Material Design 3 guidelines
- **Navigation**: Smooth navigation between screens using Navigation Compose
- **Responsive Layout**: Proper spacing, padding, and professional styling

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Modern Android Architecture Components
- **Navigation**: Navigation Compose
- **Design System**: Material3

## Project Structure

```
app/
├── src/main/java/com/pmis/app/
│   ├── MainActivity.kt
│   ├── navigation/
│   │   └── PMISNavigation.kt
│   ├── screens/
│   │   ├── WelcomeScreen.kt
│   │   └── MainScreen.kt
│   └── ui/theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── src/main/res/
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── ...
```

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK with API level 24 or higher

### Building the Project

1. Clone or download this project
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the app on an emulator or physical device

### Using Gradle Command Line

```bash
# Build the project
./gradlew build

# Install debug APK
./gradlew installDebug
```

## Screens

### Welcome Screen
- Centered welcome message: "Welcome to the PM Internship Scheme"
- Subheading: "Empowering students with real-world opportunities"
- Large "Get Started" CTA button at the bottom

### Main Screen
- Placeholder screen displaying: "This is the main app screen"
- Ready for future feature implementation

## Development

The app follows modern Android development practices:

- **Single Activity Architecture** with Navigation Compose
- **Material3 theming** for consistent design
- **Composable functions** for reusable UI components
- **Proper resource management** with string resources
- **Clean code structure** with separated concerns

## License

This project is created for the PM Internship Scheme initiative.
