buildscript {
    val version_code by extra(13)
    val version_name by extra("poc_v13")

    val compose_version by extra("1.4.0-alpha03")
    val compose_m3_version by extra("1.1.0-alpha03")
    val compose_compiler_version by extra("1.4.0-alpha02")

    val media2_version by extra("1.2.1")
    val accompanist_version by extra("0.28.0")
    val room_version by extra("2.5.0-beta01")

    val librespot_commit by extra("e95c4f0529")
    val hilt_version by extra("2.43.2")
}

plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.21" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}