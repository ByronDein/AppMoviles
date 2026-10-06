import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Aquí se leen los datos de la llave para firmar el APK.
// El archivo keystore.properties NO se sube a Git (mira keystore.properties.example).
val archivoKeystore = rootProject.file("keystore.properties")
val datosKeystore = Properties()
if (archivoKeystore.exists()) {
    datosKeystore.load(FileInputStream(archivoKeystore))
}

android {
    namespace = "com.example.conectasenas"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.conectasenas"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (archivoKeystore.exists()) {
                storeFile = rootProject.file(datosKeystore["storeFile"] as String)
                storePassword = datosKeystore["storePassword"] as String
                keyAlias = datosKeystore["keyAlias"] as String
                keyPassword = datosKeystore["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Si existe keystore.properties el APK sale firmado
            if (archivoKeystore.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests {
            // Necesario para que Robolectric encuentre los recursos
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // Firebase (autenticación y base de datos)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Geolocalización para la pantalla Buscar dispositivo
    implementation(libs.play.services.location)

    // Pruebas unitarias
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)

    // Pruebas en el celular / Firebase Test Lab
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// El plugin de Google Services necesita el archivo google-services.json
// (se descarga desde la consola de Firebase y se deja en la carpeta app/).
// Si todavía no existe, la app compila igual pero Firebase no va a funcionar.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
