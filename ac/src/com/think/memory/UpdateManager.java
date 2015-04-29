package com.think.memory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.think.memory.R;
import com.think.memory.util.Api;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager {
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		if (isNetworkAvailable(mContext)) {

			UpdateTask updateTask = new UpdateTask();
			updateTask.execute();

		} else {
			Toast.makeText(mContext, "网络不可用...", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	/*
	 * private boolean isUpdate() { // 获取当前软件版本 String versionCode =
	 * Common.VERSION; // 获取版本更新信息
	 * 
	 * String serviceCode = mHashMap.get("version"); // 版本判断 if
	 * (serviceCode.equals(versionCode)) { return true; }
	 * 
	 * return false; }
	 */
	
	
	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog(HashMap<String, String> mHashMap) {

		CustomDialog dialog = new CustomDialog(mContext, R.style.mystyle,R.layout.customdialog2, mHashMap);
		dialog.show();
	}

	// 判断网络连接方法
	public static boolean isNetworkAvailable(Context ctx) {
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 获取版本更新数据
	class UpdateTask extends AsyncTask<Object, Integer, String> {

		private UpdateTask() {
		}

		protected String doInBackground(Object... params) {			
			return Api.updateVersion(Common.version_code);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@SuppressLint("ParserError")
		@Override
		protected void onPostExecute(String result) {
			Log.e("updatemanage", "result: "+result);
			if (result == null)
			{

			}
			else
			{
				try
				{
					JSONObject myresult = null;
					String status = null;
					try
					{
						myresult = new JSONObject(result);
						status = myresult.getString("status");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
					Log.e("updatemanger", "status : "+status);
					if(status.equals("0"))
					{	
						mHashMap = new HashMap<String, String>();
						String version_code = myresult.getString("new_version");
						String upgradeDesc = myresult.getString("desc");
						String dwUrl = myresult.getString("url");
						
						mHashMap.put("version", version_code);
						mHashMap.put("upgradeDesc", upgradeDesc);
						mHashMap.put("dwUrl", dwUrl);
						mHashMap.put("name", Common.appname);
						// 版本对比
						if (!version_code.endsWith(Common.version_code)) {
							showNoticeDialog(mHashMap);	//更新
						}
						
					}
					else if(status.equals("1"))
					{
						//String desc = myresult.getString("desc");
						//Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(mContext, "版本信息获取错误", Toast.LENGTH_SHORT).show();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

}
