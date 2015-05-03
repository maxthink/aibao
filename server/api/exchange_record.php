<?php
ini_set("display_errors","On");
error_reporting(E_ALL);

include_once('../lib/init.php');


$token		= isset($_REQUEST['token']) ? $_REQUEST['token'] : false;
$uid		= isset($_REQUEST['uid']) ? intval($_REQUEST['uid']) : false;
$callback		= isset($_REQUEST['callback']) ? $_REQUEST['callback'] : 'jsonp1';


file_put_contents('kk.txt',print_r($_REQUEST,1), FILE_APPEND );


//if($token===false || $uid===false ) exit('--');
if(  $uid==false ) exit( $callback.'('.json_encode( array('status'=>1) ).')' );

$page_no = isset($_REQUEST['page_no']) ? intval($_REQUEST['page_no']) : 0;
$key = md5($uid.PRODUCT_KEY.$page_no);

//if($token==$key){

	$page_size = 9;
	$offect = $page_no*$page_size;
	
	$sql = "select SQL_CALC_FOUND_ROWS type,score,ctime from app_exchange where uid=$uid order by ctime desc limit $offect,$page_size ";

	$db->query($sql);
	$res = $db->getalldata();

	if(!empty($res)){
		foreach($res as $key=>$val){
			$res[$key]['ctime']= date('m-d H:i',$val['ctime']);
			$res[$key]['score'] = $res[$key]['score']/RATE;
		}
	}
	file_put_contents('kk.txt',print_r($res,1), FILE_APPEND );
	chmod('kk.txt',0777);
	$sql = 'SELECT FOUND_ROWS() count';
	$db->query($sql);
	$total = $db->getone();

	$page_count = ceil($total/$page_size);
	$pages = array('page_no'=>$page_no,'page_count'=>$page_count);

	if($page_no>=0){
		//exit(  json_encode( array('status'=>'0','page'=>$pages,'list'=>$res) )  );
		exit( $_REQUEST['callback'].'('.json_encode( array('status'=>'0','page'=>$pages,'list'=>$res)).')' );
	}
	else
	{
		exit( json_encode( array('status'=>'-1') ));
	}



?>