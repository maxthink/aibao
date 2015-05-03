<?php

ini_set("display_errors","On");
error_reporting(E_ALL);

include_once('../lib/init.php');


file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );
//file_put_contents('kk.txt',print_r($_SERVER,1), FILE_APPEND );


$uid		= isset($_REQUEST['uid']) ? ($_REQUEST['uid']) : false;
$token		= isset($_REQUEST['token']) ? $_REQUEST['token'] : false;
$time		= isset($_REQUEST['time']) ? $_REQUEST['time'] : false;

//echo abs($_SERVER['REQUEST_TIME']-$time)>20;

//签名错误, 时间差错误, 不显示错误原因, 防止有人找漏洞做参考用
if($token===false || $uid===false ) exit( json_encode( array('status'=>500,'msg'=>'') ) );

if( abs($_SERVER['REQUEST_TIME']-$time)>20 ) exit( json_encode( array('status'=>500,'msg'=>'') ) );

$sign = md5($uid.PRODUCT_KEY.$time);

if($token==$sign){

	
	$sql = "select repeated,timeline from app_sign where uid=$uid order by timeline desc limit 1 ";
	$res = getrow($sql);
	
	if($res==false){
		$signed=false;
		$score = 1;
		$repeated = 0;

	}else{
		$timeline = $res['timeline'];
		$repeated = $res['repeated'];
		$today = strtotime(date('Y-m-d'));
		if($timeline <= $today){
			$signed = false;
			$score = 1;
		}else{
			exit( json_encode( array('status'=>1,'msg'=>'今天已签到') ) );
		}
	}
	
	//switch($repeated){
		//case 1:
			//$score = 90;
		//break;
		//case 2:
			//$score = 110;
		//break;
		//case 3:
			//$score = 140;
		//break;
		//default: if($repeated>3) $score = 140;
	//}


	
	
	$repeated++;
	$sql = "insert app_sign set uid=$uid, score=$score, repeated=$repeated, timeline=".$_SERVER['REQUEST_TIME'];
	$inid = insert($sql);
	if($inid){
		add_order($config_platform['sign'], $inid, $uid, $score );
		exit( json_encode( array('status'=>0,'score'=>$score,'msg'=>'签到成功, 奖励'.$score.'积分') ) );
	}else{
		exit( json_encode( array('status'=>2,'msg'=>'签到错误') ) );
	}	

}
else
{
	
}

?>