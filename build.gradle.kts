import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("com.google.dagger.hilt.android")
}

layout.buildDirectory.set(file("/private/tmp/my-wallet-movie-app-build"))

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

val tmdbAccessToken = providers.gradleProperty("TMDB_ACCESS_TOKEN").orNull
    ?: localProperties.getProperty("TMDB_ACCESS_TOKEN")
    ?: ""

android {
    namespace = "com.ivy.movie"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ivy.movie"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "TMDB_ACCESS_TOKEN",
            "\"${tmdbAccessToken.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        )
    }

    val javaVersion = libs.versions.jvm.target.get()
    kotlinOptions { jvmTarget = javaVersion }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDirs(files("/private/tmp/movie-room-schemas"))
    }
}

room {
    schemaDirectory("/private/tmp/movie-room-schemas")
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.kotlin.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.activity)
    implementation(libs.bundles.room)
    implementation(libs.bundles.hilt)
    implementation(libs.compose.coil)
    implementation("androidx.navigation3:navigation3-runtime:1.1.0")
    implementation("androidx.navigation3:navigation3-ui:1.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    ksp(libs.room.compiler)
    ksp(libs.hilt.compiler)
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.room.testing)

    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(libs.room.testing)

    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.10.0")
}
