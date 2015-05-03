<?php
/*
	可可平台广告积分接收接口文件
	
	详细查看 开发文档

参数名		类型	参数说明 补充
os			string	系统类型 必填项，该值为大写,可选值(‘IOS’,’ANDROID’)
os_version	string	系统版本
mac			string	设备标识 MAC	 地址。 MAC	 地址为大写,无分隔符,（ ios7 以上该值为空）
imei		string	设备标识 广告标识符
imei		string	Android 系统的唯一设备 ID
appid		string	开发者应用 id 可可后台对应的 platform	id
ad_name		string	广告名，如果是应用类广告则为应用名
coins		float	积分数 必填项
adid		int		广告 id 必填项
transactionid int	任务 id 必填项， 用于识别是否会用重复的积分返还请求,同一个任务的积分返还id不变
uid			string	媒体开发人员在可可广告 sdk中设置的 userInfo 值该值由媒体的开发人员在sdk中设置，可可广告平台只负责传递，不关系其具体含义
ip			string	手机客户端的 ip 地址。 必填项
source		string	来源 必填项,渠道来源，该值固定为： adcocoa
sign		string	请求签名 必填项， 签名规则详见补充说明

//示例
Array
(
    [ad_name] => 萌萌德州扑克
    [adid] => 1415
    [appid] => 3061c6cd11cba068b717844a2d8024a8
    [coins] => 70
    [imei] => 359209020227033
    [ip] => 221.123.142.62
    [mac] => 88e3ab773288
    [os] => ANDROID
    [os_version] => 4.2.2
    [source] => adcocoa
    [transactionid] => 795622806
    [uid] => 15311413491
    [sign] => 5dbe149167a2083e16e6bff5d78abc5b
)


*/

ini_set('display_errors','on');
error_reporting(E_ALL);

$secrit_app = 'wxd2rcunjcr4ayexz9vz3ta7tm8suv5m';

file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );

include_once('../lib/init.php');

$ad_name		=$s['ad_name']		= isset($_REQUEST['ad_name'])	? $_REQUEST['ad_name'] : false;
$adid			=$s['adid']			= isset($_REQUEST['adid'])		? intval($_REQUEST['adid']) : false ;
$appid			=$s['appid']		= isset($_REQUEST['appid'])		? $_REQUEST['appid'] : false;
$coins			=$s['coins']		= isset($_REQUEST['coins'])		? intval($_REQUEST['coins']) : false;
$imei			=$s['imei']			= isset($_REQUEST['imei'])		? $_REQUEST['imei'] : false ;	
$ip				=$s['ip']			= isset($_REQUEST['ip'])		? $_REQUEST['ip'] : false ;		//
$mac			=$s['mac']			= isset($_REQUEST['mac'])		? $_REQUEST['mac'] : false ;	//
$os				=$s['os']			= isset($_REQUEST['os'])		? $_REQUEST['os'] : false ;		//
$os_version		=$s['os_version']	= isset($_REQUEST['os_version'])? $_REQUEST['os_version'] : false ;
$source			=$s['source']		= isset($_REQUEST['source'])	? $_REQUEST['source'] : false;
$transactionid	=$s['transactionid']= isset($_REQUEST['transactionid'])	? $_REQUEST['transactionid'] : false ;
$uid			=$s['uid']			= isset($_REQUEST['uid'])		? $_REQUEST['uid'] : false;			//
$sign								= isset($_REQUEST['sign'])		? $_REQUEST['sign'] : false;

foreach($s as $k=>$v){
	if($v===false) unset($s[$k]);
}
ksort($s);
$sig = md5(http_build_query($s).'&secret='.$secrit_app);

if($sig!==$sign){
	header('HTTP/1.1 403 Forbidden');	//由于签名不对, 直接拒绝
}
else
{
	//检查任务是否已存在
	$repeat = false;
	
	$sql = "select transactionid from $record_adcocoa where transactionid=$transactionid ";
	$db->query($sql);
	$repeat = $db->getone();
	
	
	//任务不存在订单: 执行添加任务;  任务存在: 返回 403 状态
	if($repeat === false )
	{
		$mobile = isset($extra_data['mobile']) ? $extra_data['mobile'] : '';

		$sql = "insert $record_adcocoa set 
				`ad_name` = '$ad_name',
				`adid` = $adid,
				`appid` = '$appid',
				`coins` = $coins,
				`imei` = '$imei',
				`ip` = '$ip',
				`mac` = '$mac',
				`os` = '$os',
				`os_version` = '$os_version',
				`source` = '$source',
				`transactionid` = $transactionid,
				`uid` = '$uid',
				`sign` = '$sign' ";

		$db->query($sql);
		$inid = $db->insert_id();
		if($inid)
		{
			//添加记录, coco渠道号规定为 16
			add_order($platform_coco, $inid, $uid, $coins, $ad_name );
			header('HTTP/1.1 200 OK');

		}else{
			header('HTTP/1.1 200 OK');
			echo 'false';
		}
		
	}
	else
	{
		header('HTTP/1.1 403 Forbidden');
	}
}


?>