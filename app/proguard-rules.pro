# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for readable stacktraces in production crash reports
-keepattributes SourceFile,LineNumberTable

# Pierfrancesco Soffritti YouTube Player Rules
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** { *; }
-dontwarn com.pierfrancescosoffritti.androidyoutubeplayer.**

# Retrofit Rules
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# Moshi rules
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# Firebase Notification / Cloud Messaging Rules
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

