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
# 指定代码的压缩级别，值在0-7之间。一般设置5足矣
-optimizationpasses 5

# 打印混淆信息
-verbose

# 代码优化选项，不加该行会将没有用到的类删除，发布的是代码库这个选项需要
# 在做混淆之前最开始会默认对代码进行压缩，为了增加反编译的难度可以选择不压缩
-dontshrink

# 保留参数的名称和方法，该选项可以保留调试级别的属性。
-keepparameternames

# 过滤泛型,出现类型转换错误时再启用这个。目前的项目暂时无泛型类型，我先注释了
#-keepattributes Signature

# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 不混淆资源部分的配置
-keep class **.R$* {*;}

#okhttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment{*;}
-keep public class * extends android.app.Fragment{*;}

-keep class com.pddstudio.preferences.encrypted.**{*;}


-keep class com.mampod.track.sdk.** {
      *;
}


