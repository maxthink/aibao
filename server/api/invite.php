<?php

ini_set('display_errors','On');
error_reporting(E_ALL);
include_once('../lib/init.php');


$invitecode	= isset($_REQUEST['invitecode']) ? addslashes($_REQUEST['invitecode']) : false;
$imei	= isset($_REQUEST['imei']) ? addslashes($_REQUEST['imei']) : false;
$time		= $_SERVER['REQUEST_TIME'];



if( $invitecode===false || $imei===false ) exit('--');

if(!isset($_REQUEST['sure']) ){

	$sql = 'select p_imei from app_user where imei=\''.$imei.'\'';
	$p_imei = getone($sql);

	if( !empty($p_imei) ){
		
		echo json_encode(array('status'=>'1','msg'=>'222已经绑定邀请者了' ));
	
	}else{
		
		$sql = 'select nick from app_user where invitecode=\''.$invitecode.'\'';
		$nick = getone($sql);
		if($nick!==false){
			echo json_encode(array('status'=>'0','nick'=>$nick ));
		}else{
			echo json_encode(array('status'=>'1','msg'=>'111没有查到邀请码的拥有者, 请查看下邀请码是否输入正确'));
		}
	}

}else{

	$sql = 'select p_imei from app_user where imei=\''.$imei.'\'';
	$p_imei = getone($sql);
	
	if($p_imei !==false){
		
		echo json_encode(array('status'=>'1','msg'=>'11已经绑定邀请者了' ));
	
	}else{
		
		$sql = 'select imei from app_user where invitecode=\''.$invitecode.'\'';
		$invite_imei = getone($sql);
		if($invite_imei!==fale){

			$sql = 'update app_user set p_imei=\''.$invite_imei.'\' where imei=\''.$imei.'\'';
			$db->query($sql);

			echo json_encode(array('status'=>'0','msg'=>'22您的邀请者已绑定成功' ));

		}else{
			echo json_encode(array('status'=>'1','msg'=>'33没有查到邀请码的拥有者, 请查看下邀请码是否输入正确'));
		}
	}

}


?>