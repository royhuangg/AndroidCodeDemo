# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

-printmapping mapping.txt

-dontskipnonpubliclibraryclassmembers
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.vending.licensing.ILicensingService

-optimizationpasses 5
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
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

-keep class android.os.** { *; }
-dontwarn android.os.**

-keep class libcore.icu.** { *; }
-dontwarn libcore.icu.**

-keep class libcore.icu.** { *; }
-dontwarn libcore.icu.**

-keep class com.google.** { *; }
-dontwarn com.google.**
-keep class * extends android.content.DialogInterface.**{ *; }
-dontwarn android.content.DialogInterface.**

#-keep class org.apache.** { *; }
#-dontwarn org.apache.**

-keep class org.json.** { *; }
-dontwarn org.json.**

-dontwarn android.support.**

-keep class android.support.** { *; }
-keep interface android.support.** { *; }
#Support v7 compact
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
#Support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#Support RecyclerView
-keep public class * extends android.support.v7.widget.RecyclerView.ItemDecoration{ *; }
-keep class android.support.v7.widget.RecyclerView{ *; }
-keep class * extends android.support.v7.widget.RecyclerView.ViewHolder{ *; }

-keep class javax.naming.** { *; }
-dontwarn javax.naming.**
-keep class com.grasea.grandroid.** { *; }
-dontwarn com.grasea.grandroid.**
-keepattributes Signature
-keepattributes InnerClasses

-dontwarn android.support.**
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
#lambda
-dontwarn java.lang.invoke.*

#okhttp3
-dontwarn okhttp3.**
-dontwarn okio.**

-dontwarn com.squareup.picasso.**

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#DataBinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

#Grandroid MVP Framework
-keepclassmembers public class * extends com.grasea.grandroid.mvp.GrandroidPresenter {
   public <init>(...);
}

-keepclassmembers class ** {
  @com.grasea.grandroid.api.Callback public *;
}
-keepclassmembers class ** {
  @com.grasea.grandroid.api.RequestFail public *;
}
-keepclassmembers class ** {
  @com.grasea.grandroid.api.Backend public *;
}
-keepclassmembers class ** {
  @com.grasea.grandroid.adapter.ItemConfig public *;
}

-keepclassmembers public class * extends com.grasea.grandroid.adapter.GrandroidRecyclerAdapter {
   public <init>(...);
}
-keepclassmembers public class * extends android.support.v4.app.Fragment {
   public <init>(...);
}
#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# GreenDao rules
# Source: http://greendao-orm.com/documentation/technical-faq
#
### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
 }
 -keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**
-keep class **$Properties
########--------Retrofit + RxJava--------#########
-dontwarn retrofit.**
-dontwarn retrofit2.Platform$Java8
-keep class retrofit.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
#-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
#-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}
-keep class net.gotev.uploadservice.** { *; }
#Picasso
-dontwarn com.squareup.okhttp.**
#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**
-keep class com.codevscolor.materialpreference.** { *; }
# ALSO REMEMBER KEEPING YOUR MODEL CLASSES
-keep class tw.yalan.cafeoffice.api.** { *; }
-keep class tw.yalan.cafeoffice.common.City { *; }
-keep class tw.yalan.cafeoffice.model.** { *; }
-keep class tw.yalan.cafeoffice.filter.** { *; }
-keep class tw.yalan.cafeoffice.data.** { *; }
-keep class tw.yalan.cafeoffice.adpter.** { *; }

-dontwarn com.codevscolor.materialpreference.R$id