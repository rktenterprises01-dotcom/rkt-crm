package com.rktenterprises.crm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private var uploadCallback: ValueCallback<Array<Uri>>? = null
    private val url = "https://script.google.com/macros/s/AKfycbxDBAiYK4sKb5rro1skSjZiFmNsbe1cXdigTNbmKDwaO9Q-ysa6HpPTnA9g2Hs8kKQeHw/exec"

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)
        webView=findViewById(R.id.webView); progress=findViewById(R.id.progress)
        webView.settings.javaScriptEnabled=true
        webView.settings.domStorageEnabled=true
        webView.settings.databaseEnabled=true
        webView.settings.allowFileAccess=true
        webView.webViewClient=object: WebViewClient(){
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                val u=r.url.toString()
                if(u.startsWith("tel:")) { startActivity(Intent(Intent.ACTION_DIAL,Uri.parse(u))); return true }
                return false
            }
        }
        webView.webChromeClient=object: WebChromeClient(){
            override fun onProgressChanged(v: WebView,p: Int){ progress.visibility=if(p<100) View.VISIBLE else View.GONE }
            override fun onShowFileChooser(v: WebView, cb: ValueCallback<Array<Uri>>, params: FileChooserParams): Boolean {
                uploadCallback?.onReceiveValue(null); uploadCallback=cb
                return try { startActivityForResult(params.createIntent(),9001); true } catch(e:Exception){ uploadCallback=null; false }
            }
        }
        if(state==null) webView.loadUrl(url) else webView.restoreState(state)
    }
    override fun onActivityResult(req:Int,res:Int,data:Intent?){
        super.onActivityResult(req,res,data)
        if(req==9001){ uploadCallback?.onReceiveValue(if(res==RESULT_OK && data!=null) WebChromeClient.FileChooserParams.parseResult(res,data) else null); uploadCallback=null }
    }
    override fun onSaveInstanceState(out:Bundle){ webView.saveState(out); super.onSaveInstanceState(out) }
    @Deprecated("Deprecated in Java") override fun onBackPressed(){ if(webView.canGoBack()) webView.goBack() else super.onBackPressed() }
}
