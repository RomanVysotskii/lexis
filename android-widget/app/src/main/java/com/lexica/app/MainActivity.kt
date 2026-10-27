package com.lexica.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.ValueCallback
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val filePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        filePathCallback?.onReceiveValue(uri?.let { arrayOf(it) } ?: emptyArray())
        filePathCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = true
        webView.settings.setSupportZoom(false)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                if (callback == null) return false
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                return try {
                    filePicker.launch(arrayOf("*/*"))
                    true
                } catch (_: Exception) {
                    filePathCallback = null
                    callback.onReceiveValue(null)
                    false
                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(webView) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)
        }
        webView.addJavascriptInterface(WidgetBridge(this), "Android")
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!::webView.isInitialized) { finish(); return }
                webView.evaluateJavascript("document.querySelector('.screen.active')?.id === 'screen-home'") { result ->
                    if (result == "true") finish()
                    else webView.evaluateJavascript("document.querySelector('.screen.active [data-go=\"home\"]')?.click()", null)
                }
            }
        })
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            filePathCallback?.onReceiveValue(null)
            filePathCallback = null
            webView.removeJavascriptInterface("Android")
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }

    class WidgetBridge(private val context: Context) {
        @JavascriptInterface
        fun updateStreak(count: Int, lastDay: String) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_STREAK, count.coerceAtLeast(0))
                .putString(KEY_LAST_DAY, lastDay)
                .apply()
            StreakWidgetProvider.refresh(context)
        }
    }

    companion object {
        const val PREFS = "lexica_widget"
        const val KEY_STREAK = "streak_count"
        const val KEY_LAST_DAY = "last_day"
    }
}
