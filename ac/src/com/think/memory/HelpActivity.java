package com.think.memory;

import com.think.memory.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends Activity {

	private TextView title;
	private Button m_back_button;
	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.webview);
		title = (TextView) findViewById(R.id.top_title);

		title.setText("帮助中心");

		m_back_button = (Button) findViewById(R.id.top_left);
		m_back_button.setVisibility(View.VISIBLE);
		m_back_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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

		webView.loadUrl("file:///android_asset/help.html");

	}

}
