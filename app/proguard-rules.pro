# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keep interface com.swiftctrl.sdk.SwiftCtrlCallback
-keep class com.swiftctrl.sdk.SwiftCtrlSDK
-keep class com.swiftctrl.sdk.connector.SwiftCtrlClient
-keep class com.swiftctrl.sdk.connector.SwiftCtrlActivityClient
-keep class com.swiftctrl.sdk.connector.SwiftCtrlLifecycleClient
-keep class com.google.crypto.tink.subtle.X25519

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class * extends androidx.*

-dontwarn com.swiftctrl.sdk.model.**
-keep class com.swiftctrl.sdk.model.** { *; }
-keep interface com.swiftctrl.sdk.model.** { *; }
-keep enum com.swiftctrl.sdk.model.** { *; }

-dontwarn com.swiftctrl.sdk.datasource.remote.model.**
-keep class com.swiftctrl.sdk.datasource.remote.model.** { *; }
-keep interface com.swiftctrl.sdk.datasource.remote.model.** { *; }
-keep enum com.swiftctrl.sdk.datasource.remote.model.** { *; }

-dontwarn com.swiftctrl.sdk.SwiftCtrlCallback
-keep interface com.swiftctrl.sdk.SwiftCtrlCallback
-dontwarn com.swiftctrl.sdk.SwiftCtrlFullCallback
-keep interface com.swiftctrl.sdk.SwiftCtrlFullCallback
-dontwarn com.swiftctrl.sdk.SwiftCtrlSDK
-keep class com.swiftctrl.sdk.SwiftCtrlSDK
-dontwarn com.swiftctrl.sdk.SwiftCtrlSDK
-keep class Utils

-dontwarn com.google.crypto.**
-keep class com.google.crypto.** { *; }
-keep interface com.google.crypto.** { *; }
-keep enum com.google.crypto.** { *; }
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
  <fields>;
}
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite { *; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Dagger ProGuard rules.
# https://github.com/square/dagger
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection

# Material design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# OkHttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

# Retrofit
-dontwarn retrofit.**
-dontwarn retrofit.appengine.UrlFetchClient
-keep class retrofit.** { *; }
-keepclasseswithmembers class * { *; }

# Naviguation
-keepnames class androidx.navigation.fragment.NavHostFragment

# Room
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource