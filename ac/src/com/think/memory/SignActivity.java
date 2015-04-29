package com.think.memory;

import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.think.memory.R;
import com.think.memory.util.Api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class SignActivity extends Activity {

	private TextView title;
	private Button m_back_button;
	private WebView webView;

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.webview);
		title = (TextView) findViewById(R.id.top_title);

		title.setText("签到");

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
		webView.loadUrl("file:///android_asset/sign.html");
		webView.addJavascriptInterface(this, "love");

		// 插播接口调用
		// 开发者可以到开发者后台设置展示频率，需要到开发者后台设置页面（详细信息->业务信息->无积分广告业务->高级设置）
		// 自4.03版本增加云控制是否开启防误点功能，需要到开发者后台设置页面（详细信息->业务信息->无积分广告业务->高级设置）

	}

	private class webClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {

			if (!webView.getSettings().getLoadsImagesAutomatically()) {
				webView.getSettings().setLoadsImagesAutomatically(true);
			}
		}

	}

	public void sign() {

		Sign sign = new Sign();
		sign.execute();

	}

	// 签到
	class Sign extends AsyncTask<Object, Integer, String> {

		private Sign() {}

		protected String doInBackground(Object... params) {
			return Api.sign();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@SuppressLint("ParserError")
		@Override
		protected void onPostExecute(String result) {
			Log.e("sign", "result: " + result);

			if (result == null) {

			} else if (result.equals("false")) {

			} else {

				JSONObject res = null;
				String status = null;
				try {
					res = new JSONObject(result);
					status = res.getString("status");

					if (status.equals("0")) {
						int score = res.getInt("score");
						Common.showMsg(SignActivity.this, "签到成功, 奖励" + score+ "积分", 5000);
						Common.addScore_sign(score);

						// 展示插播广告，可以不调用loadSpot独立使用
						SpotManager.getInstance(SignActivity.this).showSpotAds
						(
								SignActivity.this, new SpotDialogListener()
								{
									@Override
									public void onShowSuccess()
									{
										Log.e("YoumiAdDemo", "展示成功");
									}

									@Override
									public void onShowFailed() {
										Log.i("YoumiAdDemo", "展示失败");
									}

									@Override
									public void onSpotClosed() {
										Log.i("YoumiAdDemo", "展示关闭");
									}

								}
						);

					} else {
						String msg = res.getString("msg");
						Common.showMsg(SignActivity.this, msg, 1000);
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			}

		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
				webView.goBack();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
