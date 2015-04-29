package com.think.memory;

import com.think.memory.R;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class ExchangeRecordActivity extends Activity {

	private TextView title;
	private Button m_back_button;
	private WebView webView;
	
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.webview);
		title = (TextView) findViewById(R.id.top_title);

		title.setText("提现记录");

		m_back_button = (Button) findViewById(R.id.top_left);// gua_top_back
		m_back_button.setVisibility(View.VISIBLE);
		m_back_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (arg0.getId() == R.id.top_left) {
					if (webView.canGoBack()) {
						webView.goBack();
					} else {
						finish();
					}
				}
			}
		});

		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new webClient());
		webView.loadUrl("file:///android_asset/exchange_record.html");
		webView.addJavascriptInterface(this, "love");

	}

	private class webClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {

		
			webView.loadUrl("javascript:setuid("+Common.uid+")");
			
			if (!webView.getSettings().getLoadsImagesAutomatically()) {
				webView.getSettings().setLoadsImagesAutomatically(true);
			}
		}

	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	
}
