# PrivacyCall

[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android CI](https://github.com/tekle-eyesus/PrivacyCall/actions/workflows/android.yml/badge.svg)](https://github.com/tekle-eyesus/PrivacyCall/actions/workflows/android.yml)

## Overview

PrivacyCall is an Android application designed to enhance user privacy during phone calls, particularly when receiving calls from unknown or unlisted numbers. It addresses the growing concern of potential privacy breaches and unwanted contact by implementing real-time voice alteration and intelligent call filtering.

## Features

*   **Real-time Voice Alteration:** Transforms your voice into a robotic or other chosen effect during calls from unknown numbers, preventing callers from recognizing your voice.
    *   Adjustable voice alteration parameters (pitch, speed, formant shift).
    *   Multiple voice effect options (robotic, chipmunk, deep voice, etc.).
*   **Intelligent Call Filtering:** Screens unknown callers using an AI-powered chatbot to determine the urgency of the call.
    *   Pre-recorded message playback for unknown callers.
    *   Speech-to-text conversion of the caller's response.
    *   Urgency assessment based on keywords, sentiment analysis, and caller information.
*   **Selective Notification:** Notifies you only when a call is deemed urgent, minimizing interruptions from unwanted calls.
*   **Call Recording Review:** Provides an option to listen to the conversation between the caller and the chatbot before answering an urgent call.
*   **Direct Number Blocking:** Allows you to easily block the caller's number directly from the notification screen.

## Architecture

The application follows a modular architecture, comprising the following key components:

1.  **Call Interception Module:** Detects incoming calls and identifies whether the caller's number is in the user's contact list.
2.  **Audio Processing Module:** Captures the user's voice input, applies the selected voice alteration effect in real-time, and outputs the altered audio to the call.
3.  **AI-Powered Filtering Module:** Employs a chatbot to interact with unknown callers, collecting information about the purpose and urgency of the call.
4.  **Urgency Assessment Module:** Analyzes the chatbot conversation to determine the urgency of the call based on predefined criteria.
5.  **Notification Management Module:** Generates notifications for urgent calls and provides options to review the chatbot conversation or block the caller's number.
6.  **Settings Module:** Allows users to customize the application's behavior, including enabling/disabling features, selecting voice alteration effects, and configuring notification preferences.

## Technologies Used

*   Android SDK
*   Java/Kotlin
*   Audio processing libraries, AI/NLP libraries, or other relevant dependencies

## Security and Privacy

PrivacyCall prioritizes user security and privacy. The application implements the following measures:

*   **Data Encryption:** Encrypts sensitive data, such as call logs and user preferences.
*   **Secure Storage:** Stores data securely on the device, preventing unauthorized access.
*   **Compliance:** Adheres to relevant privacy regulations (e.g., GDPR, CCPA).
*   **Minimal Permissions:** Requests only the necessary permissions to function correctly.

## Getting Started

1.  Clone the repository: `git clone https://github.com/tekle-eyesus/PrivacyCall.git`
2.  Open the project in Android Studio.

## Contributing

We welcome contributions to PrivacyCall! Please follow these guidelines:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Submit a pull request with a clear description of your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments
