# --- ProGuard rules for Android app with Compose, Retrofit, OkHttp, AdMob ---

# 1. Keep main Android entry points
-keep class android.support.multidex.** { *; }
-keep class androidx.multidex.** { *; }
-keep class android.support.v4.app.** { *; }
-keep class androidx.appcompat.** { *; }
-keep class android.app.Application { *; }

# 2. Jetpack Compose and AndroidX (reflection/serialization)
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.ViewModelProvider { *; }
-keep class androidx.lifecycle.ViewModelStore { *; }
-keep class androidx.lifecycle.ViewModelStoreOwner { *; }
-keep class androidx.savedstate.** { *; }
-keep class androidx.compose.runtime.saveable.** { *; }
-keep class androidx.compose.ui.platform.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.foundation.** { *; }

# 3. Retrofit and OkHttp (interfaces and annotations)
-keepattributes Signature,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.moshi.**

# 4. AdMob and Google Play Services (only what is necessary)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.measurement.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.google.ads.**

# 5. Keep useful logs and stacktraces
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
# Mantén los logs de warning y error para producción
#-assumenosideeffects class android.util.Log {
#    public static *** w(...);
#    public static *** e(...);
#}

# 6. Debugging: keep line numbers for stacktraces
-keepattributes SourceFile,LineNumberTable

# 7. Optional: Hide original source file name
#-renamesourcefileattribute SourceFile

# 8. Project specific ProGuard rules
# Si usas WebView con JS, descomenta y ajusta:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 9. Mantén clases de modelos serializables si usas Moshi/Gson/Parcelize
#-keepclassmembers class * implements android.os.Parcelable {
#    static ** CREATOR;
#}
#-keepclassmembers class ** {
#    @com.squareup.moshi.Json* <fields>;
#}
#-keepclassmembers class ** {
#    @com.google.gson.annotations.SerializedName <fields>;
#}

# Gson reads only these two asset DTOs through reflection. Keep the scope deliberately narrow.
-keep class com.masmultimedia.sospechapp.words.data.WordsAssetRoot { *; }
-keep class com.masmultimedia.sospechapp.words.data.WordAssetDto { *; }

# 10. Ajustes para Compose Navigation (si usas SafeArgs/NavGraph)
-keep class androidx.navigation.** { *; }

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
