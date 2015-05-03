<?php
/*
 * 关系型数据库操作类
 */ 

class DB_Sql
{
  var $Host     = "";
  var $Database = "";
  var $User     = "";
  var $Password = "";

  var $charset = "utf8";
  
  /* private: link and query handles */
  var $Link_ID  = 0;
  var $Query_ID=0;
  
  /* public: current error number and error text */
  var $Errno    = 0;
  var $Error    = "";
  
  var $Debug         = 0;     ## Set to 1 for debugging messages.
  
  /* public: configuration parameters */
  var $Auto_Free     = 0;     ## Set to 1 for automatic mysql_free_result()
  
  var $Halt_On_Error = "yes"; ## "yes" (halt with message), "no" (ignore errors quietly), "report" (ignore errror, but spit a warning)
  var $Seq_Table     = "db_sequence";

  /* public: result array and current row number */
  var $Record   = array();
  var $Row;



  /* public: this is an api revision, not a CVS revision. */
  var $type     = "mysql";
  var $revision = "1.2";
  
  
  function link_id() {
    return $this->Link_ID;
  }

  function query_id() {
    return $this->Query_ID;
  }
  
  /* public: connection management */
  function connect($Database, $Host, $User , $Password ) 
  {
    /* Handle defaults */
      $this->Database=$Database;
      $this->Host=$Host;
      $this->User=$User;
      $this->Password=$Password;
      
	    /* establish connection, select database */
	    if ( $this->Link_ID==0 ) 
	    {
	      	$this->Link_ID=mysql_connect($Host, $User, $Password);
	      	if (!$this->Link_ID) 
	      	{
	        	$this->halt("connect($Host, $User, \$Password) failed.");
	        	return 0;
	     	}

			if($this->version() > '4.1') {
				if($this->charset) {
					@mysql_query("SET character_set_connection=$this->charset, character_set_results=$this->charset, character_set_client=binary", $this->Link_ID);
				}
				if($this->version() > '5.0.1') {
					@mysql_query("SET sql_mode=''", $this->Link_ID);
				}
			}

	      	if (!mysql_select_db($Database,$this->Link_ID)) 
	      	{
	        	$this->halt("cannot use database ".$this->Database);
	        	return 0;
	      	}
	    }

    	return $this->Link_ID;
  }
  
	function select_db($database)
	{
		$connectcheck = mysql_select_db($database, $this->Link_ID);

		if($connectcheck) {
			return true;
		} else {
			$this->halt('Cannot use database ' . $database);
			return false;
		}
	}
  
  function setDebug($debug)
  {
  	$this->Debug=$debug;
  }
  
  function getalldata()
  {
  		$result=array();
		for($i=0;$i<$this->num_rows();$i++)
		{
			//$result[$i]=mysql_fetch_array($this->query_id());		//数字索引、字段名作键名
			$result[$i]=mysql_fetch_assoc($this->query_id());		//字段名作键名（only）
		}
		return $result;
  }

  function getone(){
	$re = mysql_fetch_array($this->query_id());	//返回根据从结果集取得的行生成的数组，如果没有更多行则返回 false。
	return $re[0];
	
  }

  function getrow(){
	return mysql_fetch_array($this->query_id());
  }

  function query($Query_String) {
    /* No empty queries, please, since PHP4 chokes on them. */
    if ($Query_String == "")
      return 0;

    # New query, discard previous result.
    if ($this->Query_ID) 
    {
      $this->free();
    }

    if ($this->Debug)
      printf("Debug: query = %s<br>\n", $Query_String);

    $this->Query_ID = mysql_query($Query_String,$this->Link_ID);
    $this->Row   = 0;
    $this->Errno = mysql_errno();
    $this->Error = mysql_error();
    if (!$this->Query_ID) 
    {
      $this->halt("Invalid SQL: ".$Query_String."<br>");
    }

    # Will return nada if it fails. That's fine.
    return $this->Query_ID;
  }

  /* private: error handling */
  function halt($msg) 
  {
    	$this->Error = mysql_error($this->Link_ID);
   	 	$this->Errno = mysql_errno($this->Link_ID);
    	if ($this->Halt_On_Error == "no")
      	return;

    	$this->haltmsg($msg);

    	if ($this->Halt_On_Error != "report")
    	{
      		die("Session halted.");
    	}
  }

  function haltmsg($msg) 
  {
    printf("</td></tr></table><b>Database error:</b> %s<br>\n", $msg);
    printf("<b>MySQL Error</b>: %s (%s)<br>\n",
      $this->Errno,
      $this->Error);
  }
    
  function free() 
  {
      //mysql_freeresult($this->Query_ID);
      $this->Query_ID = 0;
  }
  
  /* public: walk result set */
  function next_record() {
    if (!$this->Query_ID) {
      $this->halt("next_record called with no query pending.");
      return 0;
    }

    $this->Record = @mysql_fetch_array($this->Query_ID);
    $this->Row   += 1;
    $this->Errno  = mysql_errno();
    $this->Error  = mysql_error();

    $stat = is_array($this->Record);
    if (!$stat && $this->Auto_Free) 
    {
      $this->free();
    }
    return $stat;
  }
  
  function seek($pos) 
  {
    $status = mysql_data_seek($this->Query_ID, $pos);
    if ($status)
    {
      $this->Row = $pos;
    }
    else 
    {
      $this->halt("seek($pos) failed: result has ".$this->num_rows()." rows");

      /* half assed attempt to save the day, 
       * but do not consider this documented or even
       * desireable behaviour.
       */
      mysql_data_seek($this->Query_ID, $this->num_rows());
      $this->Row = $this->num_rows;
      return 0;
    }

    $this->Record = @mysql_fetch_array($this->Query_ID);
    $this->Errno  = mysql_errno();
    $this->Error  = mysql_error();

    return 1;
  }
  
  function lock($table, $mode="write") {
    $this->connect();
    
    $query="lock tables ";
    if (is_array($table)) {
      while (list($key,$value)=each($table)) {
        if ($key=="read" && $key!=0) {
          $query.="$value read, ";
        } else {
          $query.="$value $mode, ";
        }
      }
      $query=substr($query,0,-2);
    } else {
      $query.="$table $mode";
    }
    $res = mysql_query($query, $this->Link_ID);
    if (!$res) {
      $this->halt("lock($table, $mode) failed.");
      return 0;
    }
    return $res;
  }
  
  function unlock() {
    $this->connect();

    $res = mysql_query("unlock tables");
    if (!$res) {
      $this->halt("unlock() failed.");
      return 0;
    }
    return $res;
  }
  
  
  /* public: evaluate the result (size, width) */
  function affected_rows() {
    return @mysql_affected_rows($this->Link_ID);
  }

  function num_rows() {
    return @mysql_num_rows($this->Query_ID);
  }

  function num_fields() {
    return @mysql_num_fields($this->Query_ID);
  }

  /* public: shorthand notation */
  function nf() {
    return $this->num_rows();
  }

  function np() {
    print $this->num_rows();
  }

  function f($Name) {
    return $this->Record[$Name];
  }

  function p($Name) {
    print $this->Record[$Name];
  }

  function insert_id(){
	  return @mysql_insert_id();
  }
  
  
  /* public: return table metadata */
  function metadata($table='',$full=false) {
    $count = 0;
    $id    = 0;
    $res   = array();

    /*
     * Due to compatibility problems with Table we changed the behavior
     * of metadata();
     * depending on $full, metadata returns the following values:
     *
     * - full is false (default):
     * $result[]:
     *   [0]["table"]  table name
     *   [0]["name"]   field name
     *   [0]["type"]   field type
     *   [0]["len"]    field length
     *   [0]["flags"]  field flags
     *
     * - full is true
     * $result[]:
     *   ["num_fields"] number of metadata records
     *   [0]["table"]  table name
     *   [0]["name"]   field name
     *   [0]["type"]   field type
     *   [0]["len"]    field length
     *   [0]["flags"]  field flags
     *   ["meta"][field name]  index of field named "field name"
     *   The last one is used, if you have a field name, but no index.
     *   Test:  if (isset($result['meta']['myfield'])) { ...
     */

    // if no $table specified, assume that we are working with a query
    // result
    if ($table) {
      $this->connect();
      $id = @mysql_list_fields($this->Database, $table);
      if (!$id)
        $this->halt("Metadata query failed.");
    } else {
      $id = $this->Query_ID; 
      if (!$id)
        $this->halt("No query specified.");
    }
 
    $count = @mysql_num_fields($id);

    // made this IF due to performance (one if is faster than $count if's)
    if (!$full) {
      for ($i=0; $i<$count; $i++) {
        $res[$i]["table"] = @mysql_field_table ($id, $i);
        $res[$i]["name"]  = @mysql_field_name  ($id, $i);
        $res[$i]["type"]  = @mysql_field_type  ($id, $i);
        $res[$i]["len"]   = @mysql_field_len   ($id, $i);
        $res[$i]["flags"] = @mysql_field_flags ($id, $i);
      }
    } else { // full
      $res["num_fields"]= $count;
    
      for ($i=0; $i<$count; $i++) {
        $res[$i]["table"] = @mysql_field_table ($id, $i);
        $res[$i]["name"]  = @mysql_field_name  ($id, $i);
        $res[$i]["type"]  = @mysql_field_type  ($id, $i);
        $res[$i]["len"]   = @mysql_field_len   ($id, $i);
        $res[$i]["flags"] = @mysql_field_flags ($id, $i);
        $res["meta"][$res[$i]["name"]] = $i;
      }
    }
    
    // free the result only if we were called on a table
    if ($table) @mysql_free_result($id);
    return $res;
  }

  function table_names() {
    $this->query("SHOW TABLES");
    $i=0;
    while ($info=mysql_fetch_row($this->Query_ID))
     {
      $return[$i]["table_name"]= $info[0];
      $return[$i]["tablespace_name"]=$this->Database;
      $return[$i]["database"]=$this->Database;
      $i++;
     }
   return $return;
  }
  
	function close()
	{
		mysql_close($this->link_id());
	}

	function field_name($columnnum)
	{
		return @mysql_field_name($this->Query_ID, $columnnum);
	}

	function version() {
		return mysql_get_server_info($this->Link_ID);
	}

}

class DBOpera extends DB_Sql
{
	/**
	 * 构造时自动连接数据库
	 */
	function __construct()
	{
		$this->connect(DB_DBNAME,DB_SERVER,DB_USERNAME,DB_PASSWORD);
	}

	function DBOpera()
	{
		$this->connect(DB_DBNAME,DB_SERVER,DB_USERNAME,DB_PASSWORD);
	}

	/**
	 * 析构时自释放资源
	 *
	 */
	function __destruct()
	{
	}
}

?>