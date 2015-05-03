<?php
ini_set("display_errors","On");
error_reporting(E_ALL);
include_once('../lib/init.php');


$token		= isset($_REQUEST['token']) ? $_REQUEST['token'] : false;
$uid		= isset($_REQUEST['uid']) ? intval($_REQUEST['uid']) : false;
$time		= isset($_REQUEST['time']) ? $_REQUEST['time'] : false;

//echo abs($_SERVER['REQUEST_TIME']-$time)>20;

//签名错误, 时间差错误, 不显示错误原因, 防止有人找漏洞做参考用
if($token===false || $uid===false ) exit( json_encode( array('status'=>500,'msg'=>'') ) );

if( abs($_SERVER['REQUEST_TIME']-$time)>20 ) exit( json_encode( array('status'=>500,'msg'=>'') ) );

		
$ex_score = isset($_REQUEST['exchangescore']) ? intval(trim($_REQUEST['exchangescore'])) : false;
if($ex_score===false) exit( json_encode( array('status'=>400,'msg'=>'请选择提现金额') ) );

$type = isset($_REQUEST['type']) ? strtolower($_REQUEST['type']) : false;
if(!in_array($type,array('qq','alipay','bill')))  exit( json_encode( array('status'=>400,'msg'=>'请选择提现类型') ) );
		
$account = isset($_REQUEST['account']) ? addslashes($_REQUEST['account']) : false;
if($account===false) exit( json_encode( array('status'=>400,'msg'=>'请输入账号') ) );

$key = md5($uid.PRODUCT_KEY.'sc'.$time);


if($token==$key && $ex_score>0){
	$sql = 'select score from app_user where uid='.$uid;
	$score = getone($sql);
	
	if($score!=false && $ex_score<=$score){
		
		switch($type){
			case 'qq':	$type='充值QQ币'; break;
			case 'alipay': $type='支付宝提现'; break;
			case 'bill': $type='手机充值'; break;
		}

		$sql = "insert app_exchange set uid=$uid, type='$type', account='$account', score=$ex_score, status=0, ctime=".$_SERVER['REQUEST_TIME'];
		$inid = insert($sql);
		if($inid){
			
			$sql = 'update app_user set score=score-'.$ex_score.', exchange_count=exchange_count+1 where uid='.$uid;
			$db->query($sql);
			
			exit( json_encode( array('status'=>0,'msg'=>'提现请求成功，工作日24小时内到账，请查收!') ) );

		}else{
			exit( json_encode( array('status'=>500,'msg'=>'') ) );
		}

	}else{
		exit( json_encode( array('status'=>400,'msg'=>'积分不够') ) );
	}
}else{
	exit( json_encode( array('status'=>500,'msg'=>'3') ) );
}


?>