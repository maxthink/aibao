package com.think.memory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;
import cn.aow.android.DAOW;

import com.think.memory.R;
import com.think.memory.HelpActivity;
import com.think.memory.fragment.AccountFragment;
import com.think.memory.fragment.ExchangeFragment;
import com.think.memory.fragment.HomeFragment;
import com.think.memory.util.Api;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MenuActivity extends FragmentActivity implements OnClickListener {

	boolean isExit;
	private ViewPager mPager = null;
	private ImageView img1, img2, img3;
	private ImageView Tabimg;
	private RelativeLayout mCursor;
	private int offset = 0;//
	private int bmpW, screenW;//
	private int tabId;
	private AccountFragment accountFragment;
	private HomeFragment homeFragment;
	private ExchangeFragment exchangeFragment;

	Handler mHandler;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu);

		initview();
		initSomeData();

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				isExit = false;
			}

		};

		UpdateManager.isNetworkAvailable(MenuActivity.this);

		// 初始化有米SDK
		AdManager.getInstance(this).init(Common.youmi_key, Common.youmi_secret, false);
		// 加载插播资源
		SpotManager.getInstance(this).loadSpotAds();
		// 插屏出现动画效果，0:ANIM_NONE为无动画，1:ANIM_SIMPLE为简单动画效果，2:ANIM_ADVANCE为高级动画效果
		SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);
		// 设置插屏动画的横竖屏展示方式，如果设置了横屏，则在有广告资源的情况下会是优先使用横屏图。
		SpotManager.getInstance(this).setSpotOrientation( SpotManager.ORIENTATION_PORTRAIT);

		Log.e("duomeng key:",Common.duomeng_key);
		// 初始化多盟积分墙
		DAOW.getInstance(this).init(MenuActivity.this, Common.duomeng_key);
				
		AnalyticsConfig.enableEncrypt(true);	//设置友盟日志加密
		
		///UmengUpdateAgent.update(this);
		//UmengUpdateAgent.silentUpdate(this);	//静默更新
		
		//UpdateConfig.setDebug(true);	//友盟, 测试用, 发布时去掉
        //AnalyticsConfig.setAppkey("54cb087dfd98c53ec10001f0");	//友盟设置appkey
	}
	

	
                  

	// 初始化一些样式
	@SuppressWarnings("deprecation")
	private void initview() {

		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		Tabimg = (ImageView) findViewById(R.id.tabimg);
		mCursor = (RelativeLayout) findViewById(R.id.moveRe);

		mPager.setCurrentItem(0);
		Tabimg.setBackgroundResource(R.drawable.home_press); // 应该是为滑动用的

		img1.setOnClickListener(this);
		img2.setOnClickListener(this);
		img3.setOnClickListener(this);

		bmpW = BitmapFactory
				.decodeResource(getResources(), R.drawable.ad_press).getWidth();
		screenW = getWindowManager().getDefaultDisplay().getWidth();
		offset = (screenW / 3 - bmpW) / 2;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = offset;

		mCursor.setLayoutParams(params);

		mPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));

		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {

				switch (arg0) {

					case 0:
						tabId = R.drawable.home_press;
						break;
					case 1:
						tabId = R.drawable.exchange_press;
						break;
					case 2:
						tabId = R.drawable.account_press;
						break;
				}
				Tabimg.setBackgroundResource(tabId);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

				setText(arg0, arg2, arg1);
				int once = screenW / 3;
				int three = screenW - offset - bmpW;//
				if (arg0 == 0) {
					// mCursor.setX(once * arg1 + offset);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.leftMargin = (int) (once * arg1 + offset); // Your X

					mCursor.setLayoutParams(params);

					setAlpha(arg0, arg2, arg1);
				}

				if (arg0 == 1) {
					// mCursor.setX(once * arg1 + offset + once);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.leftMargin = (int) (once * arg1 + offset + once);

					mCursor.setLayoutParams(params);

					setAlpha(arg0, arg2, arg1);
				}
				if (arg0 == 2) {
					// mCursor.setX(three);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.leftMargin = (int) (three);

					mCursor.setLayoutParams(params);

					setAlpha(arg0, arg2, arg1);
				}

			}

			// arg0:
			public void onPageScrollStateChanged(int arg0) {

				if (arg0 == 2) {
					// IsOver = true;
				}
			}

			private void setText(int arg0, int arg2, float arg1) {
			}

			private void setAlpha(int arg0, int arg2, float arg1) {
			};
		});

	}

	private void initSomeData() {

		checkUpdate();
		
		GetInfo getinfo =new GetInfo();
		getinfo.execute();
	}
	
	//刷新数据
	public  void refreshData()
	{
		GetInfo getinfo =new GetInfo();
		getinfo.execute();
	}

	
	class GetInfo extends AsyncTask<Object, Integer, String> {

		public GetInfo() {
		}

		protected String doInBackground(Object... params) {

			String imei = ""; // 获取手机的did

			TelephonyManager tm = (TelephonyManager) MenuActivity.this
					.getSystemService(Context.TELEPHONY_SERVICE);
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
			Log.e("menu", "onPostExecute: " + result);
			if (result == null || result == "") {
				Log.e("menu", "onPostExecute: null");
			} else if (result.equals("false")) {
				Log.e("menu", "onPostExecute: false");
			} else {

				try {

					JSONObject res = new JSONObject(result);

					String status = res.getString("status");

					if (status.equals("0")) {
						int today = res.getInt("today");
						int total = res.getInt("total");
						int score = res.getInt("score");						
						int uid = res.getInt("uid");						
						int task_count = res.getInt("task_count");
						int exchange_count = res.getInt("exchange_count");
						int servertime = res.getInt("timestamp");
						String nick = res.getString("nick");
						JSONArray ads = res.getJSONArray("ads");
						
						Common.getScore = false; // 设置不用获取用户信息
						
						Common.server_time = servertime;
						Common.client_time = (int) (System.currentTimeMillis() / 1000);
						
						Common.show_ok = true;
						Common.uid = uid;
						Common.score = score;
						Common.today = today;
						Common.total = total;
						Common.nick = nick;
						Common.task_count = task_count;
						Common.exchange_count = exchange_count;
						Common.ads = ads;

						Log.e("menu", "server time " + servertime);
						Log.e("menu", "client time " + Common.client_time);
						
						
					} else {
						Common.showMsg(MenuActivity.this, "用户信息获取错误", 1000);
					}

				} catch (JSONException ex) {
					Log.e("menu", ex.getMessage());
				}

			}
		}
	}

	// 检查软件更新
	private void checkUpdate() {

		UpdateManager manager = new UpdateManager(MenuActivity.this);
		manager.checkUpdate();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	
	public class pagerAdapter extends FragmentPagerAdapter {

		public pagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "1", "2", "3" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {

			case 0:

				if (homeFragment == null) {
					homeFragment = new HomeFragment();
				}
				return homeFragment;

			case 1:

				if (exchangeFragment == null) {
					exchangeFragment = new ExchangeFragment();
				}
				return exchangeFragment;

			case 2:

				if (accountFragment == null) {
					accountFragment = new AccountFragment();
				}
				return accountFragment;

			default:
				return null;
			}
		}

	}

	public void onClick(View v) {
		if (v.getId() == R.id.img1) {
			mPager.setCurrentItem(0);
		}
		if (v.getId() == R.id.img2) {
			mPager.setCurrentItem(1);
		}
		if (v.getId() == R.id.img3) {
			mPager.setCurrentItem(2);
		}
	}


	// 帮助
	public void openHelp() {
		startActivity(new Intent(this, HelpActivity.class));
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {

			if(Common.show_end){
				// 展示插播广告，可以不调用loadSpot独立使用
				SpotManager.getInstance(MenuActivity.this).showSpotAds
				(
						MenuActivity.this, new SpotDialogListener()
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
			
			MobclickAgent.onKillProcess(MenuActivity.this);
			
			 new Handler().postDelayed(new Runnable(){  
			     public void run() {  
			    	Intent intent = new Intent(Intent.ACTION_MAIN);
			 		intent.addCategory(Intent.CATEGORY_HOME);
			 		startActivity(intent);
			 		
			 		System.exit(0);
			     }  
			  }, 2500); 
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
