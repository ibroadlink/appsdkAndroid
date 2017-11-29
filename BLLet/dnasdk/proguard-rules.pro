# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

####################################################################################################
# 系统默认部分
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-dontwarn android.support.**

####################################################################################################
# 通用部分
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#--------------------------
# 保护类型   -keepattributes 说明
# Exceptions, Signature, Deprecated, SourceFile, SourceDir, LineNumberTable, LocalVariableTable, 
# LocalVariableTypeTable, Synthetic, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, 
# RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, and AnnotationDefault
# --------------------
-keepattributes Exceptions,LineNumberTable,InnerClasses,Signature

####################################################################################################
# 工程自定义部分
-dontshrink

-keep class cn.com.broadlink.sdk.BLLet {
    *;
}

-keep class cn.com.broadlink.sdk.BLLet$* {
    *;
}

-keep class cn.com.broadlink.sdk.constants.** {
    *;
}

-keep class cn.com.broadlink.sdk.data.** {
    *;
}

-keep class cn.com.broadlink.sdk.interfaces.** {
    *;
}

-keep class cn.com.broadlink.sdk.param.** {
    *;
}

-keep class cn.com.broadlink.sdk.result.** {
    *;
}

-keep class cn.com.broadlink.econtrol.dataparse.** {
    *;
}

-keep class org.liquidplayer.webkit.javascriptcore.** {
    *;
}

-keep class cn.com.broadlink.sdk.js.** {
    *;
}

-keep class cn.com.broadlink.blirdaconlib.** {
    *;
}

-keep class cn.com.broadlink.networkapi.** {
    *;
}
