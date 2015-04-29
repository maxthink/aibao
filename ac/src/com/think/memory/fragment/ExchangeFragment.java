package com.think.memory.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.think.memory.R;
import com.think.memory.Common;
import com.think.memory.util.Api;
import com.think.memory.view.LoadingDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class ExchangeFragment extends Fragment {

	private static  WebView webView;
	private String account;
	private LoadingDialog dialog;	//有用, 提交数据时显示
	protected boolean isVisible;
	
	@SuppressLint({ "SetJavaScriptEnabled", "InflateParams" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.e("exchange", "onCreateView");
		
		View view = inflater.inflate(R.layout.fragment_exchange, null);

		webView = (WebView) view.findViewById(R.id.game_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		webView.setWebViewClient(new webClient());
		
		webView.loadUrl("file:///android_asset/payali.html");
		webView.addJavascriptInterface(this, "love");
		
		return view;

	}
	
	private class webClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {

			if (!webView.getSettings().getLoadsImagesAutomatically()) {
				webView.getSettings().setLoadsImagesAutomatically(true);
			}
		}

	}

	
	// 提现请求判断
	@JavascriptInterface
	public void exchange(String user, int num, String category) {
		Log.e("exchange", "user:"+user+" score:"+num+" balance:"+Common.score);
		if (num == 0)
		{
			Toast.makeText(getActivity(), "请选择提现金额", Toast.LENGTH_LONG).show();
			return;
		}
		else if (user == null || user.equals(""))
		{
			Toast.makeText(getActivity(), "请填入账号", Toast.LENGTH_LONG).show();
			return;
		}
		else if (num > Common.score )
		{
			Toast.makeText(getActivity(), "余额不足", Toast.LENGTH_LONG).show();
		}
		else
		{
			
			Common.putConfigStr( getActivity(), "account", user);
			
			String acc = Common.getConfigStr( getActivity(), "account");
			Log.e("exchange", "config account: "+acc);
			
			GetExchange exchange = new GetExchange(category, user, num );
			exchange.execute();
		}

	}

	// 取现
	class GetExchange extends AsyncTask<Object, Integer, String> {

		private String category;

		private String account;

		private int chargemoney;

		private GetExchange(String category, String account, int chargemoney) {

			this.category = category;
			this.account = account;
			this.chargemoney = chargemoney;
			
			dialog = new LoadingDialog(getActivity());
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		protected String doInBackground(Object... params) {

			
			Log.w("eachange", "exchangeScore");
			return Api.ExchangeScore( chargemoney, category, account );

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@SuppressLint("ParserError")
		@Override
		protected void onPostExecute(String result) {
			
			dialog.dismiss();
			
			if (result == null) {
				Toast.makeText(getActivity(), "提现请求失败", Toast.LENGTH_LONG).show();

			} else  {
				
				JSONObject res = null;
				String status = null;
				try {
					res = new JSONObject(result);
					status = res.getString("status");
					String msg = res.getString("msg");
					if (status.equals("0")) {
						Common.showMsg(getActivity(), msg, 4000);
						Common.exchage(chargemoney);
						initSomeData();
					}
					else if(status.equals("400"))
					{
						Common.showMsg(getActivity(), msg, 4000);
					}
				
				}catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
		
	}
	
	
	public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if( getUserVisibleHint() ) {
            initSomeData();
        }
    }
	
	private void initSomeData()
	{
		
		//如果填写过支付宝账号, 默认填写上
		account = Common.getConfigStr( getActivity(),  account);
		Log.e("exchange","common account: "+account);
		
		if(account.equals("")){
			Log.e("exchagne","account is null");
		}else{
			Log.e("exchange","account: "+account);
			webView.loadUrl("javascript:set_account(" + account + ")");
		}
		
		Log.e("exchagne","common.score:"+Common.score);
		webView.loadUrl("javascript:show_score(" + Common.score/Common.rate + ")");
		
	}
	

}

