package com.think.memory.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.think.memory.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GetInfo extends AsyncTask<Object, Integer, String> {
	
	private Context context;
	
	public GetInfo(Context context) {
		this.context = context;
	}

	protected String doInBackground(Object... params) {

		String imei = ""; // 获取手机的did

		TelephonyManager tm = (TelephonyManager) context
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
					Common.today		= res.getInt("today");
					Common.total		= res.getInt("total");
					Common.score		= res.getInt("score");						
					Common.uid			= res.getInt("uid");						
					Common.task_count	= res.getInt("task_count");
					Common.exchange_count = res.getInt("exchange_count");
					Common.server_time	= res.getInt("timestamp");
					Common.nick			= res.getString("nick");
					Common.plat			= res.getJSONArray("plat");
					Common.did			= res.getJSONArray("did");
					Common.show_sign	= res.getBoolean("show_sign");
					Common.show_end		= res.getBoolean("show_end");
					
					Common.getInfo		= false; // 设置不用获取用户信息
					Common.show_ok		= true;
					Common.client_time	= (int) (System.currentTimeMillis() / 1000);
					
					
					Log.e("menu", "server time " + Common.server_time);
					Log.e("menu", "client time " + Common.client_time);
					Log.e("menu", "common.uid: "+Common.uid);
					
				} else {
					Common.showMsg(context, "用户信息获取错误", 1000);
				}

			} catch (JSONException ex) {
				Log.e("menu error ", ex.getMessage());
			}

		}
	}
}