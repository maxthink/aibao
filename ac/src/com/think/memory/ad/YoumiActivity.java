package com.think.memory.ad;


import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.think.memory.R;
import com.think.memory.Common;

public  class YoumiActivity extends Activity implements OnClickListener {
	
//	private WebView webview;
//	private WebSettings setting ;
	
	
	@SuppressWarnings("static-access")
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);
		

		// 请在使用积分墙功能之前进行初始化
		OffersManager.getInstance(this).onAppLaunch();

		// 务必在调用 积分墙的任意接口 之前 调用以下接口设置用户的标识，该标识最终通过订单回调到您的服务器
		OffersManager.getInstance(this).setCustomUserId( Common.uid+"" );

		// 有米Android SDK v4.10之后的sdk还需要配置下面代码，以告诉sdk使用了服务器回调
		OffersManager.getInstance(this).setUsingServerCallBack(true);

		// 关闭积分到账通知栏提示功能
		PointsManager.setEnableEarnPointsNotification(false);

		// 关闭积分到账悬浮框提示功能
		PointsManager.setEnableEarnPointsToastTips(false);

		// (可选)开启用户数据统计服务,(sdk v4.08之后新增功能)默认不开启，传入false值也不开启，只有传入true才会调用
		AdManager.getInstance(this).setUserDataCollect(true);

		// 积分墙配置检查（没有使用“通过 Receiver来获取积分订单”功能）：
		// boolean isSuccess =
		// OffersManager.getInstance(context).checkOffersAdConfig();
		// MyLog.d(TAG, "有米广告SDK配置正确吗？ " + isSuccess);
		
				
		//打开积分墙
		startAd();
		
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
//	    webview.loadUrl("file:///android_asset/ad/offwall_youmi.html");
		
	}

	//打开应用
	public void startAd(){
		
		OffersManager.getInstance(this).showOffersWall();
		
		Log.e("offwall_youmi", "startAd");
		
	}
	
	
	public void onRestart(){
		super.onRestart();
		Log.e("offwall_youmi","onRestart");
		finish();

	}

	/**
	* 退出时回收资源
	*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("offwall_youmi","onDestroy");
		
		// 回收积分广告占用的资源
		OffersManager.getInstance(this).onAppExit();
	}

	@Override
	public void onClick(View arg0) {
		
		
	}
	
	
	
}
