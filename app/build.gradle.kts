plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("mediation-auto-adapter")
}


android {
    namespace = "com.ithe.ss.imv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ithe.ss.imv"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.clear()
            abiFilters.addAll(arrayOf("armeabi-v7a"))
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.pangle.cn:mediation-sdk:6.6.0.7")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.5.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

//    // ijk
//    implementation("tv.danmaku.ijk.media:ijkplayer-java:0.8.8")
//    implementation("tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.8")

    // androidx-meida
    implementation("androidx.media3:media3-exoplayer:1.4.0")
//    implementation("androidx.media3:media3-exoplayer-dash:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kotlin Coroutines
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    implementation("com.github.donkingliang:LabelsView:1.6.5")
    implementation("com.makeramen:roundedimageview:2.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}