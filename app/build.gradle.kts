plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.safeargs)
}

android {
    namespace = "ioannapergamali.savejoannepink"
    compileSdk = 34

    defaultConfig {
        applicationId = "ioannapergamali.savejoannepink"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

configurations.all {
    exclude(group = "xmlpull", module = "xmlpull")
    exclude(group = "xpp3", module = "xpp3")
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation("androidx.compose.ui:ui-graphics:1.0.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.google.accompanist:accompanist-glide:0.15.0")
    implementation("com.google.accompanist:accompanist-insets:0.15.0")
    implementation("io.coil-kt:coil-compose:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.foundation:foundation-layout-android:1.6.4")
    implementation("androidx.compose.foundation:foundation:1.6.4")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.safe.args.generator)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.github.bumptech.glide:glide:4.12.0") {
        exclude(group = "xmlpull", module = "xmlpull")
    }
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("androidx.compose.compiler:compiler:1.5.1")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.compose.material:material:1.6.4")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.runtime:runtime:1.6.4")
    implementation("androidx.compose.animation:animation:1.6.4")
    implementation(libs.coil.kt.coil.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:x.y.z")
}
