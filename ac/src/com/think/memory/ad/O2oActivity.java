package com.think.memory.ad;

import org.json.JSONException;
import org.json.JSONObject;

import com.otomod.wall.OffersManager;
import com.think.memory.R;
import com.think.memory.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
//import android.webkit.WebSettings;
//import android.webkit.WebView;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class O2oActivity extends Activity {

	//private WebView webview;
	//private WebSettings setting ;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);
		
		//说明页
//		webview = (WebView)this.findViewById(R.id.webview);
//
//		setting = webview.getSettings();
//		setting.setJavaScriptEnabled(true);
//		setting.setDefaultTextEncodingName("utf-8");
//		setting.setCacheMode(WebSettings.LOAD_DEFAULT);	//设置缓存模式
//
//	    webview.addJavascriptInterface(this, "love");
//
//	    webview.loadUrl("file:///android_asset/ad/o2o.html");
		startAd();
	}
	
	//打开应用
	public void startAd(){

		try {
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("uid", Common.uid);
			OffersManager.showOffers(this, Common.o2o_key, jsonObject );
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("o2oActivity", "open wall error ..");
		}
		
	}
	
	public void onRestart()
	{
		super.onRestart();
		finish();
		
	}
	
	
}
