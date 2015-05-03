<?php
/*
	百赚  有米广告积分接收接口文件
	
	详细查看 http://wiki.youmi.net/Youmi_android_offers_order_callback_protocol




全称		简写	类型	说明
Order ID	order	string	订单ID：该值是唯一的，如果开发者接收到相同的订单号，那说明该订单已经存在。
App ID		app		string	开发者应用ID
Ad Name		ad		string	广告名，如果是应用类型的广告则是应用名  注:参数经过urlencode，请使用urldecode获取原始参数
User ID		user	string	用户ID：对于有米积分墙第二版及以上版本开发者可以设置自己的用户ID，例如开发者的应用是有登录功能的，则可以使用登录后的用户ID来替代有米提供的标识CID（有米为每台设备生成的唯一标识符），否则有米会自动设置为CID。 注:参数经过urlencode，请使用urldecode获取原始参数。

Channel		chn		int		渠道号
Points		points	int		用户可以赚取的积分 如果该值为0，则表示可能用户因为某些情况拿不到积分，例如用户当天激活的次数超过最大的限制值（会和开发者进行结算，但是不会给用户积分）
Signature	sig		string	参数签名，签名算法如下。该值的作用是校验上述参数的完整性，以防止在传输过程中被第三方篡改。

不参与签名的参数:

全称			简写		类型	说明
Adid			adid		int		广告id
Package Name	pkg			String	应用包名
Device ID		device		string	设备ID：android是imei
注:参数经过urlencode，请使用urldecode获取原始参数。

Time			time		int		产生效果的时间
Price			price		float	应用可以获得的收入
Trade_Type		trade_type	int		回调的任务类型。1=>主任务；2=>附加任务(附加任务可能会有多个)
Feedback Param	_fb			字符串	该值是在请求过程中传入的预留参数fb，在此处回调。注意：若请求过程中没有传如fb参数，或fb参数为空，则回调时将不会拼上`_fb`参数，目前SDK不支持`_fb`参数传入

签名算法
$sig = substr(md5($dev_server_secret . "||" . $order . "||" . $app . "||" . $user . "||" . $chn . "||" . $ad . "||" . $points), 12, 8);


Array
(
    [order] => YM150324m2R1hOXQe2
    [app] => f6f4cd68be715a2d
    [ad] => 家记
    [pkg] => com.jiaji
    [user] => 359209020227033
    [chn] => 0
    [points] => 2240
    [price] => 0.56
    [time] => 1427210981
    [device] => 359209020227033
    [adid] => 10902
    [trade_type] => 1
    [sig] => 14a23685
)

*/


ini_set('display_errors','on');
error_reporting(E_ALL);

file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );

include_once('../lib/init.php');

$order			= isset($_REQUEST['order']) ? addslashes($_REQUEST['order']) : false;
$app			= isset($_REQUEST['app']) ? addslashes($_REQUEST['app']) : false;
$ad				= isset($_REQUEST['ad']) ? addslashes($_REQUEST['ad']) : false;
$user			= isset($_REQUEST['user']) ? addslashes($_REQUEST['user']) : false;			//用户id, 我们是手机imei号
$chn			= isset($_REQUEST['chn']) ? intval($_REQUEST['chn']) : false;
$points			= isset($_REQUEST['points']) ? intval($_REQUEST['points']) : false;
$signature		= isset($_REQUEST['sig']) ? $_REQUEST['sig'] : false;


$adid			= isset($_REQUEST['adid']) ? intval($_REQUEST['adid']) : false ;
$pkg			= isset($_REQUEST['pkg']) ? $_REQUEST['pkg'] : false ;		//应用包名
$device			= isset($_REQUEST['device']) ? $_REQUEST['device'] : false ;
$time			= isset($_REQUEST['time']) ? $_REQUEST['time'] : false ;
$price			= isset($_REQUEST['price']) ? json_decode($_REQUEST['price'],true) : false ;
$trade_type		= isset($_REQUEST['trade_type']) ? $_REQUEST['trade_type'] : false ;
//$_fb			= isset($_REQUEST['_fb']) ? $_REQUEST['_fb'] : false ;

$sig = substr(md5($key_youmi . "||" . $order . "||" . $app . "||" . $user . "||" . $chn . "||" . $ad . "||" . $points ), 12, 8);


if($sig !== $signature ){
	header('HTTP/1.1 403 Forbidden');	//由于签名不对, 直接拒绝
}

if($order !== false )
{
	//先查下是否存在该订单
	$sql = "select `order` from $record_youmi where `order`='$order'";
	//echo $sql;
	$res = getone($sql);
	//var_dump($res);
	
	//不存在订单: 执行添加订单.  订单存在: 返回 403 状态
	if($res == false )
	{
		$sql = "insert $record_youmi set 
				`order`='$order', 
				`app`='$app', 
				`ad` = '$ad', 
				`pkg` = '$pkg', 
				`user` = '$user',  
				`chn` = $chn, 
				`points` = $points, 
				`time` = $time, 
				`device` = '$device', 
				`adid` = $adid, 
				`trade_type` = $trade_type, 
				`sig` = '$sig'
				";
		$db->query($sql);
		$inid = $db->insert_id();
		if($inid)
		{

			if($trade_type ==1 ){
				$desc = $ad;
			}else{
				$desc = $ad .' 追加奖励';
			}

			//添加记录, 有米渠道号规定为 2
			add_order($platform_youmi, $inid, $user, $points, $desc );
			header('HTTP/1.1 200 OK'); 
		}else{
			header('HTTP/1.1 404 Not Found'); 
		}
		
	}else{
		header('HTTP/1.1 403 Forbidden'); 
	}
}else{
	header('HTTP/1.1 403 Forbidden'); 
}


?>
