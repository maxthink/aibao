package com.think.memory.fragment;

import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.aow.android.DAOW;
import cn.aow.android.DListener;

import com.otomod.wall.OffersManager;
import com.think.memory.IncomeActivity;
import com.think.memory.R;
import com.think.memory.Common;
import com.think.memory.ad.QumiActivity;
import com.think.memory.ad.YinggaoActivity;
import com.think.memory.ad.YoumiActivity;
import com.think.memory.util.Api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class HomeFragment extends Fragment {

	private static WebView webView;
	
	@SuppressLint({ "SetJavaScriptEnabled", "InflateParams", "JavascriptInterface" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, null);

		webView = (WebView) view.findViewById(R.id.game_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.setWebViewClient(new webClient());

		webView.loadUrl("file:///android_asset/home.html");
		webView.addJavascriptInterface(this, "love");

		return view;

	}

	public class webClient extends WebViewClient {
		
		@Override
		public void onPageFinished(WebView view, String url) {

			showAds();
			
			// 如果设置最后加载图片, 现在开始加载图片
			if (!webView.getSettings().getLoadsImagesAutomatically()) {
				webView.getSettings().setLoadsImagesAutomatically(true);
			}
		}

	}
	
	@JavascriptInterface
	private void showAds(){

		if(Common.plat != null){
			
			for (int i = 0; i < Common.plat.length(); i++) {
				try {
					JSONObject temp = (JSONObject) Common.plat.get(i);
					String name = temp.getString("name");
					String color = temp.getString("color");
					String platid = temp.getString("plat_id");
					Common.log("", "setad: name:"+name+" color:"+color+" platid:"+platid);
					webView.loadUrl("javascript:setAd('"+name+"','"+color+"',"+platid+")");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		
	}
	
	

	// 打开广告应用
	public void startAd(int ad) throws JSONException {
		Log.e("home", "startAd " + ad);
		switch (ad) {
		
		case 1: // o2o
			
			if( allow(1) ){
				// startActivity(new Intent(getActivity(), O2oActivity.class));
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("uid", Common.uid + "");
				OffersManager.showOffers(getActivity(), Common.o2o_key, jsonObject);
			}else{
				Common.showMsg(getActivity(), "该平台任务做的够多了,换个平台吧.",  3000);
			}
			break;
		case 2: // youmi
			if( allow(2) ){
				startActivity(new Intent(getActivity(), YoumiActivity.class));
			}else{
				Common.showMsg(getActivity(), "该平台任务做的够多了,换个平台吧.",  3000);
			}
			
			break;
		case 3: // 赢告
			if( allow(3) ){
				startActivity(new Intent(getActivity(), YinggaoActivity.class));
			}else{
				Common.showMsg(getActivity(), "该平台任务做的够多了,换个平台吧.",  3000);
			}
			
			break;
		case 4: // duomeng
			if( allow(4) ){
				DAOW.getInstance(getActivity()).show(getActivity());
			}else{
				Common.showMsg(getActivity(), "该平台任务做的够多了,换个平台吧.",  3000);
			}
			
			break;
		case 5: // qumi
			if( allow(5) ){
				startActivity(new Intent(getActivity(), QumiActivity.class));
			}else{
				Common.showMsg(getActivity(), "该平台任务做的够多了,换个平台吧.",  3000);
			}
			break;

		default:
			break;
		}
	}
	
	private boolean allow(int plat){
		
		try {

			for (int i = 0; i < Common.plat.length(); i++) {
				JSONObject jo = (JSONObject) Common.plat.opt(i);
				
				int id = jo.getInt("id");
				Common.log("homefragment", "common.plat id:"+id);
				if(id==plat){
					
					
					int allow = jo.getInt("allow_num");
					Common.log("homefargment", "找到相同的平台配置id:"+id+" 的allow :"+allow);
					
					for (int j = 0; j < Common.did.length(); j++) {
						JSONObject did = (JSONObject) Common.did.opt(j);
						int platid = did.getInt("platform");
						Common.log("homefragment", "did id:"+platid);
						
						if(platid==id){
							int tc = did.getInt("tc");
							Common.log("homefargment", "获取到相同的did 次数统计:"+tc);
							if(tc<allow){
								return true;
							}else{
								return false;
							}
						}
					}
					
					Common.log("", "没有找到该平台已做任务数据, 默认返回 true");
					return true;	//应该是还没有做该平台任务, 所以返回 true, 允许做任务.					
				}

			}
			
		} catch (JSONException e) {
			Common.log("homefragment: ", "parseContentList exception: " + e.getMessage());
		}
		
		Common.log("homefragment", "try error ");
		return true;
	}

	public void duomeng_checkPoints() {

		// 监听积分墙分数变化
		DAOW.getInstance(getActivity()).checkPoints(new DListener() {
			@Override
			public void onResponse(Object... point) {
				// 用户总的积分数
				Integer totalPoint = (Integer) point[1]; // 用户的已消费积分数
				Integer consumPoint = (Integer) point[0]; // 用户的剩余积分数
				int pointEarned = totalPoint - consumPoint;
				Log.e("duomeng", "总积分:" + totalPoint + "\n已消费积分:" + consumPoint
						+ "\n剩余积分:" + pointEarned);
				if (pointEarned > 0) {

					new AddScore(Common.platform_duomeng, pointEarned).execute();

					DAOW.getInstance(getActivity()).consumePoints(pointEarned,new DListener() {

								@Override
								public void onResponse(Object... point){}

								@Override
								public void onError(String errorInfo) {}

							}
					);

				}
			}

			@Override
			public void onError(String errorInfo) {

			}

		});
	}

	class AddScore extends AsyncTask<Object, Integer, String> {

		private int score;
		private int platform;

		private AddScore(int platform, int score) {
			this.score = score;
			this.platform = platform;
		}

		protected String doInBackground(Object... params) {
			if (score > 0) {
				return Api.addScore( platform, score);
			} else {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null) {
				Common.showMsg(getActivity(), "请求错误", Toast.LENGTH_LONG );
			} else {
				Log.e("onPostExecute", result);
				if (result.equals("true")) {
					Common.showMsg(getActivity(), "获得积分: " + score, Toast.LENGTH_LONG );
					Common.addScore_task(score);
				} else {
					Common.showMsg(getActivity(), "请求错误", 1000);
				}
			}
		}
	}
	
	// 签到
	@JavascriptInterface
	public void sign() {
		//startActivity(new Intent(getActivity(), SignActivity.class));
		Log.e("homefragment","is sign: "+Common.is_sign);
		if(Common.is_sign==false){
			Sign sign = new Sign();
			sign.execute();
		}else{
			Common.showMsg(getActivity(), "今日已签到", 1000);
		}
		
	}
	
	// 签到
	@SuppressLint("ShowToast")
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
				int status = 400;
				try {
					res = new JSONObject(result);
					status = res.getInt("status");
					String msg = res.getString("msg");

					if (status == 0) {
						int score = res.getInt("score");
						Common.showMsg(getActivity(), msg, Toast.LENGTH_LONG );
						Common.addScore_sign(score);
						
						if(Common.show_sign){
							show_chapin();
						}
						startActivity(new Intent(getActivity(), IncomeActivity.class));

					} else {
						Common.showMsg(getActivity(), msg, Toast.LENGTH_LONG );
						
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			}

		}

	}
	
	
	public void show_chapin(){
		
		// 展示插播广告，可以不调用loadSpot独立使用
		SpotManager.getInstance(getActivity()).showSpotAds
		(
				getActivity(), new SpotDialogListener()
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
		
	}


	public void onStart() {
		super.onStart();
		Log.e("home", "onstart");

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.e("home", "onresume");
		super.onResume();
		
		showAds(); //显示广告列表
		
		duomeng_checkPoints();

	}

	public void onPause() {
		super.onPause();
		Log.e("home", "onPause");
	}

}
