<?php
/*
	百赚  易积分 积分接收接口文件 2015/4/2
	
	想法: 文档说order_id是订单号, 可一个应用是一个订单号, 所以不能用order_id 来区分订单信息, 是 order_id + signNum  联合来确定是否是一个有效积分记录, 感觉现在不如用广告id: adId +signNum 来区分有效订单, 索引上空间小

//uuid			易积分的用户 id （mac、imsi、imei号等，标识不同手机的）
//userID		合作方的用户 id （合作方自己定义，用来区分不同户）
//score			此次激活给的分
//exchangetime	效果发生时间( 格式 yyyy-MM-dd HH:mm:ss utf8编码)
//plat			平台类型, 默认0
//appName		应用名称(utf8)
//adId			广告id
//adName		广告名称(utf8)
//packageName	应用包名
//order_id		订单号
//signNum		用户签到标识, 用来区分激活与签到[1:激活 2:签到 3: 第二次签到 ]
//sign			签名方式：sign= MD5(URLDecode(queryString)+appKey)
注:
1, queryString 解释: 开发者获取请求url '?'后面的全部参数(包含键值和"&", 但不包含"&sign="参数); 不需要关注参数顺序;只获取易积分传过去的参数串, 开发者配置回调地址时有自己的参数, 也需要截取掉;
2, queryString 需要url解码
3, appkey 是平台上的发布ID
4, 不建议开发者分别获取参数再进行拼串, 最好从url中获取整段请求参数串并截取, 否则后期增加其他参数, 开发者还要修改接口

Array
(
    [uuid] => b9c8e3f1cb 63e1589b1f dc660c78bb 42
    [userID] => 15311413491
    [score] => 25
    [exchangetime] => 2015-04-01 11:08:48
    [plat] => 0
    [appName] => 百赚客
    [idfa] => 
    [adId] => 11900
    [adName] => 财神锁屏
    [packageName] => com.renhua.screen
    [order_id] => b9c8e3f1cb63e1589b1fdc660c78bb4211900
    [signNum] => 1
    [appChildId] => 
    [appChildName] => 
    [sign] => a060692fb8dc39c9034b210418f81c28
)
    [QUERY_STRING] => uuid=b9c8e3f1cb63e1589b1fdc660c78bb42&userID=15311413491&score=25&exchangetime=2015-04-01+11%3A08%3A48&plat=0&appName=%E7%99%BE%E8%B5%9A%E5%AE%A2&idfa=&adId=11900&adName=%E8%B4%A2%E7%A5%9E%E9%94%81%E5%B1%8F&packageName=com.renhua.screen&order_id=b9c8e3f1cb63e1589b1fdc660c78bb4211900&signNum=1&appChildId=&appChildName=&sign=a060692fb8dc39c9034b210418f81c28

Array
(
    [uuid] => b9c8e3f1cb63e1589b1fdc660c78bb42
    [userID] => 15311413491
    [score] => 7
    [exchangetime] => 2015-04-02 09:44:36
    [plat] => 0
    [appName] => 百赚客
    [idfa] => 
    [adId] => 11900
    [adName] => 财神锁屏
    [packageName] => com.renhua.screen
    [order_id] => b9c8e3f1cb63e1589b1fdc660c78bb4211900
    [signNum] => 2
    [appChildId] => 
    [appChildName] => 
    [sign] => 58dfdc0bdcffb5574e23795922b021ba
)
    [QUERY_STRING] => uuid=b9c8e3f1cb63e1589b1fdc660c78bb42&userID=15311413491&score=7&exchangetime=2015-04-02+09%3A44%3A36&plat=0&appName=%E7%99%BE%E8%B5%9A%E5%AE%A2&idfa=&adId=11900&adName=%E8%B4%A2%E7%A5%9E%E9%94%81%E5%B1%8F&packageName=com.renhua.screen&order_id=b9c8e3f1cb63e1589b1fdc660c78bb4211900&signNum=2&appChildId=&appChildName=&sign=58dfdc0bdcffb5574e23795922b021ba


*/
ini_set('display_errors','on');
error_reporting(E_ALL);

$secret = 'EMSR30ZDDPEI371ONRR5QINDQ80ZC2N9AI';	//

file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );
file_put_contents('kk.txt',print_r($_SERVER,1), FILE_APPEND );

include_once('../lib/init.php');

$uuid			= isset($_REQUEST['uuid'])			? $_REQUEST['uuid'] : false ;		//易积分的用户
$userID			= isset($_REQUEST['userID'])		? $_REQUEST['userID'] : false ;		//合作方的用户
$score			= isset($_REQUEST['score'])			? $_REQUEST['score'] : false ;		//此次激活给的分
$exchangetime	= isset($_REQUEST['exchangetime'])	? $_REQUEST['exchangetime'] : false ;	//效果发生时间
$plat			= isset($_REQUEST['plat'])			? $_REQUEST['plat'] : false ;		//平台类型, 默认0
$appName		= isset($_REQUEST['appName'])		? $_REQUEST['appName'] : false ;	//应用名称
$adId			= isset($_REQUEST['adId'])			? $_REQUEST['adId'] : false ;		//广告id
$adName			= isset($_REQUEST['adName'])		? $_REQUEST['adName'] : false ;		//广告名称
$packageName	= isset($_REQUEST['packageName'])	? $_REQUEST['packageName'] : false ;	//应用包名
$order_id		= isset($_REQUEST['order_id'])		? $_REQUEST['order_id'] : false ;	//订单号
$signNum		= isset($_REQUEST['signNum'])		? intval($_REQUEST['signNum']) : false ;	//用户签到标识
$sign			= isset($_REQUEST['sign'])			? $_REQUEST['sign'] : false ;	//签名方式：sign= MD5(URLDecode(queryString)+appKey)


$sign_pos = strpos($_SERVER['QUERY_STRING'],'&sign=');
$m = substr($_SERVER['QUERY_STRING'],0,$sign_pos);
$sig = md5(urldecode($m).$secret);


if($sig!==$sign){
	
	exit(json_encode(array('message'=>'签名验证错误','success'=>'false')));

}
else
{

	$sql = "select order_id from $record_yijifen where order_id='$order_id' and signNum=$signNum ";
	$db->query($sql);
	$repeat = $db->getone();
	
	//不存在订单: 执行添加订单.
	if($repeat === false )
	{
		
		$sql = "insert $record_yijifen set 
				`uuid`='$uuid', 
				`userID`='$userID', 
				`score` = '$score', 
				`exchangetime` = '$exchangetime',
				`plat` = $plat,  
				`appName` = '$appName', 
				`adId` = $adId, 
				`adName` = '$adName', 
				`packageName` = '$packageName',
				`order_id` = '$order_id', 
				`signNum` = $signNum, 
				`sign` = '$sign' ";

		$db->query($sql);
		$inid = $db->insert_id();
		if($inid)
		{
			//添加记录
			add_order($platform_yijifen, $inid, $userID, $score);
			header('HTTP/1.1 200 OK');
			exit(json_encode(array('message'=>'','success'=>'true')));

		}else{
			header('HTTP/1.1 200 OK');
			exit(json_encode(array('message'=>'处理错误','success'=>'false')));
		}
		
	}
	else
	{
		header('HTTP/1.1 200 OK');
		exit(json_encode(array('message'=>'订单重复','success'=>'false')));
	}
}


?>
