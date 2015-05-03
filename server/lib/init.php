<?php

define("BASEDIR", dirname(__FILE__));
include(BASEDIR . '/DBOpera.inc.php');

//本地数据库连接配置
define("DB_SERVER", "localhost");
define("DB_USERNAME", "aizhuanbao");
define("DB_PASSWORD", "aic2015!@#");
define("DB_DBNAME", "aizhuanbao");

$db = new DBOpera();
define("PRODUCT_KEY","442a7704bd4c7fc424e844dc85bd141d");	//数据通信秘钥

define('RATE',100);	//   积分/金钱 比率

/*
渠道id分配
0: 每日签到
1: o2o  欧拓
2: youmi有米
4: 赢告
8: dianle
16: coco

*/
$platform_sign		= 0;	//每日签到收入 
$platform_o2o		= 1;	//欧拓		(server)
$platform_youmi		= 2;	//有米		(server)
$platform_yinggao	= 3;	//赢告无限	(server)
$platform_duomeng	= 4;	//多盟
$platform_qumi		= 5;	//趣米		客户端发送积分
$platform_wanpu		= 6;	//万普		客户端发送积分
$platform_dianle	= 7;	//点乐		(server)
$platform_guomeng	= 8;	//果盟

$config_platform = array(
	'sign'		=> 0,
	'o2o'		=> 1,
	'youmi'		=> 2,
	'yinggao'	=> 3,
	'duomeng'	=> 4,
	'qumi'		=> 5,
	'wanpu'		=> 6,
	'dianle'	=> 7,
	'guomeng'	=> 8,

);


/*
数据库记录表

*/
$record_o2o = 'app_ad_record_o2o';
$record_youmi = 'app_ad_record_youmi';
$record_yinggao = 'app_ad_record_yinggao';
$record_dianle = 'app_ad_record_dianle';
$record_adcocoa = 'app_ad_record_adcocoa';
$record_yijifen	= 'app_ad_record_yijifen';

/*
服务器端回调 秘钥
*/

$key_youmi = 'eec4b2b062a5b8ea';	//memory 秘钥

$key_yinggao = 'sYbPQx9xyxBJw4t3GlUBMVj63IunJojP';	//memory 秘钥
$key_dianle = '487739645cf12002ea61046074842ccc';	// md5(aic);


//数据库插入数据方法
function insert($sql=''){
	if($sql!=''){
		global $db;
		$db->query($sql);
		$inid = $db->insert_id();
		if($inid){
			return $inid;
		}else{
			return false;
		}
	}else{
		return false;
	}
	
}

//数据库获取一个值
function getone($sql=''){
	if($sql!=''){
		global $db;
		$db->query($sql);
		return $db->getone();
	}else{
		return false;
	}
}

function getrow($sql=''){
	if($sql!=''){
		global $db;
		$db->query($sql);
		return $db->getrow();
	}else{
		return false;
	}
}


function getPlat(){
	global $db;
	$sql = 'select id,name,color,plat_id,allow_num from app_plat where status=0 order by ord asc';
	$db->query($sql);
	return $db->getalldata();

}

//任务已完成次数
function getDidCount($uid){
	global $db;
	$today = strtotime(date('Y-m-d'));
	$sql = 'SELECT platform,count(*) tc FROM `app_income_record` where uid='.$uid.' and timeline>'.$today.' GROUP BY platform';
	$db->query($sql);
	return $db->getalldata();
}


/*	通过imei号获取用户信息
*	使用地方: 前端接口获取分数
*	$imei 手机imei号
*	return array boolean  如果没有数据, 则返回false
*/
function getUserByImei($imei=''){
	
	if($imei!==''){
		global $db;
		$sql = "select * from app_user where imei='$imei' ";
		$db->query($sql);
		return $db->getrow();
	}else{
		return false;
	}

}


/* 添加收益记录(z_income_record) ,项目此处只会增加收益
*	
*	$platform	int	渠道id
*	$platform_record_id int  渠道记录表id
*	$uid		int		用户编号
*	$income		int		收益
*	
*	return boolean
*/
function add_order($platform, $platform_record_id, $uid, $income, $descript='' ){
	global $db;
	if($descript==''){
		$descript = get_desc($platform);
	}
	$sql = "insert into app_income_record set uid=$uid, platform=$platform, platform_record_id=$platform_record_id, income=$income, descript='$descript', timeline=".$_SERVER['REQUEST_TIME'];
	//echo $sql;
	$db->query($sql);
	$inid = $db->insert_id();
	
	//能获取到手机号, 应该是系统的用户, 不再去查找用户信息是否存在
	if($inid){
		$sql = "update app_user set score=score+$income, old_score=old_score+$income, task_count=task_count+1 where uid=$uid ";
		$db->query($sql);
	}
}

function get_desc($platform){

	global $config_platform;

	switch($platform){
		case $config_platform['sign']:
			return '签到';
		break;
		case $config_platform['duomeng']:
			return '多盟积分';
		break;
		case $config_platform['qumi']:
			return '趣米积分';
		break;
	}
}


/*	通过用户编号获取当天积分
*	使用地方: 前端接口获取分数
*	$uid 手机imei号
*	return num 当天赚的积分
*/
function getTodayScore($uid){
	if($uid!==''){
		global $db;
		$today = strtotime(date('Y-m-d',$_SERVER['REQUEST_TIME']));
		$sql = 'select sum(income) from app_income_record where uid='.$uid.' and timeline>'.$today;
		
		$db->query($sql);
		$today = $db->getone();
		if($today){
			return $today;
		}else{
			return 0;	
		}
		
	}else{
		return 0;
	}
}


/**
* 生成随机数字
*
*/
function generate_rand($l)
{ 
	$c= "abcdefghijklmnopqrstuvwxyz0123456789"; 
	srand((double)microtime()*1000000); 
	for($i=0; $i<$l; $i++) 
	{ 
		$rand.= $c[rand()%strlen($c)]; 
	} 
	return strtolower($rand); 
}

/*	
*	获取邀请码
*	
*
*/
function getInviteCode(){
	global $db;
	
	$go = false;
	do{
		$code = generate_rand(8);

		$sql = 'select invitecode from app_user where invitecode=\''.$code.'\'';
		$db->query($sql);
		$ok = $db->getone();
		echo 'ok: '.$ok;
		if($ok){
			$go = true;
		}else{
			return strtoupper($code);
		}
	}while($go);

}
 


//功能性方法
//$memcache = new Memcache();
//$memcache->connect('localhost', 11211);



?>
