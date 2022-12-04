import java.io.FileInputStream
import java.util.Properties
import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.zacsweers.moshix") version "0.18.3"
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.protobuf") version "0.8.18"
}
apply(plugin = "dagger.hilt.android.plugin")

val versionMajor = 0
val versionMinor = 1
val versionPatch = 2
val versionBuild = 0
val isStable = true

val compose_version: String by rootProject.extra
val compose_m3_version: String by rootProject.extra
val compose_compiler_version: String by rootProject.extra
val media2_version: String by rootProject.extra
val accompanist_version: String by rootProject.extra
val room_version: String by rootProject.extra
val librespot_commit: String by rootProject.extra
val hilt_version: String by rootProject.extra

val keystorePropertiesFile = rootProject.file("keystore.properties")

val splitApks = !project.hasProperty("noSplits")

android {
    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            getByName("debug")
            {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    compileSdk = 33
    defaultConfig {
        applicationId = "bruhcollective.itaysonlab.jetispot"
        minSdk = 23
        targetSdk = 33
        versionCode = 1000
        versionName = StringBuilder("${versionMajor}.${versionMinor}.${versionPatch}").apply {
            if (!isStable) append("-beta.${versionBuild}")
        }.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
        if (!splitApks)
            ndk {
                (properties["ABI_FILTERS"] as String).split(';').forEach {
                    abiFilters.add(it)
                }
            }
    }
    if (splitApks)
        splits {
            abi {
                isEnable = !project.hasProperty("noSplits")
                reset()
                include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                isUniversalApk = false
            }
        }
    //source sets in .kts
    sourceSets {
        getByName("main") {
            java.srcDir("src/main/libs")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("release")
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
        debug {
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
    }

    compileOptions {
       // coreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose_compiler_version
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Jetispot-${defaultConfig.versionName}-${name}.apk"
        }
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/*.version"
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/kotlinx/**"
            excludes += "**/*.properties"
            excludes += "DebugProbesKt.bin"
            excludes += "kotlin-tooling-metadata.json"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "log4j2.xml"
            excludes += "**.proto"
        }
    }
    namespace = "bruhcollective.itaysonlab.jetispot"
}

moshi {
    // Opt-in to enable moshi-sealed, disabled by default.
    enableSealed.set(true)
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")


    // AndroidX
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha01")

    // Compose
    implementation("androidx.navigation:navigation-compose:2.5.2")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.material3:material3:$compose_m3_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.compose.ui:ui-util:$compose_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    debugImplementation("androidx.customview:customview:1.2.0-alpha02")
    debugImplementation("androidx.customview:customview-poolingcontainer:1.0.0")

    // Compose - Additions
    implementation("com.google.accompanist:accompanist-navigation-material:$accompanist_version")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist_version")
    implementation("com.google.accompanist:accompanist-pager:$accompanist_version")

    // Images
    implementation("io.coil-kt:coil-compose:2.2.0")

    // DI
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")

    // Playback
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.4")
    implementation("com.gitlab.mvysny.slf4j:slf4j-handroid:1.7.30")
    implementation("androidx.media2:media2-session:$media2_version")
    implementation("androidx.media2:media2-player:$media2_version")

    // Librespot
    implementation("com.github.iTaysonLab.librespot-java:librespot-player:$librespot_commit:thin") {
        exclude(group = "xyz.gianlu.librespot", module = "librespot-sink")
        exclude(group = "com.lmax", module = "disruptor")
        exclude(group = "org.apache.logging.log4j")
    }

    // Data - Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-protobuf:2.9.0")

    // Data - SQL
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Data - Proto
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-java:3.21.5")
    implementation("com.tencent:mmkv:1.2.14")
}

//https://stackoverflow.com/questions/65390807/unresolved-reference-protoc-when-using-gradle-protocol-buffers
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.5"
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    //option("lite")
                }
            }
        }
    }
}
