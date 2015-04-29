package com.think.memory.ad;

import org.json.JSONException;
import org.json.JSONObject;

import cn.aow.android.DAOW;
import cn.aow.android.DListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.think.memory.Common;
import com.think.memory.util.Api;

public class DuomengActivity extends Activity {

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		DAOW.getInstance(this).show(DuomengActivity.this);
	}

	public void onRestart() {
		super.onRestart();
		Log.e("duomeng", "onRestart");
		checkPoints();

	}

	public void onResume() {
		super.onResume();
		Log.e("duomeng", "onResume");
		//checkPoints();

	}
	
	public void onPause()
	{
		super.onPause();
		Log.e("duomeng", "onPause");
		checkPoints();
	}
	
	public void onDestory()
	{
		super.onDestroy();
	}

	public void checkPoints() {

		
		// 监听积分墙分数变化
		DAOW.getInstance(DuomengActivity.this).checkPoints(new DListener() {
			@Override
			public void onResponse(Object... point) {

				Integer totalPoint = (Integer) point[1]; // 用户的已消费积分数
				Integer consumPoint = (Integer) point[0]; // 用户的剩余积分数
				int pointEarned = totalPoint - consumPoint;
				Log.e("duomeng", "总积分:" + totalPoint + "\n已消费积分:" + consumPoint + "\n剩余积分:" + pointEarned);
				if (pointEarned > 0) {

					AddScore addScore = new AddScore(pointEarned);
					addScore.execute();

					DAOW.getInstance(DuomengActivity.this).consumePoints(
							pointEarned, new DListener() {

								@Override
								public void onResponse(Object... point) {
								}

								@Override
								public void onError(String errorInfo) {
									// TODO Auto-generated method stub
								}

							});

				} else {
					finish();
				}

			}

			@Override
			public void onError(String errorInfo) {

			}

		});
	}

	
	class AddScore extends AsyncTask<Object, Integer, String> {

		private int score;

		private AddScore(int score) {
			this.score = score;
		}

		protected String doInBackground(Object... params) {
			if (score > 0) {

				// 减去sdk所加积分, 服务器添加新增积分
				Log.e("send http: add score", "true");
				return Api.addScore( Common.platform_duomeng, score );

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
				Common.showMsg(DuomengActivity.this, "请求错误", 4000);
			} else  {
				
				JSONObject res = null;
				String status = null;
				try {
					res = new JSONObject(result);
					status = res.getString("status");
					String msg = res.getString("msg");
					if (status.equals("0")) {
						Common.showMsg(DuomengActivity.this, msg, 4000);
						Common.addScore_task(score);
					}
					else if(status.equals("400"))
					{
						Common.showMsg(DuomengActivity.this, msg, 4000);
					}
				
				}catch (JSONException e) {
					e.printStackTrace();
				}

			}
			
		}
	}

}
