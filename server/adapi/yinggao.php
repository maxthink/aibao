<?php
/*
	百赚  赢告无限广告积分接收接口文件
	
	详细查看 文档 词义解释
*/

//key	平台给的唯一标示
//pa	积分, 即用户应该增加多少积分，如该用户应该增加100积分，那么pay=100
//rid	判断字符串，如果您需要判断该用户有没有重复获取积分，可以通过这个值和{0}、{3}来确定。我们服务器已经做了过滤，即，{0}用户获取了{1}积分，积分来源为{2}，积分获取类型为{3}，如果您还需要过滤，可以通过这种方式来过			滤，否则忽略此参数值（注意{3}=1的不能判断）
//n		积分获取类型，值为固定两个： 0：安装广告并且打开应用后获取积分，1个广告只会通知一次  1：签到获取积分，签到每天一个，可能会连续几天
//m		//根据“加密的密匙”和数据生成的md5值，用来确认数据的合法性，具体结构为：MD5({0}+{1}+{2}+{3}+加密密匙)，得出的字符串即为m的值，
/*
Array
(
    [key] => 15311413491
    [pay] => 40
    [rid] => 0c5cf32a0d6a4060b380af168158a146
    [n] => 0
    [m] => 11da88f60822f407f950c5c767937758
    [pn] => com.egame
    [appname] => 爱游戏
)
*/


ini_set('display_errors','on');
error_reporting(E_ALL);

file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );

include_once('../lib/init.php');

$key	= isset($_REQUEST['key'])	? $_REQUEST['key'] : false ;	//手机号
$pay	= isset($_REQUEST['pay'])	? $_REQUEST['pay'] : false ;	//积分, 
$rid	= isset($_REQUEST['rid'])	? $_REQUEST['rid'] : false ;	//订单号, 一个应用一个, 深度任务跟主任务一个订单号, 所以是会重复的
$n		= isset($_REQUEST['n'])		? $_REQUEST['n'] : false ;		// 0, 安装打开获取   1,签到获取积分, 可能好几个
$m		= isset($_REQUEST['m'])		? $_REQUEST['m'] : false ;		//签名, 验证用
$pn		= isset($_REQUEST['pn'])	? addslashes($_REQUEST['pn']) : false ;	//包名
$appname= isset($_REQUEST['appname'])? addslashes($_REQUEST['appname']) : false ;	//应用名

$sig = md5($key.$pay.$rid.$n.$key_yinggao);

//当您接到请求后，如果成功添加积分，请返回字符串”success”，否则为其他(必须)
if($sig!==$m){
	//header('HTTP/1.1 403 Forbidden');
	echo 'sig error';
}
else
{
	$repeat = false;

	//如果是主线任务, 查下是否存在该订单
	if($n==0){
		$sql = "select rid from $record_yinggao where rid='$rid' ";
		$db->query($sql);
		$repeat = $db->getone();
	}
	//var_dump($repeat);

	//不存在订单: 执行添加订单
	if($repeat == false )
	{

		$sql = "insert $record_yinggao set `key`='$key', `pay`=$pay, `rid`='$rid', `n`=$n, `m`='$m', `pn`='$pn', `appname`='$appname' ";
		$db->query($sql);
		$inid = $db->insert_id();
		if($inid)
		{
			//添加记录
			add_order($platform_yinggao, $inid, $key, $pay, $appname );
			//header('HTTP/1.1 200 OK');
			echo 'success';
		}else{
			//header('HTTP/1.1 200 OK');
			echo 'false';
		}
		
	}
	else
	{
		//header('HTTP/1.1 200 OK');
		echo 'repeat';
	}
}


?>
