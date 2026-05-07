# Ivy Movies

Simple Android app for browsing movies and TV series. The app uses TMDB for remote content and Room for saved items.

## Stack

- Kotlin
- Jetpack Compose + Material 3
- Jetpack Navigation 3
- Hilt
- Clean Architecture with data, domain, and presentation layers
- Coroutines + Flow
- Retrofit + OkHttp
- Gson
- Room
- JUnit and Android instrumented tests

## API Token

Create or update `local.properties` in the project root:

```properties
sdk.dir=/Library/Android/sdk
TMDB_ACCESS_TOKEN=your_tmdb_read_access_token
```

Use the TMDB API Read Access Token, the long token that starts with `eyJ...`.

The app also accepts the token as a Gradle property:

```bash
./gradlew assembleDebug -PTMDB_ACCESS_TOKEN=your_tmdb_read_access_token
```

If the token is blank, the app uses local demo data so the UI can still run.

## Login

Use local credentials:

```text
admin / admin
```

## Build And Test

```bash
./gradlew testDebugUnitTest assembleDebug compileDebugAndroidTestKotlin connectedDebugAndroidTest --no-configuration-cache
```

`connectedDebugAndroidTest` requires a running emulator or connected Android device.
