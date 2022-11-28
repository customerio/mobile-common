plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("maven-publish")
    kotlin("plugin.serialization") version "1.7.20"
    id("co.touchlab.faktory.kmmbridge") version "0.3.2"
}

repositories {
    google()
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

kmmbridge {
    githubReleaseArtifacts()
    githubReleaseVersions()
    spm()
    versionPrefix.set("0.1")
}

sqldelight {
    // Read more on gradle options on https://cashapp.github.io/sqldelight/multiplatform_sqlite/gradle/
    database("CioDatabase") {
        packageName = "io.customer.shared.local"
        // The directory where to store '.db' schema files to verify migrations
        schemaOutputDirectory = file("src/main/sqldelight/databases")
        // Migration files will fail during compilation if there are errors in them.
        verifyMigrations = true
    }
}

group = "io.customer.shared"
version = "1.0"

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val sqlDelightVersion = "1.5.3"
        val ktorVersion = "2.1.2"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        all {
            // Removes warning from all (common, android and ios) modules when using internal Customer.IO
            // apis that are made public to share between modules, but, are intended to be used by
            // Customer.IO SDKs only.
            // See [InternalCustomerIOApi] for more details.
            languageSettings.optIn(annotationName = "io.customer.shared.internal.InternalCustomerIOApi")
        }

        targets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()
            .forEach {
                it.binaries.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
                    .forEach { lib ->
                        lib.isStatic = false
                        lib.linkerOpts.add("-lsqlite3")
                    }
            }
    }
}

android {
    namespace = "io.customer.shared"
    compileSdk = 32
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}
