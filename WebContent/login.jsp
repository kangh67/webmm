<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.Report"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="re" scope="page" class="report.Report"/>
<jsp:setProperty name="user" property="*" />
<jsp:setProperty name="answers" property="*" />


<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String userName = user.getUserName();
String userPWD = user.getUserPWD();

System.out.println("[login.jsp] User name=" + userName + ", password=" + userPWD);

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

<script>
	function del(uni_id)
	{				
		if(confirm("Are you sure to delete this record? It will be unrecoverable")) {
			window.location.href="login.jsp?del=" + uni_id;
		}
	}
</script>

<script>
	function view(uni_id, type)
	{
		document.getElementById("type").value = type;
		document.getElementById("uni").value = uni_id;
    	
    	document.forms["status"].submit();
	}
</script>

<script>
	function showtable(id)
    {      	
      	document.getElementById("table_yours").style.display="none";     				
      	document.getElementById("table_others").style.display="none";      	
      			
      	document.getElementById(id).style.display="block";
    }      	
</script>

<% 
String del = null;
try {
	del = new String(request.getParameter("del"));
	System.out.println("Try to delete: " + del);
}catch(Exception e) {
}

if(del != null) {
	re.deleteReport(myDBBean, del);
	System.out.println("Deleted: " + del);
}

ResultSet rs = myDBBean.query(sql);
if(rs.next()) {
	user.setLogined(true);
	answers.initialization();
%>
		
<div class="wrapper wrapper-style1 wrapper-first">
  <article class="5grid-layout" id="top">
    <div class="row">      
      <div class="3u">
      	<font color=black size=4 face="arial">User: <b><font color=orange face="verdana"><%=rs.getString("last_name") %>, <%=rs.getString("first_name") %></font></b></font><br />
      	<font color=black size=2 face="arial"><i>Please choose the browse scope or start a new report.</i></font><br /><br />     
      	
      	<input type="button" class="button button-small" style="width:250px" value="Your Reports" id="your" onclick="showtable('table_yours')"><br />
      	<input type="button" class="button button-small" style="width:250px" value="Others' Reports" id="others" onclick="showtable('table_others')"><br />
      	<input type="button" class="button button-small" style="width:250px" value="New Report" id="new" onclick="window.location.href='report_general.jsp'">	
      </div>


  	<div class="9u">
  	<style type="text/css">
   			.STYLE1 {background-color:#FFDAC8}
   			.STYLE2 {background-color:#F0F0F0}
   			.STYLE3 {background-color:white}
	</style> 
      
      <%ResultSet rs_userReport = re.getUserReports(myDBBean, userName); 
      	int userNum = 0;
      	while(rs_userReport.next()) {
			userNum ++;
		}	
		int index = 1;
		%>
	
	<style type="text/css">	
      	#smallnav ul
		{
			list-style-type:none;
			margin:0;
			padding:0;
			overflow:hidden;
		}
		#smallnav li
		{
			float:left;
		}
		#smallnav a:link,a:visited
		{
			display:block;
			width:220px;
			font-weight:bold;
			color:#FFFFFF;
			background-color:#bebebe;
			text-align:center;
			padding:4px;
			text-decoration:none;			
		}
		#smallnav a:hover,a:active
		{
			background-color:#cc0000;
		}
      </style>
      
      	<nav id="smallnav">
      	<ul>
			<li><a href="" style="background-color:#cc0000;">1. Report</a></li>
			<li><a href="analyze.jsp?type=fall">2. Analyze</a></li>
			<li><a href="">3. Improve</a></li>
			<li><a href="">4. Control</a></li>
		</ul>
		</nav>
		
	<br />
      <table id="table_yours" width=100%>
      	<tr>
      		<td>
      			<font size=4 color=brown face="verdana">Your Reports (<%=userNum %>)</font>
      		</td>
      		<td colspan=5>
      			<form name="yours" id="yours" method="post" action="login.jsp" onSubmit="return check()">
      				<input type=text id="search_user" name="search_user" value="" style="color:black;background-color:white;border:lightgray 2px solid;width:300px;height=20px;display:inline">  
      			 	<button id="search1" onclick="">Search</button>	     
      			</form>
      		</td>
      	</tr>      
      	<tr>
      		<td align=center width=60%><font size=3 color=brown face="arial">TITLE</font></td>
      		<td align=center><font size=3 color=brown face="arial">UPDATE</font></td>
      		<td align=center><font size=3 color=brown face="arial">COMPLETED</font></td>
      		<td colspan=3 width=25% align=center><font size=3 color=brown face="arial">ACTION</font></td>
      	</tr>
      	
      	<tr><td colspan=6>
      	<div style="overflow:auto;height:600px;">
      	
      	<table width=100%>
      	<%while(rs_userReport.previous()) { 
      		index ++;
      		String rtitle = rs_userReport.getString("rname");
      		String uptime = rs_userReport.getString("uptime");
      		String status = rs_userReport.getString("rstatus");
      		if(rtitle == null) {
      			rtitle = "No Title";
      		}
      		if(uptime != null) {
      			uptime = uptime.split(" ")[0];
      		}
      		if(status.equals("2")) {
      			status = "Yes";
      		}else {
      			status = "No";
      		}%>
      		<tr class="<%if(index%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>" onmouseover="this.className='STYLE1'" onmouseout="this.className='<%if(index%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>'">
      			<td width=55%>     					
      				<font size=3 color=black face="arial"><%=rtitle %></font>
      			</td>
      			<td>
      				<font size=3 color=black face="arial"><%=uptime %></font>
      			</td>
      			<td>
      				<font size=3 color=black face="arial"><%=status %></font>
      			</td>     			
      			
      			<td>
      				<%if(!rs_userReport.getString("rstatus").equals("2")){ %>
      					<button style="width:43px" title="Continue to Report" onclick="view('<%=rs_userReport.getString("uni_id") %>', 'continue');">con.</button>
      				<%} %>      				
      			</td>   
      			<td>
      				<%if(rs_userReport.getString("rstatus").equals("2")){ %>      					
      					<button style="width:43px" title="View or Edit" onclick="view('<%=rs_userReport.getString("uni_id") %>', 'view');">view</button>
      				<%} %>    				
      			</td> 
      			<td>
      				<button style="width:43px" title="Delete This Record" onclick="del('<%=rs_userReport.getString("uni_id") %>');">del.</button>
      			</td>    			
      		</tr>      	
      	<%} %>
      	
      </table>
      </div>
      </td></tr>
      </table>
      
      
      <%ResultSet rs_otherReport = re.getOtherReports(myDBBean, userName); 
      	userNum = 0;
      	while(rs_otherReport.next()) {
			userNum ++;
		}	
		index = 1;
		%>
		
      <table id="table_others" width=100% style="display:none">
      	<tr>
      		<td>
      			<font size=4 color=brown face="verdana">Other Reports (<%=userNum %>)</font>
      		</td>
      		<td colspan=3>
      			<button id="search_cf" onclick="window.location.href='customized_cf.jsp'">Search with contributing factors</button>
      		</td>
      	</tr>
      	<tr>
      		<td align=center width=60%><font size=3 color=brown face="arial">TITLE</font></td>
      		<td align=left><font size=3 color=brown face="arial">UPDATE</font></td>
      		<td align=center><font size=3 color=brown face="arial">COMPLETED</font></td>
      		<td width=15% align=center><font size=3 color=brown face="arial">ACTION</font></td>
      	</tr>
      	
      	<tr><td colspan=4>
      	<div style="overflow:auto;height:600px;">
      	
      	<table width=100%>
      	<%while(rs_otherReport.previous()) { 
      		index ++;
      		String rtitle = rs_otherReport.getString("rname");
      		String uptime = rs_otherReport.getString("uptime");
      		String status = rs_otherReport.getString("rstatus");
      		if(rtitle == null) {
      			rtitle = "No Title";
      		}
      		if(uptime != null) {
      			uptime = uptime.split(" ")[0];
      		}
      		if(status.equals("2")) {
      			status = "Yes";
      		}else {
      			status = "No";
      		}%>
      		<tr class="<%if(index%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>" onmouseover="this.className='STYLE1'" onmouseout="this.className='<%if(index%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>'">
      			<td width=60%>     					
      				<font size=3 color=black face="arial"><%=rtitle %></font>
      			</td>
      			<td>
      				<font size=3 color=black face="arial"><%=uptime %></font>
      			</td>
      			<td>
      				<font size=3 color=black face="arial"><%=status %></font>
      			</td>  	
      			   
      			<td align=center>
      				<%if(rs_otherReport.getString("rstatus").equals("2")){ %>      					
      					<button style="width:43px" title="View" onclick="view('<%=rs_otherReport.getString("uni_id") %>', 'view');">view</button>
      				<%} %>    				
      			</td> 
      			    			
      		</tr>      	
      	<%} %>
      	
      </table>
      </div>
      </td></tr>
      </table>      
      
      
      <form method="post" id="status" action="view.jsp">
      	<input type="hidden" value="NA" id="type" name="type">
      	<input type="hidden" value="NA" id="uni" name="uni">
      </form>
      
      <%      
      rs_userReport.close();
      rs_otherReport.close(); 
      
      %>
    
      <!--  input type="button" class="button button-big" value="New (card style)" id="new" onclick="window.location.href='report.jsp?q=new'" -->
      
          
      <br /><br /><br /><br /><br /><br />   
    </div>
   </div>
  </article>
 </div> 
 <footer>
 </footer>
  


<%

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
