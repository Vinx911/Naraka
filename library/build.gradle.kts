plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("maven-publish")
}

android {
    namespace = "com.vinx911.compose"
    compileSdk = libs.versions.compile.sdk.version.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.version.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }

}

dependencies {
    implementation(androidx.corektx)
    implementation(androidx.appcompat)
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.vinx911"
            artifactId = "Naraka"
            version = "1.0.0"
            description = "每一个App死掉(崩溃)后，都应该进入地狱(Naraka)."

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
