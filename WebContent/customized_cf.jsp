<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="contributing_factor.CF_unstructured"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:setProperty name="user" property="*" />



<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String userName = user.getUserName();
String userPWD = user.getUserPWD();

String sql = "select * from users where user_id='" + userName + "' and passwords='" + userPWD + "'";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Voluntary Patient Safety Reporting Network</title>
<meta charset="utf-8">
<link href="http://fonts.googleapis.com/css?family=Open+Sans:300,600,700" rel="stylesheet">
<script src="js/jquery-1.8.3.min.js"></script>
<script src="css/5grid/init.js?use=mobile,desktop,1000px"></script>
<script src="js/init.js"></script>
<noscript>
<link rel="stylesheet" href="css/5grid/core.css">
<link rel="stylesheet" href="css/5grid/core-desktop.css">
<link rel="stylesheet" href="css/5grid/core-1200px.css">
<link rel="stylesheet" href="css/5grid/core-noscript.css">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/style-desktop.css">
</noscript>
<!--[if lte IE 9]>
<link rel="stylesheet" href="css/ie9.css">
<![endif]-->
<!--[if lte IE 8]>
<link rel="stylesheet" href="css/ie8.css">
<![endif]-->
<!--[if lte IE 7]>
<link rel="stylesheet" href="css/ie7.css">
<![endif]-->
</head>
<body>

<nav id="nav">
  <ul>
    <li><a href="index.html">Home</a></li>
    <li><a href="index.html#search">Search</a></li>
    <li><a href="login.jsp">Report</a></li>
    <li><a href="index.html#contact">Contact</a></li>
  </ul>
</nav>

<% 

ResultSet rs = myDBBean.query(sql);
if(rs.next()) {
	sql = "select * from contributing_factors_list";
	ResultSet rs_cf = myDBBean.query(sql);
%>

<script>
function show_hiddendiv(o){
	document.getElementById(o + "_son").style.display="block";
	document.getElementById(o).href="javascript:hidden_showdiv('" + o + "');";
	document.getElementById("span_" + o).innerHTML='<img src="images/minus.png" title="Click to fold" height=18 style="vertical-align:middle;display:inline-block;">';
}

function hidden_showdiv(o){
	document.getElementById(o + "_son").style.display="none";
	document.getElementById(o).href="javascript:show_hiddendiv('" + o + "');";
	document.getElementById("span_" + o).innerHTML='<img src="images/plus.png" title="Click to unfold" height=18 style="vertical-align:middle;display:inline-block;">';
}	

function addFactor(o){
	document.getElementById("myCF_" + o).style.display="block";
	var cf = document.getElementById("cfs").value;
	if(cf=="") {
		document.getElementById("cfs").value = o;
	}
	else {
		var s = cf.split(",");
		for(var i=0; i<s.length; i++) {
			if(s[i] == o) {
				s.splice(i,1);
			}
		}
		cf = s.join() + "," + o;
		document.getElementById("cfs").value = cf;
	}
	if(document.getElementById("cfs").value == "") {
		document.getElementById("search").style.display="none";
	}
	else {
		document.getElementById("search").style.display="block";
	}
	//confirm(document.getElementById("cfs").value);
}

function removeFactor(o){
	document.getElementById("myCF_" + o).style.display="none";
	var cf = document.getElementById("cfs").value;
	var s = cf.split(",");
	for(var i=0; i<s.length; i++) {
		if(s[i] == o) {
			s.splice(i,1);
		}
	}
	document.getElementById("cfs").value = s.join();
	if(document.getElementById("cfs").value == "") {
		document.getElementById("search").style.display="none";
	}
	else {
		document.getElementById("search").style.display="block";
	}
	//confirm(document.getElementById("cfs").value);
}

function similarity(o)
{	
	if(document.getElementById("cfs").value == "") {
		confirm("You need to select at least one contributing factor.");
	}
	else {
		document.getElementById("simi").style.display="none";		
		document.getElementById("loading").style.display="block"; 
		o.form.submit();
	}
}
</script>

<style>
.flipx{
    -moz-transform:scaleX(-1);
    -webkit-transform:scaleX(-1);
    -o-transform:scaleX(-1);
    transform:scaleX(-1);
    /*IE*/
    filter:FlipH;
}
</style>

<div class="wrapper wrapper-style1 wrapper-first">
  <article class="5grid-layout" id="top">
    <div class="row">      
      <div class="8u">
     	<table style="vertical-align:middle;" width=100%>
     		<tr><th><h3><font color=brown>All Factors</font></h3></th></tr>      			
     	</table>
     	
     	<table id="contributing_factor">    	
     		<tr>
     			<td style="line-height:18px;display:inline-block">
     				<%while(rs_cf.next()) { 
     					String thisID = rs_cf.getString("CFID");
     					String lastID = "NA";
     					String nextID = "NA";
     					if(!rs_cf.isFirst()) {
     						rs_cf.previous();
     						lastID = rs_cf.getString("CFID");
     						rs_cf.next();
     					}
     					if(!rs_cf.isLast()) {
     						rs_cf.next();
     						nextID = rs_cf.getString("CFID");
     						rs_cf.previous();
     					}
     					int level = CF_unstructured.getLevel(thisID); 
     					int dif = CF_unstructured.countLevelDiff(thisID, nextID);
     					if(level != 0) {     						
     						if(CF_unstructured.isSon(thisID, lastID)) {%>
     							<span id="<%=lastID %>_son" style="display:none">
     						<%}     						
     					}%>	
     					<%for(int i=0; i<level; i++) { %>
     						&nbsp;
     					<%} %>
     					<%if(dif < 0) {%>
     						<a style="text-decoration:none" id="<%=thisID %>" href="javascript:show_hiddendiv('<%=thisID%>');">
     							<span id="span_<%=thisID %>">
     								<img src="images/plus.png" title="Click to unfold" height=18 style="vertical-align:middle;display:inline-block;">
     							</span>
     						</a>
     					<%}else { %>
     						&nbsp;&nbsp;&nbsp;&nbsp;
     					<%} %>
     					<%if(level == 0) { %>
     						<font size=3 color=brown face="arial"><%=thisID %>.&nbsp;<%=rs_cf.getString("Term") %></font>
     					<%}else { %>
     						<font size=2 <%if(dif >= 0) {%>color=black <%}else{ %> color=brown <%} %>face="arial"><%=thisID %>.&nbsp;<%=rs_cf.getString("Term") %></font>
     					<%} %>
     					<img src="images/continue.png" title="Add to MY FACTORS" height=18 style="cursor:pointer;vertical-align:middle;display:inline-block;" onclick="addFactor('<%=thisID%>')">
     					<br />
     					<%for(int i=0; i<dif; i++) { %>
     						</span>
     					<%} %>    					 					
     				<%} %>     					
     			</td>     				
     		</tr>     	     	
		</table>
      </div>


  	<div class="4u">
  		<table style="vertical-align:middle;" width=100%>
     		<tr><th><h3><font color=brown>My Factors</font></h3></th></tr>      			
     	</table>
     	<%rs_cf.beforeFirst();
     	while(rs_cf.next()) {
     		String thisID = rs_cf.getString("CFID");%>
     		<table id="myCF_<%=thisID %>" style="margin-bottom:5px;display:none;">
     			<tr style="line-height:20px">     			
     				<td>
     					<img class="flipx" src="images/continue.png" title="Remove from MY FACTORS" height=18 style="cursor:pointer;vertical-align:middle;display:inline-block;" onclick="removeFactor('<%=thisID%>')">
     				</td>
     				<td>
     					<font size=3 color=black face="arial"><%=thisID%>.&nbsp;<%=rs_cf.getString("Term") %></font>
     				</td>
     			</tr>
   			</table>
     	<%} %>
     	<br />
     	<table id="search" style="display:none">
     		<tr><td>
     			<form action="similarity_cf_only.jsp" method="post" name="simi" id="simi"> 
      				<input type="hidden" value="" id="cfs" name="cfs">		
      				<input type="hidden" value="fall" id="type" name="type">		
      				<input type="submit" class="button button-small" style="width:200" value="Search" id="con" onclick="similarity(this)">			
				</form>			
				<div align=center><img src="images/loading.gif" style="display:none" id="loading"></div>
			</td></tr>
		</table>
		
    </div>
   </div>
  </article>
 </div> 
 <footer>
 </footer>
  


<%
	rs_cf.close();
}else { 
	user.setLogined(false);%>

<div class="wrapper wrapper-style3">
  <article id="report">
    <header>
      <h2>Report Your Case</h2>
      <span>Thanks for reporting new cases to VISIT.<br/> 
      Your contribution will help us give better solutions for current cases.</span> </header>
    
    <form name="login" method="post" action="login.jsp" onSubmit="return check()">
    <center>
    	<table>
    		<tr>
    			<td align=center><font size=5><b>You can submit as a registered user</b></font>
    				<br/>Manage your submitted cases easily
    			</td>
    		</tr>
    		<tr>
    			<td align=center><font color=black face=arial>Account</font></td>
    		</tr>     	
     		<tr>
     		 	<td align=center><input type="text" id="userName" name="userName" style="color:black;background-color:white;border:lightgray 2px solid;width:300px"></td>
   			</tr>	
   			<tr>
   				<td align=center><font color=black face=arial>Password</font></td>
   			</tr>
   			<tr>
   				<td align=center><input type="password" id="userPWD" name="userPWD" style="color:black;background-color:white;border:lightgray 2px solid;width:300px"></td>
    		</tr>
    		<tr>
    			<td height=30 align=center><font color=red face=arial>Sorry, wrong account name or password</font></td>
    		</tr>
    		<tr>
    			<td align=center><input type="submit" class="button button-big" value="Login & Report" id=report></td>
    		</tr>
    		<tr>
    			<td height=50></td>
    		</tr>
    		<tr>
    			<td align=center><font size=5><b>Or, you can</b></font></td>
    		</tr>
    		<tr>
    			<td height=30></td>
    		</tr>
    		<tr>
    			<td align=center><input type="submit" class="button button-big" value="Report Anonymously" id=anon></td>
    		</tr>
    </table></center>
    </form>
    
    <footer>
      
    </footer>
  </article>
</div>

<%}
rs.close();
myDBBean.disConnect();
 %>

<div class="wrapper wrapper-style4">
  <article id="contact">
    
    <footer>
      <p id="copyright"> &copy; School of Biomedical Informatics, University of Texas Health Science Center at Houston 2015</p>
    </footer>
  </article>
</div>
</body>
</html>
