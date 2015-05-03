package com.think.memory.ad;

import com.think.memory.Common;
import com.think.memory.util.GetInfo;
import com.winad.android.offers.AdManager;
import com.winad.android.offers.AddScoreListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class YinggaoActivity extends Activity implements AddScoreListener{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		//初始化积分墙SDK
		 AdManager.setAPPID(this, Common.yinggao_key );
		 
		 // 设置积分变化监听，当积分增加时在addScoreSucceed或者addScoreFaild里回调结果
		 AdManager.setAddScoreListener(this, this );

		 
		 // 如果服务器设置回调则需设置userid
		 AdManager.setUserID(this, Common.uid+"" );
		 
		 AdManager.showAdOffers(this) ;
	}
	
	public void onRestart(){
		
		super.onRestart();
		Log.e("yinggao","onRestart");
		
		GetInfo getinfo =new GetInfo(this);
		getinfo.execute();
		
		finish();
		
	}
	
	@Override
	public void addScoreFaild(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addScoreSucceed(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
}
