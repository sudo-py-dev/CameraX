# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK Proguard rules.
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Optimization: More aggressive optimizations for R8
-optimizationpasses 5
-allowaccessmodification

# Remove Log calls in release builds for security and size
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Room: Room needs these to function correctly
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep class * { @androidx.room.ColumnInfo <fields>; }

# Kotlin Serialization / Data classes (if used in future)
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# General: Remove source file attributes for smaller size and obfuscation
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# CameraX, ML Kit, Coil, and Coroutines:
# These libraries provide their own consumer Proguard rules.
# We should NOT use broad -keep class { *; } as it prevents shrinking.
# We only add dontwarn if they have internal resolution issues.
-dontwarn androidx.camera.**
-dontwarn com.google.mlkit.**
-dontwarn coil.**
-dontwarn kotlinx.coroutines.**
-dontwarn androidx.compose.**
