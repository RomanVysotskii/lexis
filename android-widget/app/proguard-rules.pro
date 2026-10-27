# Keep the JavaScript bridge visible to WebView in release builds if minification is enabled later.
-keepclassmembers class com.lexica.app.MainActivity$WidgetBridge {
    @android.webkit.JavascriptInterface <methods>;
}
