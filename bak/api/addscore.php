<?php
ini_set("display_errors","On");
error_reporting(E_ALL);
include_once('../lib/init.php');


file_put_contents("kk.txt", print_r($_REQUEST,1) );

$token		= isset($_REQUEST['token']) ? $_REQUEST['token'] : false;
$uid		= isset($_REQUEST['uid']) ? intval($_REQUEST['uid']) : false;
$score		= isset($_REQUEST['score']) ? intval($_REQUEST['score']) : 0;
$platform	= isset($_REQUEST['platform']) ? intval($_REQUEST['platform']) : false;
$version	= isset($_REQUEST['versioncode']) ? intval($_REQUEST['versioncode']) : 0;
$time		= isset($_REQUEST['time']) ? intval($_REQUEST['time']) : 0;

if( abs($_SERVER['REQUEST_TIME']-$time)>20 ) exit( json_encode( array('status'=>500,'msg'=>'') ) );

//token = MD5Checksum.getChecksum4String(Common.uid+"as"+score+key+platform+""+time);
$key	= md5($uid.'as'.$score.PRODUCT_KEY.$platform.$time);

if($token==$key && $score>0 ){	
	add_order($platform, 0, $uid, $score );
	
	exit( json_encode(array('status'=>0,'msg'=>'恭喜你获得'.$score.'积分') ) );
}else{
	exit( json_encode(array('status'=>500,'msg'=>'') ) );
}


?>