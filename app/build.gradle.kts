plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.smartcampusrecruitment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smartcampusrecruitment"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
    configurations.all {
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.apache.poi", module = "poi-ooxml-full")
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(files("libs\\MPAndroidChart-v3.0.3.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.belerweb:pinyin4j:2.5.0")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")
    implementation ("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation ("org.java-websocket:Java-WebSocket:1.5.1")

    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
    // PDF 库
    implementation("com.github.barteksc:AndroidPdfViewer:3.1.0-beta.1") {
        exclude(group = "com.android.support", module = "support-v4")
    }

// POI 核心
    implementation("org.apache.poi:poi:5.2.2")
    implementation("org.apache.poi:poi-ooxml:5.2.2") {
        exclude(group = "org.apache.xmlbeans", module = "xmlbeans")
    }
    implementation("org.apache.xmlbeans:xmlbeans:5.1.1")

    // 2. xdocreport 把 POI 文档 -> PDF（内部用 iText 2.1.7）
    implementation("fr.opensagres.xdocreport:fr.opensagres.poi.xwpf.converter.pdf:2.0.4") {
        exclude(group ="org.apache.poi", module = "poi-ooxml-full")
        exclude(group = "org.apache.poi", module = "poi-ooxml")
    }


// Commons 工具库
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("commons-beanutils:commons-beanutils:1.9.4") {
        exclude(group = "commons-collections")
    }
    implementation("org.apache.commons:commons-math3:3.6.1")

}