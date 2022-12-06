package com.matthaigh27.chatgptwrapper

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    private val userAgent = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.5359.79 Mobile Safari/537.36"
    private val chatUrl = "https://chat.openai.com/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#343541")

        webView.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView, url: String) {
                if (view is CustomWebView && url == "https://chat.openai.com/backend-api/models") {
                    view.loggedIn = true
                }
                return super.onLoadResource(view, url)
            }
        }

        webView.settings.userAgentString = userAgent
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true

        webView.loadUrl(chatUrl)
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }
}
