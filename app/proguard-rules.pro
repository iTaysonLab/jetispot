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
-renamesourcefileattribute
-repackageclasses
-allowaccessmodification

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void checkFieldIsNotNull(...);
    public static void checkParameterIsNotNull(...);
}

-dontusemixedcaseclassnames
-dontwarn kotlin.**

# moshix issues with serialized sealed classes
-keep class bruhcollective.itaysonlab.jetispot.core.objs.** { *; }

# genericjson gson fixes
-keep class bruhcollective.itaysonlab.jetispot.playback.sp.AndroidSinkOutput

# spotify protobuf, probably not needed to keep
#-keep class com.spotify.** {*;}

# default sink
-keep class bruhcollective.itaysonlab.jetispot.playback.sp.AndroidSinkOutput

# native tremolo code
-keepclassmembers class xyz.gianlu.librespot.player.decoders.tremolo.OggDecodingInputStream {
   *;
}

# gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken