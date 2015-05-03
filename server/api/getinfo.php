<?php
ini_set("display_errors","On");
error_reporting(E_ALL);
include_once('../lib/init.php');

$token		= isset($_REQUEST['token']) ? $_REQUEST['token'] : false;
$imei		= isset($_REQUEST['imei']) ? addslashes($_REQUEST['imei']) : false;

file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );

if($token===false || $imei===false ) exit( json_encode( array('status'=>500,'msg'=>'') ) );

$key = md5($imei.'aic');

if($token==$key){
	
	$plat = getPlat();

	$user = getUserByImei($imei);

	if($user!==false){
		if(!isset($user['score'])) $user['score']=0;
		if(!isset($user['old_score'])) $user['old_score']=0;
		if(!isset($user['invitecode'])) $user['invitecode']='';
		if(!isset($user['nick'])) $user['nick']='还未设置昵称';
		
		$did = getDidCount($user['uid']);
		$today_score = getTodayScore($user['uid']);
		
		exit(json_encode( array(
			'status'=>'0',
			'uid'=>$user['uid'],
			'today'=>$today_score,
			'score'=>$user['score'],
			'total'=>$user['old_score'],
			'task_count'=>$user['task_count'],
			'exchange_count' => $user['exchange_count'],
			'invitecode'=>$user['invitecode'], 
			'nick'=>$user['nick'],
			'zhifubao'=>$user['zhifubao'],
			'mobile'=>$user['mobile'],
			'timestamp'=>$_SERVER['REQUEST_TIME'],
			'plat'=>$plat,
			'did'=>$did,
			'show_sign'=>true,
			'show_end'=>true,
		) ) );
	}
	else	//没有该用户, 就添加用户
	{
		$invitecode = getInviteCode();
		$sql = "insert app_user set imei='$imei', invitecode='$invitecode', timeline=".$_SERVER['REQUEST_TIME'];
		$inid=insert($sql);
		exit( json_encode( array(
			'status'=>0,
			'uid'=>$inid,
			'today'=>0,
			'score'=>0,
			'total'=>0,
			'task_count'=>0,
			'exchange_count' => 0,
			'invitecode'=>$invitecode, 
			'nick'=>'',
			'zhifubao'=>'',
			'mobile'=>'',
			'timestamp'=>$_SERVER['REQUEST_TIME'],
			'plat'=>$plat,
			'did'=>'',
			'show_sign'=>true,
			'show_end'=>true,
		) ) );
	}
	
	

}else{
	exit( json_encode( array('status'=>2 ) ) );
}


?>