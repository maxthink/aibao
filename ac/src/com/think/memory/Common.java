package com.think.memory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.think.memory.util.Api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Common {
	
	//客户端数据
	public final static String appname = "爱赚宝 - 手机赚钱";
	public final static String VERSION = "V1.01";
	public final static String version_code = "2";
	public final static String config = "config";
	public final static int rate = 100;			//积分/rmb 的比率, 就是多少积分兑换多少人民币, 设置为100, 就是100积分为一元人民币
	public static int client_time = 1;		//客户端时间
	public static int server_time = 1;		//服务器时间
	
	// 用户信息
	public static boolean show_ok = false;
	public static int uid = 0; 				//uid
	
	public static double score = 0;			//当前金币
	public static double today = 0;			//今天赚取的金币
	public static double total = 0;			//总共赚取过的金币
	public static int task_count = 0;		//任务总数
	public static int exchange_count = 0;	//提现次数
	public static String mobile = "";
	public static String myinvitecode = "";
	public static String nick = "";
	public static boolean is_sign = false;
	public static JSONArray plat ;
	public static JSONArray did ;
	
	//用户信息是否用刷新
	public static boolean getInfo = true;	
	
	// 广告显示配置
	public static boolean show_sign = false;
	public static boolean show_end	= false;
	
	
	/*
	$platform_o2o		= 1;	//欧拓		(server)
	$platform_youmi		= 2;	//有米		(server)
	$platform_yinggao	= 3;	//赢告无限	(server)
	$platform_duomeng	= 4;	//多盟
	$platform_qumi		= 5;	//趣米		客户端发送积分
	$platform_wanpu		= 6;	//万普		客户端发送积分
	$platform_dianle	= 7;	//点乐		(server)
	$platform_guomeng	= 8;	//果盟
	$platform_coco		= 9;	//adcocoa	(server)
	*/
	
	public final static int platform_o2o 			= 1;
	public final static int platform_youmi 			= 2;
	public final static int platform_yinggao 		= 3;
	public final static int platform_duomeng 		= 4;
	public final static int platform_qumi 			= 5;
	public final static int platform_wanpu 			= 6;
	public final static int platform_dianle 		= 7;
	public final static int platform_guomeng 		= 8;
	
	/*
	 * 各平台key ,SECRET
	 */
	public final static String youmi_key	= "2a460bb8674551c5";	//memory
	public final static String youmi_secret	= "5edbf88c43509cdb";
	
	public final static String o2o_key		= "f7d367c4899311e4a526f8bc123d7e98";	//dmkj 的key
	
	public final static String duomeng_key	= "96ZJ1MbgzeY/fwTDD2";	//memory
	
	public final static String qumi_key		= "5ffa0c3b0ccf9f46";	//memory
	public final static String qumi_secret	= "5ee2970d39946ae0";
	
	public final static String yinggao_key	= "BF83D33A5F97BF6737E4DCC7B6EC777E3C1953F7";	//memory
	
	
	
	
	private static Lock LOCK = new ReentrantLock();
	private static Common instance;

	private boolean bInit = false;

	public static Common getInstance() {
		if (instance == null) {
			try {
				LOCK.lock();
				if (instance == null) {
					instance = new Common();
				}
			} finally {
				LOCK.unlock();
			}
		}
		return instance;
	}
	
	public boolean inited() {
		return bInit;
	}
	
	
	
	public static TelephonyManager getTelephonyManager(Context context) {
		if (context == null)
			return null;
		TelephonyManager telephonyManager = null;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager;
	}

	public static String getImsi(Context context) {
		String imsi = getTelephonyManager(context).getSubscriberId();
		return imsi;
	}
	
	public static void addScore_task(int score){
		Common.today += score;
		Common.total += score;
		Common.score += score;
		Common.task_count += 1;
		Common.getInfo = true;
	}
	
	public static void addScore_sign(int score){
		Common.today += score;
		Common.total += score;
		Common.score += score;
		Common.getInfo = true;
		Common.is_sign = true;
	}
	
	public static void exchage(int score){
		Common.exchange_count +=1;
		Common.score -= score;
		Common.getInfo = true;
	}


	/**
	 * Toast的封装
	 * @param mContext 上下文，来区别哪一个activity调用的
	 * @param msg 你希望显示的值。
	 */
	public static void showMsg(Context mContext,String msg,int time)
	{
		Toast toast=new Toast(mContext);
		toast=Toast.makeText(mContext,msg, time);
		//toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);//设置居中
		toast.show();
	}
	
	public static void log(String c , String d )
	{
		Log.e(c,d);
	}
	
	//系统配置数据操作
	public static void putConfigStr(Context mContext,String field, String value){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(Common.config, 0);
		sp.edit().putString(field, value).commit();
	}
	public static void putConfigInt(Context mContext,String field, int value){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(Common.config, 0);
		sp.edit().putInt(field, value).commit();
	}
	public static String getConfigStr(Context mContext, String field)
	{
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(Common.config, 0);
		String s=sp.getString(field,"");//如果该字段没对应值，则取出字符串 ""
		return s;
	}
	public static int getConfigInt(Context mContext, String field)
	{
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(Common.config, 0);
		int s=sp.getInt(field,0);//如果该字段没对应值，则取出字符串 ""
		return s;
	}
		
		
		
	/**@param mContext 上下文，来区别哪一个activity调用的
	 * @param whichSp 使用的SharedPreferences的名字
	 * @param field SharedPreferences的哪一个字段
	 * @return
	 */
	//取出whichSp中field字段对应的string类型的值
	public static String getSharePreStr(Context mContext,String whichSp,String field)
	{
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(whichSp, 0);
		String s=sp.getString(field,"");//如果该字段没对应值，则取出字符串 ""
		return s;
	}
	//取出whichSp中field字段对应的int类型的值
	public static int getSharePreInt(Context mContext,String whichSp,String field)
	{
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(whichSp, 0);
		int i=sp.getInt(field,0);//如果该字段没对应值，则取出0
		return i;
	}
	//保存string类型的value到whichSp中的field字段
	public static void putSharePreStr(Context mContext,String whichSp,String field,String value)
	{
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(whichSp, 0);
		sp.edit().putString(field, value).commit();
	}
	//保存int类型的value到whichSp中的field字段
	public static void putSharePreInt(Context mContext,String whichSp,String field,int value)
	{
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(whichSp, 0);
		sp.edit().putInt(field, value).commit();
	}
}
