buildscript {
    repositories { google(); mavenCentral(); maven("https://jitpack.io") }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("com.github.recloudstream:gradle:81b1d424d2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    }
}
allprojects { repositories { google(); mavenCentral(); maven("https://jitpack.io") } }
subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")
    cloudstream {
        setRepo("https://github.com/adarsh7665-dev/famelack-india-cloudstream")
        description = "Indian live TV channels from the official Famelack public dataset."
        authors = listOf("adarsh7665-dev")
        status = 1
        tvTypes = listOf("Live")
        iconUrl = "https://www.google.com/s2/favicons?domain=famelack.com&sz=%size%"
        isCrossPlatform = true
    }
    android {
        namespace = "com.adarsh7665.famelackindia"
        compileSdk = 35
        defaultConfig { minSdk = 21; targetSdk = 35 }
        buildFeatures.buildConfig = true
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
    dependencies {
        implementation("com.github.recloudstream.cloudstream:library:-SNAPSHOT")
        implementation("com.github.Blatzar:NiceHttp:0.4.11")
        implementation("org.jsoup:jsoup:1.18.3")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    }
}
