package com.think.memory.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.think.memory.Common;

import android.util.Log;


public class Api  {
	
	private static String key = "442a7704bd4c7fc424e844dc85bd141d"; // md5(aichen);
	private static String assectUrl = "http://123.57.56.8/aibao/api/";
	//private static String assectUrl = "http://123.57.56.8/aibao_t/api/";	//测试
	
	// 加积分接口
	public static String addScore(int platform, int score ) {
		
		String token = "";
		int time =0;
		try {
			int offect = (int) (System.currentTimeMillis() / 1000) - Common.client_time;
			//Common.log("api", "sign offect: "+offect);
			time = Common.server_time+offect;
			token = MD5Checksum.getChecksum4String(Common.uid+"as"+score+key+platform+""+time);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Urlparam[] param = new Urlparam[6];
		param[0] = new Urlparam("token",token+"");
		param[1] = new Urlparam("uid",Common.uid+"");
		param[2] = new Urlparam("score",score+"");
		param[3] = new Urlparam("platform",platform+"");
		param[4] = new Urlparam("versioncode",Common.version_code);
		param[5] = new Urlparam("time",time+"");
		
		Log.e("API addScore: ", "send");
		return Api.send( assectUrl+"addscore.php", param);
	}
	
	// 签到
	public static String sign(){
		String token = "";
		int time =0;
		try {
			int offect = (int) (System.currentTimeMillis() / 1000) - Common.client_time;
			//Common.log("api", "sign offect: "+offect);
			time = Common.server_time+offect;
			token = MD5Checksum.getChecksum4String(Common.uid+key+time);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Urlparam[] param = new Urlparam[4];
		param[0] = new Urlparam("token",token+"");
		param[1] = new Urlparam("uid",Common.uid+"");
		param[2] = new Urlparam("time",time+"");
		param[3] = new Urlparam("versioncode",Common.version_code);
		
		return Api.send( assectUrl+"sign.php", param);
	}

	// 查询用户信息接口
	public static String getInfo(String imei) {

		String token = "";
		try {
			token = MD5Checksum.getChecksum4String(imei+"aic");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Urlparam[] param = new Urlparam[2];
		param[0] = new Urlparam("token",token+"");
		param[1] = new Urlparam("imei",imei);
		//Log.e("API", "get Info");
		return Api.send( assectUrl+"getinfo.php", param);
	}

	// 兑换积分接口
	public static String ExchangeScore( int exchangescore, String type, String account) {

		String token = "";
		int time =0;
		try {
			int offect = (int) (System.currentTimeMillis() / 1000) - Common.client_time;
			//Common.log("api", "sign offect: "+offect);
			time = Common.server_time+offect;
			Log.e("api", "tokenstring: "+Common.uid+key+time);
			token = MD5Checksum.getChecksum4String(Common.uid+key+"sc"+time);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Urlparam[] param = new Urlparam[7];
		param[0] = new Urlparam("token",token);
		param[1] = new Urlparam("uid",Common.uid+"");
		param[2] = new Urlparam("exchangescore",exchangescore+"");
		param[3] = new Urlparam("type",type);
		param[4] = new Urlparam("account",account);
		param[5] = new Urlparam("versioncode",Common.version_code);
		param[6] = new Urlparam("time",time+"");
		
		return Api.send( assectUrl+"exchange.php", param);
		
	}

	// 版本更新接口
	public static String updateVersion(String version) {

		Urlparam[] param = new Urlparam[1];
		param[0] = new Urlparam("version_code",version);
		
		return Api.send( assectUrl+"getversion.php", param);
	}
	
	// 获取账单记录
	public static String getBill(String imei, int pagenum ){
		
		String token = "";
		try {
			token = MD5Checksum.getChecksum4String(imei+key+pagenum);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Urlparam[] param = new Urlparam[3];
		param[0] = new Urlparam("token",token);
		param[1] = new Urlparam("imei",imei);
		param[2] = new Urlparam("pagenum",pagenum+"");
		
		return Api.send( assectUrl+"bill.php", param);
	}
	
	//邀请
	public static String invite(String imei, String invitecode, boolean sure ){
		Urlparam[] param;
		if(sure){
			param = new Urlparam[3];
			param[0] = new Urlparam("imei",imei);
			param[1] = new Urlparam("invitecode",invitecode);
			param[2] = new Urlparam("sure","true");
		}else{
			param = new Urlparam[2];
			param[0] = new Urlparam("imei",imei);
			param[1] = new Urlparam("invitecode",invitecode);
		}
		
		return Api.send( assectUrl+"invite.php", param );
	}
	
	private static String send(String url , Urlparam[] param){
		
		String returnStr = "";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		Log.e("API", "url: "+url);
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for(int i=0; i<param.length; i++){
				Log.e("Api", "param: key: "+param[i].key+" value:"+param[i].value);
				nameValuePairs.add(new BasicNameValuePair(param[i].key, param[i].value));
			}
		
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnStr = EntityUtils.toString(response.getEntity());
			}else{
				Log.e("Api", "http status code: "+response.getStatusLine().getStatusCode() );
				returnStr = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.e("Api", "result: "+returnStr);
		return returnStr;
	}

	
}
