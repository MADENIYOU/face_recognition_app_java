# Facial Recognition App - Java

This is a Java-based facial recognition application that uses OpenCV (via JavaCV) to detect faces, recognize known individuals, and allow for registration of new faces.

## Features

-   **Real-time Face Detection:** Detects faces in a live camera feed.
-   **Face Recognition:** Recognizes previously registered faces.
-   **Face Registration:** Allows users to register new faces by capturing multiple poses.
-   **Simple GUI:** A basic graphical user interface for interaction.

## Prerequisites

Before you begin, ensure you have the following installed:

-   **Java Development Kit (JDK):** Version 11 or higher.
-   **Apache Maven:** Version 3.6.0 or higher.
-   **Webcam:** A functional webcam is required for the application to work.

## Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository_url>
    cd reconnaissance/facial_recognition_app_java
    ```

2.  **Initial Known Faces (Optional):**
    If you have images of known faces, place them in the `known_faces` directory inside the project root. The images should be named in the format `Name_X.png` (e.g., `John_1.png`, `John_2.png`).

## Building the Project

Navigate to the `facial_recognition_app_java` directory and build the project using Maven:

```bash
mvn clean install
```

This command will compile the Java code, download all necessary dependencies (including JavaCV/OpenCV), and package the application into a JAR file.

## Running the Application

After a successful build, you can run the application using the generated JAR file:

```bash
java -jar target/facial-recognition-app-java-1.0-SNAPSHOT.jar
```

The application window should appear, and you can start the camera, register new faces, or perform recognition.

## Usage

-   **Start Camera:** Click the "Start Camera" button to begin the live video feed from your webcam.
-   **Stop Camera:** Click the "Stop Camera" button to stop the camera feed.
-   **Start Registration:** Click "Start Registration," enter a name when prompted, and follow the instructions to capture multiple poses (Center, Left, Right, Up, Down). The application will beep when a pose is captured. After all poses are captured, the model will be re-trained.
-   **Recognition:** Once faces are registered and the model is trained, the application will attempt to recognize faces in the live feed.
