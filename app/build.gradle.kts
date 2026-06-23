import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    jacoco
}

android {
    namespace = "com.cheminee.metronome"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cheminee.metronome"
        minSdk = 26
        targetSdk = 34
        versionCode = 20
        versionName = "3.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    debugImplementation("androidx.compose.ui:ui-tooling")

    // Provide org.json implementation for JVM unit tests
    implementation("org.json:json:20230227")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
}


jacoco {
    toolVersion = "0.8.11"
}

val coverageSourceDirs = files("src/main/java", "src/main/kotlin")
val buildDirPath = layout.buildDirectory.get().asFile
val coverageClassDirs = files(
    fileTree("${buildDirPath}/tmp/kotlin-classes/debug") {
        exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*")
    },
    fileTree("${buildDirPath}/intermediates/javac/debug/compileDebugJavaWithJavac/classes") {
        exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*")
    }
)

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    executionData.setFrom(fileTree(buildDirPath).include("**/jacoco/*.exec"))
    sourceDirectories.setFrom(coverageSourceDirs)
    classDirectories.setFrom(coverageClassDirs)
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("testDebugUnitTest")
    executionData.setFrom(fileTree(buildDirPath).include("**/jacoco/*.exec"))
    sourceDirectories.setFrom(coverageSourceDirs)
    classDirectories.setFrom(coverageClassDirs)
    violationRules {
        rule {
            element = "BUNDLE"
            includes = listOf(
                "com.cheminee.metronome.data.*",
                "com.cheminee.metronome.data.exporter.*",
                "com.cheminee.metronome.data.importer.*",
                "com.cheminee.metronome.repository.*",
                "com.cheminee.metronome.metronome.*"
            )
            excludes = listOf(
                "com.cheminee.metronome.ui.**",
                "com.cheminee.metronome.MainActivity*"
            )
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn("jacocoTestCoverageVerification")
}
