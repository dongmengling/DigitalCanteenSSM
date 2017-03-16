<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<!-- <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> -->
<html>
    <head>
    <base href="<%=basePath%>">      
    <title>预置档口信息</title>
      
  	<meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="expires" content="0">   
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no,minimal-ui" />
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
  	
    <script src="js/modernizr.custom.js"></script>
    <link rel="stylesheet" href="css/m_bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="css/normalize.css" />
    <link rel="stylesheet" type="text/css" href="css/demo.css" />
    <link rel="stylesheet" type="text/css" href="css/icons.css" />
    <link rel="stylesheet" type="text/css" href="css/component.css" />
  	<script>
    	/* 在校区选择之后得到该校区之下的食堂选择框
    		objValue：校区的ID值
    		windowInsertForm：档口预置表单
    		canteenItemsList：所有食堂信息列表
    		wndCantID：食堂select标签的选择值
    	*/
  		function getCanteen(objValue)
		{	
			var objForm = document.forms["windowInsertForm"];
		
    		if(objValue == ""){
    			objForm.wndCantID.disabled = true;
    		}else{   			 	
    			objForm.wndCantID.disabled = false; 
    			objForm.wndCantID.options.length = 0;
    			
    			<c:forEach items="${canteenItemsList }" var="item" >  	
    	 			var cantCampusID = ${item.cantCampusID };
 	 				var optionObj = document.createElement("option");
    	 			if( cantCampusID == Number(objValue)){	 				
	                    optionObj.text = "${item.cantName }";
	                    optionObj.value = "${item.cantID }";
	                    objForm.wndCantID.add(optionObj);
    	 			}  
				  </c:forEach>
    		}
		}

        function check(form){
        	if(form.cantCampusID.value == ""){
        		alert("请先选择档口所在校区！");
        		return false;
        	}
        	if(form.wndCantID.value == ""){
        		alert("请先选择档口所在食堂！");
        		return false;
        	}
        	if(form.wndName.value == ""){
        		alert("请填写档口名称！");
        		form.wndName.focus();
        		return false;
        	}
        }
        /*判断浏览器类型*/
      function userBrowser(){  
        var browserName=navigator.userAgent.toLowerCase();  
        if(/msie/i.test(browserName) && !/opera/.test(browserName)){  
            alert("IE");  
            return ;  
        }else if(/firefox/i.test(browserName)){  
            alert("Firefox");  
            return ;  
        }else if(/chrome/i.test(browserName) && /webkit/i.test(browserName) && /mozilla/i.test(browserName)){  
            alert("Chrome");  
            return ;  
        }else if(/opera/i.test(browserName)){  
            alert("Opera");  
            return ;  
        }else if(/webkit/i.test(browserName) &&!(/chrome/i.test(browserName) && /webkit/i.test(browserName) && /mozilla/i.test(browserName))){  
            alert("Safari");  
            return ;  
        }else{  
            alert("unKnow");  
        }  
    }  
      /*判断手机电脑浏览器*/
      function check() 
      { 
        var userAgentInfo=navigator.userAgent; 
        var Agents =new Array("Android","iPhone","SymbianOS","Windows Phone","iPad","iPod"); 
        var flag=true; 
        for(var v=0;v<Agents.length;v++) 
          { 
            if(userAgentInfo.indexOf(Agents[v])>0)
             { 
                flag=false; 
                break; 
              }
          } 
        alert(flag); 
      }
  	</script>
    <!--<script>/*调用check（）函数*/check()</script>  -->
    </head>   
    <body>
    <div id="st-container" class="st-container">    
    <div class="st-pusher">
    <%@ include file="publicjsp/index.jsp" %>               
        <div class="st-content">
        <div class="st-content-inner">
        <div class="main clearfix"> 
        <div id="st-trigger-effects">
          <button data-effect="st-effect-3">
            <div class="burger-container" z-index="-1">
              <span class="burger-bun-top"></span>
              <span class="burger-filling"></span>
              <span class="burger-bun-bot"></span>
            </div>
          </button>
        </div> 
        <div>  
          <h3>预置档口</h3>  
        </div>

    <form name = "windowInsertForm" action="insertWindow.action" method="post" >
    	<!-- 选择食堂所属校区 -->
      <div align="center">
      <select name="cantCampusID" onchange="getCanteen(this.value)" data-toggle="select" class="form-control select select-primary mrs mbm" >
        <option value="">请选择食堂所属校区</option>
        <c:forEach items="${pagehelper.list }" var="item" >
          <option value="${item.campusID }">${item.campusName }</option>
        </c:forEach>
      </select>
      </div>

   		<!-- 选择档口所属食堂 -->
      <div align="center">
   		<select name="wndCantID" disabled="disabled" data-toggle="select" class="form-control select select-primary mrs mbm">
   			<option value="">请选择档口所属食堂</option>
   		</select>
      </div>

   		<!-- 输入档口名称 -->
      <div class="form-group" >
    	  <div class="row">
          <div class="col-xs-6">
            <input type="text" placeholder="请输入预置档口名称"   name="wndName" class="form-control input-sm">           
          </div>
          <div class="col-xs-6">
            <input type="text" placeholder="请输入档口位置"       name="wndAddr" class="form-control input-sm">
          </div>
        </div>

        <div class="row">
          <div class="col-xs-6">
           <input type="text" placeholder="请输入档口价格范围"   name="wndPriceRange" class="form-control input-sm">
          </div>
          <div class="col-xs-6">
           <input type="text" placeholder="请输入档口营业时间段" name="wndSaleHours" class="form-control input-sm">
          </div>
        </div>

        <div class="row">
          <div class="col-xs-6">
            <input type="text" placeholder="请输入档口支付方式"   name="wndPayment" class="form-control input-sm">
          </div>
          <div class="col-xs-6">
            <input type="text" placeholder="请输入档口描述"       name="wndDescription" class="form-control input-sm">
          </div>
        </div>

        <input type="text" placeholder="请输入档口备注"       name="wndNote" class="form-control input-sm">
        <br>   
    	<!-- 档口添加按钮 -->
    	  <div align="center"><input type="submit" value="添加档口" onClick="return check(windowInsertForm)" class="btn btn-primary btn-wide" data-role="none"></div>
      </div> 

    <!-- 列举已录入的档口 -->
	    <table  class="table table-striped table-bordered table-condensed">
        <thead>
		    <tr style="background:#7acfa6;text-align:center;color:white;">
		   	 	  <td>校区名称</td>
	        	<td>食堂名称</td>
	        	<td>档口名称</td>
		        <td colspan="2">编辑</td>
		    </tr>
        </thead>

			<c:forEach items="${windowItemsList }" var="item" >
				<tr align="center" >
				 	  <td >${item.campusName }</td>
				   	<td>${item.cantName }</td>
				   	<td>${item.wndName }</td>
				   	<!-- wndID=${item.wndID}& -->
				    <td><a href="modifyWindow.action?wndID=${item.wndID}&cantCampusID=${item.cantCampusID}">修改</a></td>
				    <td><a href="deleteWindowById.action?wndID=${item.wndID}">删除</a></td>
				</tr>
			</c:forEach>
		</table>
	</form>
	</div>
  </div>
  </div>
  </div>
  </div>
  <script src="js/classie.js"></script>
  <script src="js/sidebarEffects.js"></script>
    </body>
</html>