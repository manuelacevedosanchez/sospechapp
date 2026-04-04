# --- ProGuard rules for Android app with Compose, Retrofit, OkHttp, AdMob ---

# 1. Keep main Android entry points

# 2. Jetpack Compose and AndroidX (only what is needed for reflection/serialization)

# 3. Retrofit and OkHttp (keep only interfaces and annotations)

# 4. AdMob and Google Play Services (keep only what is necessary)

# 5. Keep useful logs and stacktraces


# 7. Optional: Hide original source file name
#-renamesourcefileattribute SourceFile

# --- End of recommended rules ---
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
#
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable
#
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile