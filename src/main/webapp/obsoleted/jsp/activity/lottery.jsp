<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}" rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<%-- 活动结束
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.rotate.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.easing.min.js"></script> 
--%>
<link href="${contextPath}/jsp/aone/css/activity-cover-bootstrap.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />

<title>幸运抽奖大转盘</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help lottery">
		<div class="ui-wrap"></div>
	</div>
	<div class="ui-wrap" style="padding-top:20px;">
		<div class="active-whole">
			<div class="active-left large">
				<div class="active" id="active1">
					<div class="ly-plate">
						<div class="rotate-bg"></div>
						<div class="lottery-star"><img src="${contextPath}/jsp/aone/images/lottery_arrow.png" id="lotteryBtn" /></div>
					</div>
				</div>
			</div>
			<div class="active-right small">
				<div class="lucky lottery">
					<h3>中奖公示</h3>
					<%-- 活动结束
					<c:choose>
					<c:when test="${fn:length(drawList)>0}">
						<div id="sd1" class="scrollDiv">
						<ul>
						<c:forEach var="item" items="${drawList }">  
							<li><span class="mail">${item.user }</span><span class="present">${item.giftName }</span></li>
						</c:forEach>
						</ul>
						</div>
					</c:when>
					<c:otherwise>
						<p class="noWin">暂无人中奖.</p>
					</c:otherwise>
					</c:choose> --%>
					<p class="over">抽奖活动已结束。</p>
					<p class="over"><a class="btn btn-primary" href="${contextPath }/activity/lottery/winners" target="_blank">查看中奖名单</a></p>
				</div>
				<div class="mine">
					<c:if test="${authenticated == true}">
						<p>
							<a class="btn btn-warning" href="${contextPath }/activity/lottery?func=delivery" target="_blank">提交收货地址</a>
							<a class="btn btn-success" href="${contextPath }/activity/lottery?func=myPrize" target="_blank">查看我的中奖</a>
						</p>
					</c:if>
					<p class="suggest"><a href="http://iask.cstnet.cn/?/question/393" target="_blank">活动建议与反馈</a></p>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="bonus-detail">
			<h2>抽奖活动规则</h2>
			<ol>
				<li>活动时间为2014年10月24日-11月6日（14天）；</li>
				<li>每天登录科研在线团队文档库，即获得抽奖机会1次；每天最多一次抽奖机会；</li>
				<li>用户不得通过任何人工或技术的作弊方式参与活动，一旦发现，即有权取消作弊用户参与活动的资格和中奖资格，有权追回已经发放的奖品；</li>
				<li>科研在线团队文档库拥有活动的最终解释权。</li>
			</ol>
			<h2>奖品发放</h2>
			<ol>
				<li>如您抽到奖品，活动页面会弹出中奖提醒。奖品分为空间容量和实物两大类。抽中个人空间同步版容量，系统将自动添加到您个人空间同步版中；抽中团队空间容量，需要用户手动分配空间；抽中实物奖品，您需如实填写领奖信息，我们将通过快递发送奖品；</li>
				<li>实物奖品发放起止时间：11月7日-12月31日；</li>
				<li>用户抽中实物奖品后，需在11月7日前提交个人信息，以便核实中奖信息和奖品发放，超过11月7日，视为放弃奖品；</li>
				<li>请保证您提供的领奖信息清晰准确，如果因领奖信息有误、不完整或不清晰而导致您未能及时收到奖品的，科研在线团队文档库将不承担补发奖品等责任；</li>
				<li>本活动快递奖品的收货地址仅限中国大陆地区； </li>
				<li>活动奖品以收到的实物为准。</li>
			</ol>
		</div>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>

<c:choose>
<c:when test="${authenticated == true}">

<div id="winModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="winModalLabel" aria-hidden="true">
	<div class="modal-header"style="text-align:center;">
	  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	  <h3 id="winModalLabel"> </h3>
	</div>
	<div class="modal-body winContent-js" style="text-align:center;"></div>
	<div class="modal-footer">
	  <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
	</div>
</div>

<script type="text/javascript">
$(function(){

$("#lotteryBtn").hover(function(){ $(this).attr("src","${contextPath}/jsp/aone/images/lottery_arrow_hover.png");},
		function(){ $(this).attr("src","${contextPath}/jsp/aone/images/lottery_arrow.png")}
);

<%-- 活动结束
var isStart = false;
$("#lotteryBtn").rotate({ 
   bind: 
	 { 
		click: function(){
			var _this = $(this);
			_this.css("cursor","default");
			if(isStart){
				return false;
			}
			isStart = true;
			$.ajax({
				   type: "GET",
				   dataType:"JSON",
				   url: "${contextPath}/activity/lottery?func=draw",
				   cache:false,
				   success: function(resp){
					   if(resp.status!=1){
						   alert(getDrawResponse(resp.status));
						   isStart = false;
							_this.css("cursor","pointer");
					       return;
					   }
					   var angle = getAngle(resp.result.giftLevel);
					   _this.rotate({
						 	duration:3000,
						 	angle: 0, 
	         				animateTo:1440+angle,
							easing: $.easing.easeOutSine,
							callback: function(){
								isStart = false;
								_this.css("cursor","pointer");
								var modal = $("#winModal");
								if(resp.result.giftLevel>0){
									$("#winModalLabel").text("恭喜您中奖了");
									if(resp.result.giftLevel==6){
										modal.find(".winContent-js").html('<p class="present">您获得<strong>“' + resp.result.giftName +
										'”</strong></p><p><a href="${contextPath}/system/space" target="_blank" class="btn btn-success">去分配</a></p>' );
									}else if(resp.result.giftLevel==7){
										modal.find(".winContent-js").html('<p class="present">您获得<strong>“' + resp.result.giftName +
										'”</strong></p><p><a href="${contextPath}/pan/applicationSpace" target="_blank" class="btn btn-success">查看</a></p>' );
									}else{
										modal.find(".winContent-js").html('<p class="present">您获得<strong>“' + resp.result.giftName +
										'”</strong></p><p><a href="${contextPath}/activity/lottery?func=delivery" target="_blank" class="btn btn-success">提交收货地址</a></p>' );
									}
									
								}else{
									$("#winModalLabel").text("您未中奖");
									modal.find(".winContent-js").html('<p>谢谢参与，明天再来呦～</p>' );
								}
								modal.modal("show");
							}
					 });
				   }
				});
		}
	 } 
		   
});

function getAngle(giftLevel){
	var angle1 = [ 0, 44 ];
	var angle2 = [ 46, 89 ];
	var angle3 = [ 91, 134 ];
	var angle4 = [ 136, 179 ];
	var angle5 = [ 181, 224 ];
	var angle6 = [ 316, 359 ];
	var angle7 = [ 271, 314 ];
	var angleOther = new Array([228,267]);
	var result=0;
	if(giftLevel==1){
		result = randomnum(angle1[0], angle1[1]);
	}else if(giftLevel==2){
		result = randomnum(angle2[0], angle2[1]);
	}else if(giftLevel==3){
		result = randomnum(angle3[0], angle3[1]);
	}else if(giftLevel==4){
		result = randomnum(angle4[0], angle4[1]);
	}else if(giftLevel==5){
		result = randomnum(angle5[0], angle5[1]);
	}else if(giftLevel==6){
		result = randomnum(angle6[0], angle6[1]);
	}else if(giftLevel==7){
		result = randomnum(angle7[0], angle7[1]);
	}else{
		var r = randomnum(0,angleOther.length-1);
		result = randomnum(angleOther[r][0], angleOther[r][1]);
	}
	return result;
}

//获取2个值之间的随机数
function randomnum(smin, smax) {
	var Range = smax - smin;
	var Rand = Math.random();
	return (smin + Math.round(Rand * Range));
}

function getDrawResponse(index){
	var msgArr = new Array("","今天的运气用光啦，明天再来呦～",
		"您的IP已经超过最大抽奖次数，明天再来呦～",
		"抽奖服务维护中，请稍后再试。",
		"抽奖活动还没开始，活动将于2014年10月24日零点开始。",
		"抽奖活动已结束，敬请关注团队文档库以后的活动."
	 );
	return msgArr[index-1];
}
--%>
$("#lotteryBtn").click(function(){
	alert("抽奖活动已结束，敬请关注团队文档库以后的活动.");
});


});

</script>

</c:when>
   
<c:otherwise>
<script type="text/javascript">
$(function(){
	$.getScript('${passportUrl}/js/isLogin.do', function(){
 		if(data.result){
			window.location.href="${loginUrl}";
		 }
	 });
	$("#lotteryBtn").click(function(){
		alert("抽奖活动已结束，敬请关注团队文档库以后的活动.");
	});
});

</script>
<%-- 活动结束
<div id="loginModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel" aria-hidden="true">
	<div class="modal-header">
	  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	  <h3 id="loginModalLabel">登录团队文档库 <span style="font-size:16px;color:#999;">(登录后即可抽奖)</span></h3>
	</div>
	<div class="modal-body">
		<iframe id="frameOauth" frameborder="no" border="0" style="width:530px;height:200px;border:0;" src="${getEmbedLoginUrl }" ></iframe>
	</div>
</div>
--%>
</c:otherwise>
</c:choose>
<%-- 活动结束
<c:if test="${fn:length(drawList)>11}">
	<style  type="text/css">
	.scrollDiv{height:20px;line-height:20px;overflow:hidden;}
	.scrollDiv li{height:20px;}
	#sd1{height:348px;}
	</style>
	<script type="text/javascript">
	$(function(){
		$("#sd1").Scroll({line:5,speed:500,timer:4000});
	});
	</script>
	<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.scroll.js"></script>
</c:if>
--%>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>