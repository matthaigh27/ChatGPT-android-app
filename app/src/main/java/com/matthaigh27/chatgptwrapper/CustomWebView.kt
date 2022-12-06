package com.matthaigh27.chatgptwrapper

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebView

class CustomWebView(context: Context, attrs: AttributeSet?) : WebView(context, attrs) {
    var loggedIn : Boolean = false

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val inputConnection = super.onCreateInputConnection(outAttrs)
        if (!loggedIn || inputConnection == null) {
            return BaseInputConnection(this, false)
        }

        outAttrs.inputType = EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE
        outAttrs.imeOptions = EditorInfo.IME_ACTION_GO

        return inputConnection
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }
}