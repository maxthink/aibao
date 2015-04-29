$(function(){
	 /*鼠标键入样式*/
	function funText(id){
		$(id).focus(function(){
			$(this).next('i').addClass("i-borderC");
		}).blur(function(){
			$(this).next('i').removeClass("i-borderC");
			$(this).next('i').addClass("i-border");
		});
	}
	funText(".B-tel");
	funText(".B-pw");
	funText(".B-num");
	funText(".B-payi input");
	
	
	///*单选按扭样式*/
	//$(".B-paym i").on('tap click',function(e){
		//if($(this).attr('class')=='current')
		//{
			//$(this).addClass("current");	
			//$(this).next('input').attr("checked",'true');			
		//}
		//else
		//{
			//$(".B-paym i").removeClass("current");
			//$(".B-paym input").removeAttr("checked");
			//$(this).addClass("current");
			//$(this).next('input').attr("checked",'true');
		//}
	//});

	/*单选按扭样式*/
	$(".B-paym span").on('tap click',function(e){
		if($(this).children("i").attr('class')=='current')
		{
			$(this).children("i").addClass("current");	
			$(this).children('input').attr("checked",'true');			
		}
		else
		{
			$(".B-paym i").removeClass("current");
			$(".B-paym input").removeAttr("checked");
			$(this).children("i").addClass("current");
			$(this).children('input').attr("checked",'true');
		}
	});


});