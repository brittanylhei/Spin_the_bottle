plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.spinthebottle"
    compileSdk = 35 // Note: Use 35 for now. 36 is likely a preview/beta version.

    defaultConfig {
        applicationId = "com.example.spinthebottle"
        minSdk = 24
        targetSdk = 35 // Match compileSdk
        versionCode = 1
        versionName = "1.0"
    }


    dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation("nl.dionsegijn:konfetti-xml:2.0.4")
    }
}