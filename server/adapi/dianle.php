<?php
/*
	百赚  点乐广告 积分接收接口文件
	
	详细查看 文档 词义解释

//snuid			客户端给用户分配的userid, 百赚就是手机号
//device_id		设备号，手机唯一，一个手机对应一个账户
//app_id		在点乐的应用所对应的 DIANLE_APP_ID值
//currency		积分，注意：不是钱
//app_ratio		汇率：1分钱=多少积分(>=1)
//time_stamp	时间戳
//ad_name		广告名
//pack_name		包名
//token			验证密文，其计算方法为：time_stamp（是服务器当前时间戳，毫秒）的值和前面提到的secret_key连接成字符串，然后MD5加密，即：token = MD5(time_stamp + secret_key)。
//task_id		深度任务的唯一标识符  *32位的16进制码  *当trade_type=1时，不出现此字段
//trade_type	表示广告任务的类型 1-安装激活任务 4-次日打开深度任务   *注意：同一广告可产生多次任务，并产生更多收入

Array
(
    [device_id] => 359209020227033
    [snuid] => 15311413491
    [app_id] => 81401f48df554a0c8a7a325d7d93e656
    [currency] => 20
    [app_ratio] => 1
    [trade_type] => 4
    [time_stamp] => 1426667895
    [token] => 359042efb16e98933e36b5d4fc35b32a
    [return_type] => simple
    [ad_name] => 暴走雷电
    [pack_name] => com.zzz.leidian.huoxing
    [order_id] => eb3fbf51182a5949f0b41a266b820fc2
    [task_id] => ee1221212c211852f0115ad85a6279e1
)
Array
(
    [device_id] => 359209020227033
    [snuid] => 15311413491
    [app_id] => 81401f48df554a0c8a7a325d7d93e656
    [currency] => 70
    [app_ratio] => 1
    [trade_type] => 1
    [time_stamp] => 1426667895
    [token] => 359042efb16e98933e36b5d4fc35b32a
    [return_type] => simple
    [ad_name] => 代购现场
    [pack_name] => com.koudai.haidai
    [order_id] => dee6e5206b8e59b1b995d1d4f2fcd2da
)

*/
ini_set('display_errors','on');
error_reporting(E_ALL);


//file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );

include_once('../lib/init.php');

$snuid			= isset($_REQUEST['snuid'])		? intval($_REQUEST['snuid']) : false ;	//手机号
$device_id		= isset($_REQUEST['device_id'])	? $_REQUEST['device_id'] : false ;	//设备号
$app_id			= isset($_REQUEST['app_id'])	? $_REQUEST['app_id'] : false ;	//在点乐的应用所对应的 DIANLE_APP_ID值
$currency		= isset($_REQUEST['currency'])		? $_REQUEST['currency'] : false ;		//积分，注意：不是钱
$app_ratio		= isset($_REQUEST['app_ratio'])		? $_REQUEST['app_ratio'] : false ;		//汇率：1分钱=多少积分(>=1)
$time_stamp		= isset($_REQUEST['time_stamp'])	? $_REQUEST['time_stamp'] : false ;	//时间戳
$ad_name		= isset($_REQUEST['ad_name'])	? addslashes($_REQUEST['ad_name']) : false ;	//广告名
$pack_name		= isset($_REQUEST['pack_name'])	? $_REQUEST['pack_name'] : false ;	//包名
$token			= isset($_REQUEST['token'])		? $_REQUEST['token'] : false ;	//验证密文
$task_id		= isset($_REQUEST['task_id'])	? $_REQUEST['task_id'] : false ;	//深度任务的唯一标识符  *32位的16进制码  *当trade_type=1时，不出现此字段
$trade_type		= isset($_REQUEST['trade_type'])? intval($_REQUEST['trade_type']) : false ;	//1-安装激活任务 4-次日打开深度任务


$sig = md5($time_stamp.$key_dianle);

if($sig!==$token){
	//如果验证参数不匹配，应给点乐服务器返回403 （给点乐返回的403不是http状态码403，而是字符串403）。
	echo '403';
}
else
{
	$repeat = false;

	//如果是主线任务, 查下是否存在该订单
	if($trade_type==1){
		$sql = "select app_id from $record_dianle where app_id='$app_id' ";
		$db->query($sql);
		$repeat = $db->getone();
	}

	//如果是次线任务, 检查task_id 是否存在
	if($trade_type==4){
		
		if($task_id)	//检查task_id 是否存在, 不存在就直接过.
		{
			$sql = "select task_id from $record_dianle where task_id='$task_id' ";
			$db->query($sql);
			$repeat = $db->getone();
		}else{
			$repeat = false;
		}		
	}
	
	//不存在订单: 执行添加订单.  订单存在: 返回 403 状态
	if($repeat === false )
	{
		$mobile = isset($extra_data['mobile']) ? $extra_data['mobile'] : '';

		$sql = "insert $record_dianle set 
				`snuid`='$snuid', 
				`device_id`='$device_id', 
				`app_id` = '$app_id', 
				`currency` = $currency, 
				`app_ratio` = $app_ratio,  
				`time_stamp` = $time_stamp, 
				`ad_name` = '$ad_name', 
				`pack_name` = '$pack_name', 
				`token` = '$token', 
				`task_id` = '$task_id', 
				`trade_type` = $trade_type ";

		$db->query($sql);
		$inid = $db->insert_id();
		if($inid)
		{
			//添加记录, 点乐渠道号规定为 8
			add_order($platform_dianle, $inid, $snuid, $currency, $ad_name);
			header('HTTP/1.1 200 OK');
			//给点乐服务器返回200(给点乐返回的200不是http状态码200，而是字符串200)。
			echo '200';
		}else{
			header('HTTP/1.1 200 OK');
			echo 'false';
		}
		
	}
	else
	{
		header('HTTP/1.1 200 OK');
		echo 'repeat';
	}
}


?>
