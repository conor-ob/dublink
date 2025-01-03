# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

# Firebase
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# IAP
-keep class com.android.vending.billing.**

# Lucene
-keep class org.apache.lucene.codecs.Codec
-keep class * extends org.apache.lucene.codecs.Codec
-keep class org.apache.lucene.codecs.PostingsFormat
-keep class * extends org.apache.lucene.codecs.PostingsFormat
-keep class org.apache.lucene.codecs.DocValuesFormat
-keep class * extends org.apache.lucene.codecs.DocValuesFormat
-keep class org.apache.lucene.analysis.tokenattributes.**
-keep class org.apache.lucene.**Attribute
-keep class * implements org.apache.lucene.**Attribute

# SimpleXml
-keep public class org.simpleframework.** { *; }
-keep class org.simpleframework.xml.** { *; }
-keep class org.simpleframework.xml.core.** { *; }
-keep class org.simpleframework.xml.util.** { *; }

-keepattributes ElementList, Root

-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}

# Custom behaviour
-keep class com.google.android.material.behavior.** { *; }

# TODO add back in for Aircoach
# Jsoup
#-keep public class org.jsoup.** {
#public *;
#}
#-keeppackagenames org.jsoup.nodes

# TODO add back in for Aircoach
# Mozila Rhino
#-dontwarn sun.**
#-dontwarn org.mozilla.javascript.**
#-keep class org.mozilla.javascript.** { *; }
#-keep class javax.script.** { *; }
#-keep class com.sun.script.javascript.** { *; }
#-keep class org.mozilla.javascript.* { *; }
