package com.think.memory.fragment;


import org.json.JSONException;
import org.json.JSONObject;

import com.think.memory.R;
import com.think.memory.Common;
import com.think.memory.ExchangeRecordActivity;
import com.think.memory.HelpActivity;
import com.think.memory.IncomeActivity;
import com.think.memory.util.Api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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


public class AccountFragment extends Fragment {

	private static  WebView webView;
	protected boolean isVisible;
	
	@SuppressLint({ "SetJavaScriptEnabled", "InflateParams" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account, null);
		
		webView = (WebView) view.findViewById(R.id.game_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		webView.setWebViewClient(new webClient());
		
		webView.loadUrl("file:///android_asset/account.html");
		webView.addJavascriptInterface(this, "love");
		
		return view;

	}
	
	public class webClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			
			//GetInfo getinfo =new GetInfo();
			//getinfo.execute();
			
			//如果设置最后加载图片, 现在开始加载图片
			if (!webView.getSettings().getLoadsImagesAutomatically()) {
				webView.getSettings().setLoadsImagesAutomatically(true);
			}
			
			initSomeData();
		}

	}
	

	
	// 积分记录
	public void openIncome()
	{
		Log.e("account","openIncome");
		startActivity(new Intent(getActivity(), IncomeActivity.class ));
	}
	
	//兑换记录
	public void openExchangeRecord()
	{
		Log.e("account","openExchangeRecord");
		startActivity(new Intent(getActivity(), ExchangeRecordActivity.class ));
	}

	// 打开帮助
	@JavascriptInterface
	public void openHelp()
	{
		startActivity(new Intent(getActivity(), HelpActivity.class ));
	}
	
	
	private void initSomeData()
	{
		if(Common.show_ok){
			webView.loadUrl( "javascript:initInfo("+Common.today/Common.rate+","+Common.total/Common.rate+","+Common.score/Common.rate+")" );
			webView.loadUrl( "javascript:initInfo2("+Common.task_count+","+Common.exchange_count+")" );
		}
	}

}

