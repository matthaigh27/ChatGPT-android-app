package com.matthaigh27.chatgptwrapper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.window.OnBackInvokedDispatcher
import com.matthaigh27.chatgptwrapper.databinding.ActivityMainBinding
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : Activity() {
    private val userAgent =
        "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.5615.135 Mobile Safari/537.36"
    private val chatUrl = "https://chat.openai.com/"
    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView
    private lateinit var swipeLayout: SwipeRefreshLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView = binding.webView
        swipeLayout = binding.swipeRefreshLayout

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#343541")

        webView.settings.userAgentString = userAgent
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebViewInterface(this), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url ?: return false

                if (url.toString().contains(chatUrl)) {
                    return false
                }

                if (webView.url.toString().contains(chatUrl) &&
                    !webView.url.toString().contains("/auth")
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                    return true
                }

                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeLayout.isRefreshing = false
                swipeLayout.isEnabled = !(webView.url.toString().contains(chatUrl) &&
                                        !webView.url.toString().contains("/auth"))

                webView.evaluateJavascript(
                    """
                    (() => {
                      navigator.clipboard.writeText = (text) => {
                            Android.copyToClipboard(text);
                            return Promise.resolve();
                        }
                    })();
                    """.trimIndent(),
                    null
                )
            }
        }

        swipeLayout.setOnRefreshListener {
            webView.reload()
        }

        webView.loadUrl(chatUrl)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        if (webView.canGoBack() && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            webView.goBack()
        else
            super.onBackPressed()
    }

    private class WebViewInterface(private val context: Context) {
        @JavascriptInterface
        fun copyToClipboard(text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied!", text)

            clipboard.setPrimaryClip(clip)
        }
    }
}
