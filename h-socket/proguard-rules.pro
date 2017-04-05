# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Hansion\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

-libraryjars 'C:\Program Files\Java\jdk1.8.0_77\jre\lib\rt.jar' #jdk中的文件

-libraryjars 'C:\Users\Hansion\AppData\Local\Android\Sdk\platforms\android-25\android.jar'  #sdk中的文件

-optimizationpasses 5

-dontusemixedcaseclassnames

-keep class com.hansion.h_socket.tcp.* {

public <fields>;

public <methods>;

}

-keep class com.hansion.h_socket.tcp.bean.** { *; } #实体类不混淆

-keep class com.hansion.h_socket.tcp.listener.** { *; } #监听不混淆

-keep class com.hansion.h_socket.tcp.data.** { *; } #数据设置不混淆

-keepattributes Exceptions,InnerClasses,... #内部类不混淆
-keep class com.hansion.h_socket.tcp.conn.ConnConfig{ *; }
-keep class com.hansion.h_socket.tcp.conn.ConnConfig$* { *; }

-keepattributes EnclosingMethod #避免报内部类的警告