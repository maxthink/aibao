package com.think.memory.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.newqm.sdkoffer.QuMiConnect;
import com.newqm.sdkoffer.QuMiNotifier;
import com.think.memory.R;
import com.think.memory.Common;
import com.think.memory.util.Api;
import com.think.memory.util.GetInfo;

public class QumiActivity extends Activity implements OnClickListener,QuMiNotifier {

	//private WebView webview;
	//private WebSettings setting ;
	
	
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" }) @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);
		Log.e("qumi", "oncreate");
		
		QuMiConnect.ConnectQuMi(this, Common.qumi_key, Common.qumi_secret ); // 初始化数据统计，不需要重复调用,调用一次即可

		QuMiConnect.getQumiConnectInstance(this).initOfferAd(this); // 积分墙广告初始化 ,读取缓冲
		//打开应用
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
//	    webview.loadUrl("file:///android_asset/page/ad/offwall_youmi.html");
	}
	
	public void onRestart(){
		super.onRestart();
		
		Log.e("qumi", "onRestart");
		
		Common.getInfo=true;	//重新获取用户信息去
		
		finish();

	}
	
	public void onResume(){
		super.onResume();
		Log.e("qumi", "onResume");
	}
	
	
	//打开应用
	public void startAd(){
		Log.e("start ad", "qumi");
		QuMiConnect.getQumiConnectInstance().showOffers(QumiActivity.this);
	}
	
	@Override
	public void getPoints(int pointTotal) {
		Log.e("getPoints", pointTotal+"");
		//减去sdk所加积分, 服务器添加新增积分
		//QuMiConnect.getQumiConnectInstance().spendPoints(QumiActivity.this,pointTotal);
	}

	@Override
	public void getPointsFailed(String arg0) {
		// TODO Auto-generated method stub
		Log.e("qumi getPointFailed, param: ", arg0);
	}
	
	/*
	 * earnedPoints:赚取积分接口通知.此接口只会在用户完成积分墙奖励步骤，获得积分奖励时收到通知
	 * 不会在showpoints，awardPoints，spendPoints三个方法被调用时收到通知 
	 * pointTotal：积分总数
	 * pointEarned： 用户本次赚取到的积分数
	 */
	@Override
	public void earnedPoints(int pointTotal, int pointEarned) {
		
		//Toast.makeText(getBaseContext(), "获取到添加积分:"+pointEarned, Toast.LENGTH_SHORT).show();
		Log.e("qumi earnedPoints: ", "pointTotal:"+pointTotal+"; pointEarned"+pointEarned+"");
		
		new AddScore(pointEarned).execute();
		
		
	}
	
	class AddScore extends AsyncTask<Object, Integer, String> {
		
		private int score;
		
		private AddScore(int score){
			this.score = score;
		}

		protected String doInBackground(Object... params) {
			if(score > 0 ){
				
				Log.e("send http: add score", score+"");
				return Api.addScore(Common.platform_qumi, score);
				
			}else{
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
				Log.e("qumi addScore onPostExecute ", "result = null");
			}else{
				Log.e("onPostExecute", result);
				if( result.equals("true") ){
					
					Toast.makeText(QumiActivity.this, "获得积分: "+score, Toast.LENGTH_SHORT).show();
					Common.getInfo = true;	//积分变动, 信息重新获取
					
					//减去sdk所加积分, 服务器添加新增积分
					QuMiConnect.getQumiConnectInstance().spendPoints(QumiActivity.this, score);
				}else{
					Toast.makeText(QumiActivity.this, "请求错误", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDestroy(){
		super.onDestroy();
		Log.e("qumi", "onDestroy");
	}
	
	
}
