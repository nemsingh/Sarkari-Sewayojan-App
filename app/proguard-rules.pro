# ProGuard & R8 rules for Sarkari Sewayojan

# Keep Firebase Cloud Messaging classes
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

-keep class com.example.data.service.MyFirebaseMessagingService { *; }
-keep class com.example.SewayojanApp { *; }

# Keep Moshi & Room Data Models
-keep class com.example.data.local.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}

# Keep Coroutines
-keepnames class kotlinx.coroutines.** { *; }
