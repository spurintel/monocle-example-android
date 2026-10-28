# monocle-example-android
Example app using the [Monocle Android SDK](https://github.com/spurintel/monocle-sdk-android).

This app uses the SDK the way a third-party app would: as the compiled library in `app/libs/monocle.aar`, rather than the SDK's source module. To add the SDK to your own app, follow the installation steps in the [SDK README](https://github.com/spurintel/monocle-sdk-android#installation).

## Running the example

1. Set your Monocle app's **Publishable key** as `publishable_key` in `app/src/main/res/values/strings.xml`. You'll find it on the app's **Settings** page under **API Keys** in the [Monocle Dashboard](https://app.spur.us/monocle).
2. Open the project in Android Studio and run the `app` configuration on a device or emulator.

The app asks for location permission, then runs an assessment and shows the encrypted result. Location is optional: if you deny it, the assessment runs without it.

## Where to look

- `MonocleExampleApplication.kt`: sets up Monocle once, in the `Application` class.
- `MainActivity.kt`: requests location permission and runs the assessment.
- `AndroidManifest.xml`: the permissions the SDK uses.
- `app/build.gradle.kts`: the `.aar` and the libraries the SDK needs.

## Updating the SDK

Download `monocle.aar` from the latest [SDK release](https://github.com/spurintel/monocle-sdk-android/releases) and replace `app/libs/monocle.aar`.
