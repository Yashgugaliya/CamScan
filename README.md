# CamScan
### Overview
CamScan is an Android application designed to scan images from the user's photo gallery, detect faces in each image using MediaPipe, and allow users to tag each detected face with names. The app ensures a smooth user experience with loading indicators and saves images with detected faces in a local database.

## Features
- **Gallery Access**: Easily access and scan images from the device's gallery.
- **Face Detection**: Leverage MediaPipe's powerful face detection capabilities to identify faces in images.
- **Tagging**: Tag detected faces with names for better organization and retrieval.
- **Local Storage**: Save images with detected faces and tags in a Room database.
- **User Interface**: Intuitive UI that provides loading indicators during processing.

## Built With 🛠
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [MediaPipe](https://ai.google.dev/edge/mediapipe/framework/getting_started/android) - Library for face detection.
- [Hilt](https://dagger.dev/hilt/) - Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
  - [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.

 # Package Structure
    
    com.example.screenshot  # Root Package
    
    ├── data                # For data handling.
    │   ├── local           # Local Persistence Database. Room (SQLite) database   
    |   ├── repository      # Single source of data.
    │   └── model           # Model classes
    |
    ├── di                  # Dependency Injection         
    ├── util                # Utility Classes / Kotlin extensions  
    ├── presentation        # Presentation layer
    │   ├── ui              # Activity layer
    │   ├── util            # Utility Classes / Kotlin extensions  
    │   └── viewmodel       # ViewHolder for RecyclerView
    └── app                 # Application Class

  ## Architecture
This app uses [***MVVM (Model View View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

![](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)
## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- [Android Studio](https://developer.android.com/studio)
- Kotlin
- Gradle

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Yashgugaliya/CamScan.git

