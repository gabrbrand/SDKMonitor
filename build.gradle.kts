import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.51.0"
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
}

subprojects {
    tasks.withType<KotlinJvmCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers"
            )
        }
    }

    plugins.withType<BasePlugin> {
        configure<BaseExtension> {
            compileSdkVersion(AndroidConfig.compileSdk)
            defaultConfig {
                minSdk = AndroidConfig.minSdk
                targetSdk = AndroidConfig.targetSdk
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
                encoding = "UTF-8"
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
