# Modern Android App ProGuard Rules
# Optimized for Jetpack Compose and Hilt

-android

# Print out the full proguard config used for every build
-printconfiguration build/outputs/fullProguardConfig.pro

# Kotlin metadata and reflection
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# Hilt and Dependency Injection
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Jetpack Compose
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# Keep annotation classes
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}