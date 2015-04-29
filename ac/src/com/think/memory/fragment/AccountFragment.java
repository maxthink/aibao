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
			
			GetInfo getinfo =new GetInfo();
			getinfo.execute();
			
			//如果设置最后加载图片, 现在开始加载图片
			if (!webView.getSettings().getLoadsImagesAutomatically()) {
				webView.getSettings().setLoadsImagesAutomatically(true);
			}
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

	class GetInfo extends AsyncTask<Object, Integer, String> {
		
		public GetInfo(){}

		protected String doInBackground(Object... params) {

			String imei=""; // 获取手机的did

			TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			if (tm.getDeviceId() != null) {
				imei = tm.getDeviceId();
			}
			
			return Api.getInfo(imei);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@SuppressLint("ParserError")
		@Override
		protected void onPostExecute(String result) {
			Log.e("account", "onPostExecute: "+result);
			if (result == null || result=="") {
				Log.e("account", "onPostExecute: null");
			} else if (result.equals("false")) {
				Log.e("account", "onPostExecute: false");
			}else{
				
				try {

				    JSONObject res = new JSONObject(result);
				    
				    String status = res.getString("status");

				    if(status.equals("0"))
				    {				    	
				    	double today			= res.getInt("today") ;
				    	double total			= res.getInt("total") ;
				    	double score			= res.getInt("score") ;
					    //String invitecode	= res.getString("invitecode");
					    int uid				= res.getInt("uid");
					    String nick 		= res.getString("nick");
					    int task_count 		= res.getInt("task_count");
					    int exchange_count	= res.getInt("exchange_count");
					    int servertime = res.getInt("timestamp");
					    
					    Log.e("account", "task count:"+task_count);
					    Log.e("account", "server time "+servertime);
					    Log.e("account", "client time "+Common.client_time);
					    
					    webView.loadUrl( "javascript:initInfo("+today/Common.rate+","+total/Common.rate+","+score/Common.rate+")" );
					    webView.loadUrl( "javascript:initInfo2("+task_count+","+exchange_count+")" );
					   
					    
					    Common.getScore = false;	//设置不用获取用户信息
					    Common.server_time = servertime;
					    Common.client_time = (int) (System.currentTimeMillis()/1000);
					   
					    Common.uid = uid;
					    Common.score = score;
					    Common.today = today;
					    Common.total = total;
					    Common.task_count = task_count;
					    Common.exchange_count = exchange_count;
					    
					    if(nick.equals("")){
					    	webView.loadUrl("javascript:show_setnick()");
					    }
					    
				    }else{
				    	Common.showMsg(getActivity(), "用户信息获取错误", 1000);
				    }
				   
				    
				} catch (JSONException ex) {  
				    Log.e("", ex.getMessage());
				}
				
			}
		}
	}


	// 打开帮助
	@JavascriptInterface
	public void openHelp()
	{
		startActivity(new Intent(getActivity(), HelpActivity.class ));
	}
	
	
	public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if( getUserVisibleHint() ) {
            //initSomeData();
        }else{
        	Log.e("account", "visib: false ");
        }
    }
	
	private void initSomeData()
	{
		if(Common.show_ok){
			webView.loadUrl( "javascript:initInfo("+Common.today/Common.rate+","+Common.total/Common.rate+","+Common.score/Common.rate+")" );
			webView.loadUrl( "javascript:initInfo2("+Common.task_count+","+Common.exchange_count+")" );
		}
	}

}

