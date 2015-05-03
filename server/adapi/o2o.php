<?php
/*
	百赚  欧拓广告积分接收接口文件
	
	详细查看 http://ad.o2omobi.com/help/sdk_callback_help.html

*/

//type_code 等于activation表示激活，等于sign_in表示签到
//money 表示用户获得的RMB
//unit 表示用户获得的app货币
//openudid 用户设备的唯一标示
//extra_data 额外信息，用户的应用的额外信息(需要用户自己填写)
//order_id 订单id

/*
Array
(
    [type_value] => 3
    [type_code] => activation
    [money] => 0.48
    [unit] => 1920.0
    [openudid] => 9e443faec93854e9
    [extra_data] => {\"imei\": \"359209020227033\"}
    [order_id] => 413cf134e53548123d499c781608b258
)

*/

ini_set('display_errors','on');
error_reporting(E_ALL);



file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );

include_once('../lib/init.php');

$type_code		= isset($_REQUEST['type_code']) ? $_REQUEST['type_code'] : false ;
$money			= isset($_REQUEST['money']) ? $_REQUEST['money'] : false ;
$unit			= isset($_REQUEST['unit']) ? intval($_REQUEST['unit']) : false ;
$openudid		= isset($_REQUEST['openudid']) ? $_REQUEST['openudid'] : false ;		//android 应该是手机imei号
$extra_data		= isset($_REQUEST['extra_data']) ? json_decode(stripslashes($_REQUEST['extra_data']),true) : false ;
$order_id		= isset($_REQUEST['order_id']) ? addslashes($_REQUEST['order_id']) : false;

if($order_id !== false )
{
	//先查下是否存在该订单
	$sql = "select order_id from $record_o2o where order_id='$order_id' ";
	$res = getone($sql);

	//不存在订单: 执行添加订单.  订单存在: 返回 403 状态
	if($res == false )
	{
		$uid = isset($extra_data['uid']) ? $extra_data['uid'] : false;
		
		if($uid!==false)
		{
			$sql = "insert $record_o2o set `order_id`='$order_id', `money`=$money, `unit`=$unit, `openudid`='$openudid', `uid`=$uid ";
			$db->query($sql);
			$inid = $db->insert_id();
			if($inid)
			{
				if($type_code == 'activation' )
				{
					$appname = '欧拓应用激活';
				}else{
					$appname = '欧拓应用签到';
				}

				//添加记录, 欧拓渠道号规定为 1
				add_order($platform_o2o, $inid, $uid, $unit, $appname );
				header('HTTP/1.1 200 OK'); 
			}
			else
			{
				header('HTTP/1.1 404 Not Found'); 
			}
		}else{
			header('HTTP/1.1 403 Forbidden');
		}
		
	}else{
		header('HTTP/1.1 403 Forbidden'); 
	}
}


?>
