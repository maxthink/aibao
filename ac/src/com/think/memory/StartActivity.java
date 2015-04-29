package com.think.memory;

import com.think.memory.R;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UpdateConfig;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class StartActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	//设置屏幕竖向显示
        setContentView(R.layout.start);
        
       
        
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 1000);
        
    }
    class splashhandler implements Runnable{

        public void run() {
        	
            startActivity(new Intent(StartActivity.this, MenuActivity.class));
            finish();
        }
        
    }
    
    @Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);	//友盟统计
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);	//友盟统计
	}
	
	
}
