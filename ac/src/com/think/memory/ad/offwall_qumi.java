package com.think.memory.ad;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.youmi.android.offers.PointsManager;

import com.newqm.sdkoffer.QuMiConnect;
import com.newqm.sdkoffer.QuMiNotifier;
import com.think.memory.Common;
import com.think.memory.util.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * 趣米平台积分墙代理，积分回调操作在客户端执行
 */
public class offwall_qumi{
	private static final String TAG = "QumiOfferwallAgentImpl";
	
	private static Lock LOCK = new ReentrantLock();
	private static offwall_qumi instance;

	private Context mContext;
	private boolean bInit = false;

	public static offwall_qumi getInstance() {
		if (instance == null) {
			try {
				LOCK.lock();
				if (instance == null) {
					instance = new offwall_qumi();
				}
			} finally {
				LOCK.unlock();
			}
		}
		return instance;
	}

	public void init(Context context) {
		mContext = context;

		// 初始化数据统计，不需要重复调用,调用一次即可
		QuMiConnect.ConnectQuMi( mContext, Common.qumi_key, Common.qumi_secret );
		// 读取积分墙缓存
		QuMiConnect.getQumiConnectInstance(mContext).initOfferAd(mContext);

		// 查询积分
		QuMiConnect.getQumiConnectInstance().showpoints((QuMiNotifier) mContext);
		
		// 增加积分
		// QuMiConnect.getQumiConnectInstance().awardPoints(this, 0);
		
		// 减少积分
		// QuMiConnect.getQumiConnectInstance().spendPoints(this, 0);

		bInit = true;
	}

	public boolean inited() {
		return bInit;
	}

	/**
	 * 在 UI 线程中调用以下代码展示全屏积分墙
	 * 
	 * @param context
	 */
	public void showOffersWall() {
		QuMiConnect.getQumiConnectInstance().showOffers((QuMiNotifier) this);
	}

	public void destroy() {
	}

	/*
	 * earnedPoints:赚取积分接口通知.此接口只会在用户完成积分墙奖励步骤，获得积分奖励时收到通知
	 * 不会在showpoints，awardPoints，spendPoints三个方法被调用时收到通知 pointTotal：积分总数
	 * pointEarned： 用户本次赚取到的积分数
	 */
	public void earnedPoints(int pointTotal, final int pointEarned) {

		Common.log(TAG, "当前积分总数为 = " + pointTotal + ", 本次赚取的积分数为 = " + pointEarned);

		if (pointEarned > 0) {
			new AddScore(pointEarned).execute();
		}
	}

	/*
	 * getPoints：在使用showpoints，awardPoints，spendPoints三个方法，成功时收到通知
	 * pointTotal：积分总数
	 * 
	 * 逻辑如下：
	 * 1.每次从服务端获得全量积分，直接把全量积分作为增量积分传递给百赚客服务端；
	 * 2.调用趣米sdk将全量积分消耗掉。这样保证全量积分即是增量积分。
	 */
	public void getPoints(int pointTotal) {
		
	}

	/*
	 * getPointsFailed：在使用showpoints，awardPoints，spendPoints三个方法，失败时收到通知
	 */
	public void getPointsFailed(String errormessage) {
		// TODO Auto-generated method stub
		Common.log(TAG, "积分查询&增减失败  == " + errormessage);
	}

	class AddScore extends AsyncTask<Object, Integer, String> {
		
		private int score;
		
		private AddScore(int score){
			this.score = score;
		}

		protected String doInBackground(Object... params) {
			if(score > 0 ){
				
				//减去sdk所加积分, 服务器添加新增积分
				boolean isSuccess = PointsManager.getInstance(null).spendPoints(score);	
				if(isSuccess){
					return Api.addScore(Common.platform_qumi, score);	// 最后一个参数是积分墙区分用的,有米的为 1, 趣米的为 2
				}else{
					Log.e("offwall qumi", " spend points false ");
					return null;
				}
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
					Common.showMsg(mContext, "获得积分: "+score, 1000);
					Common.getScore = true;	//积分变动, 信息重新获取
				}else{
					Common.showMsg(mContext, "请求错误", 1000);
				}
			}
		}
	}

}
