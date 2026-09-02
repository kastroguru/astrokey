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

# R8 "full mode" (the default from AGP 8) strips generic signatures from types it is free to rename,
# so `suspend fun search(...): List<NominatimResult>` reaches Retrofit as a bare `Continuation` with
# no type argument. Retrofit then cannot tell what to parse the response into and throws on every
# call — which reached the user as "Търсенето на град не мина" for every city, in a release build
# only. Retrofit 2.9.0's bundled rules predate full mode; these three are what 2.11 ships.
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
