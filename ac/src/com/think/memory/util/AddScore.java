package com.think.memory.util;

import com.think.memory.Common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AddScore extends AsyncTask<Object, Integer, String> {
	
	private Context context;
	private int score;
	private int platform;

	public AddScore(Context context, int platform, int score) {
		this.context = context;
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
			Common.showMsg(context, "请求错误", Toast.LENGTH_LONG );
		} else {
			Log.e("onPostExecute", result);
			if (result.equals("true")) {
				Common.showMsg(context, "获得积分: " + score, Toast.LENGTH_LONG );
				Common.addScore_task(score);
			} else {
				Common.showMsg(context, "请求错误", 1000);
			}
		}
	}
}
