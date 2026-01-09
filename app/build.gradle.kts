plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
//    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.51.1"

    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.foodelivery"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodelivery"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true // <--- [1] THÊM DÒNG NÀY

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64") // Cần thiết cho giả lập LDPlayer
            isUniversalApk = false
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- 1. HILT (QUAN TRỌNG: Để dùng @Inject) ---
        implementation("com.google.dagger:hilt-android:2.51.1")
//    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Để inject ViewModel trong Navigation

    // --- 2. FIREBASE (BỔ SUNG CÁC MODULE THIẾU) ---
    // Bạn mới chỉ có database, cần thêm mấy cái này cho Auth/Firestore/Storage
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")

    // --- 3. COROUTINES PLAY SERVICES (QUAN TRỌNG) ---
    // Để dùng được hàm .await() thay vì onSuccessListener
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // --- 4. DATASTORE & GSON (THAY THẾ SQLITE) ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.code.gson:gson:2.10.1")

    // --- 5. NAVIGATION & LIFECYCLE COMPOSE ---
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4") // Hỗ trợ collectAsStateWithLifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // --- 6. COIL (ĐỂ HIỂN THỊ ẢNH TỪ FIREBASE) ---
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Google Maps Compose (Thư viện vẽ Map cho Jetpack Compose)
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")


    // thêm
    val room_version = "2.7.0-alpha07" // Hoặc bản alpha mới nhất
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // [THÊM DÒNG NÀY]: Bắt buộc phải có để sinh code Database
//    kapt("androidx.room:room-compiler:$room_version")

// --- HILT (DÙNG KSP) ---
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1") // [QUAN TRỌNG]: Đổi kapt -> ksp
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- ROOM (DÙNG KSP) ---
    // Sử dụng bản ổn định 2.6.1 để tránh lỗi Alpha, trừ khi bạn bắt buộc cần KMP
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion") // [QUAN TRỌNG]: Đổi kapt -> ksp
    implementation("androidx.multidex:multidex:2.0.1") // <--- [2] THÊM DÒNG NÀY
}