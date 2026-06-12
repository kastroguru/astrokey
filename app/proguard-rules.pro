# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Room entities
-keep class eu.kastroguru.astrodiary.data.db.entity.** { *; }

# Keep Moshi models
-keepclassmembers class eu.kastroguru.astrodiary.data.network.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
