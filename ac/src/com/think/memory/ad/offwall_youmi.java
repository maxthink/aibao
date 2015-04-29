package com.think.memory.ad;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsManager;
import net.youmi.android.spot.SpotManager;
import android.content.Context;

import com.think.memory.Common;

public class offwall_youmi {
	
	
	private static Lock LOCK = new ReentrantLock();
	private static offwall_youmi instance;

	private Context mContext;
	private boolean bInit = false;

	public static offwall_youmi getInstance() {
		if (instance == null) {
			try {
				LOCK.lock();
				if (instance == null) {
					instance = new offwall_youmi();
				}
			} finally {
				LOCK.unlock();
			}
		}
		return instance;
	}
	
	
	/**
	 * 初始化有米SDK+有米积分墙
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;

		// 初始化有米SDK
		AdManager.getInstance(mContext).init(Common.youmi_key, Common.youmi_secret, false);

		// 加载插播资源
		SpotManager.getInstance(mContext).loadSpotAds();
		// 插屏出现动画效果，0:ANIM_NONE为无动画，1:ANIM_SIMPLE为简单动画效果，2:ANIM_ADVANCE为高级动画效果
		SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIM_ADVANCE);
		// 设置插屏动画的横竖屏展示方式，如果设置了横屏，则在有广告资源的情况下会是优先使用横屏图。
		SpotManager.getInstance(mContext).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
				
		// 请在使用积分墙功能之前进行初始化
		OffersManager.getInstance(mContext).onAppLaunch();

		// （可选）注册积分监听-随时随地获得积分的变动情况
		// PointsManager.getInstance(context).registerNotify(this);

		// （可选）注册积分订单赚取监听（sdk v4.10版本新增功能）
		// PointsManager.getInstance(context).registerPointsEarnNotify(this);

		// 务必在调用 积分墙的任意接口 之前 调用以下接口设置用户的标识，该标识最终通过订单回调到您的服务器
		OffersManager.getInstance(mContext).setCustomUserId( Common.uid+"");

		OffersManager.getInstance(mContext);
		// 有米Android SDK v4.10之后的sdk还需要配置下面代码，以告诉sdk使用了服务器回调
		OffersManager.setUsingServerCallBack(true);

		// 关闭积分到账通知栏提示功能
		PointsManager.setEnableEarnPointsNotification(false);

		// 关闭积分到账悬浮框提示功能
		PointsManager.setEnableEarnPointsToastTips(false);

		// (可选)开启用户数据统计服务,(sdk v4.08之后新增功能)默认不开启，传入false值也不开启，只有传入true才会调用
		AdManager.getInstance(mContext).setUserDataCollect(true);

		// addCallback(context);

		// 积分墙配置检查（没有使用“通过 Receiver来获取积分订单”功能）：
		// boolean isSuccess =
		// OffersManager.getInstance(context).checkOffersAdConfig();
		// MyLog.d(TAG, "有米广告SDK配置正确吗？ " + isSuccess);

		bInit = true;
	}
	
	public boolean inited() {
		return bInit;
	}

	/**
	 * 销毁有米SDK+有米积分墙
	 *  @param context
	 */
	public void destroy() {
		// （可选）注销积分监听-如果在onCreate注册了，那这里必须得注销
		// PointsManager.getInstance(context).unRegisterNotify(this);

		// （可选）注销积分订单赚取监听-如果在onCreate注册了，那这里必须得注销（sdk v4.10版本新增功能）
		// PointsManager.getInstance(context).unRegisterPointsEarnNotify(this);

		// 在应用退出的地方（如：Activity的onDestroy方法中）调用以下代码进行资源回收
		OffersManager.getInstance(mContext).onAppExit();

		bInit = false;
	}

	/**
	 * 在 UI 线程中调用以下代码展示全屏积分墙
	 * 
	 * @param context
	 */
	public void showOffersWall() {
		OffersManager.getInstance(mContext).showOffersWall();
	}
}
