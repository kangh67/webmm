<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.Report"%>
<%@ page import="report.Stat"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="re" scope="page" class="report.Report"/>
<jsp:useBean id="st" scope="page" class="report.Stat"/>
<jsp:setProperty name="user" property="*" />
<jsp:setProperty name="answers" property="*" />


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
	user.setLogined(true);
	answers.initialization();
	String type = new String(request.getParameter("type"));
	System.out.println("[analyze.jsp] Type = " + type);
	
	ArrayList<String> questionList = st.getQuestionList(myDBBean, type);
%>

<script>
	function showtable(id)
    {
      	var tableNum = <%=questionList.size()%>;   			
      			
      	for(var j=1; j<tableNum+1; j++)
      	{      				
      		document.getElementById("table" + j).style.display="none";      				
      	}
      			
      	document.getElementById(id).style.display="block";
    }      	
	
	function changetype(type){  		
  		window.location.href="analyze.jsp?type=" + type; 
  	}
</script>
		
<div class="wrapper wrapper-style1 wrapper-first">
  <article class="5grid-layout" id="top">
    <div class="row">      
      <div class="3u">
      	
      	<style type="text/css">
   			.STYLE1 {background-color:#FFDAC8}
   			.STYLE2 {background-color:#F0F0F0}
   			.STYLE3 {background-color:white}
		</style>
		
      	<font color=black size=4 face="arial">User: <b><font color=orange face="verdana"><%=rs.getString("last_name") %>, <%=rs.getString("first_name") %></font></b></font><br />
      	<font color=black size=2 face="arial"><i>Please specify subtype and question.</i></font><br />
      	<b><font color=black size=2 face="arial">Event Type: </font></b>
      	<select id="subtype" name="subtype" onchange="changetype(this.value);">
      			<option value="fall">FALL</option>		
      			<option value="pu">PRESSURE ULCER</option>
      	</select>
      	
      	<table width=100%>      	
      		<%
      		for(int i=0; i<questionList.size(); i++) {
      			String[] questionInfo = st.getQuestionInfo(myDBBean, type, questionList.get(i));      	
      		%>
      			<tr class="<%if(i%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>" onmouseover="this.className='STYLE1'" onmouseout="this.className='<%if(i%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>'" onclick="showtable('table<%=i+1%>');" style="line-height:22px">
      				<td>
      					<b><font size=3 color=black face="arial">Q<%=questionInfo[0]%>:</font></b>
      				</td>
      				<td>
      					<font size=3 color=#004B97 face="arial"><%=questionInfo[1]%></font>
      				</td>
      			</tr>
      		<%
      		}
      		%>
      	</table>
      </div>


  	<div class="9u">  	
      
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
			<li><a href="login.jsp">1. Report</a></li>
			<li><a href="" style="background-color:#cc0000;">2. Analyze</a></li>
			<li><a href="">3. Improve</a></li>
			<li><a href="">4. Control</a></li>
		</ul>
		</nav>
		
	<br />	
	
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">      
      google.charts.load('current', {'packages':['corechart']});      
      google.charts.setOnLoadCallback(drawChart);
      
      function drawChart() {
    	<%
    	for(int i=0; i<questionList.size(); i++) {    		
			String[] answerList = st.getAnswerList(myDBBean, questionList.get(i));
			int[] dis_user = st.getDistribution_user(myDBBean, type, questionList.get(i), userName);
			int[] dis_global = st.getDistribution_global(myDBBean, type, questionList.get(i));
			double[] per_user = st.getPercentage(dis_user);
			double[] per_global = st.getPercentage(dis_global);	
    	%>
        	var data<%=i+1%>_1 = new google.visualization.DataTable();
        	data<%=i+1%>_1.addColumn('string', 'Answers');
        	data<%=i+1%>_1.addColumn('number', 'Counts');        
			<%for(int j=0; j<answerList.length; j++) {%>
				data<%=i+1%>_1.addRows([["<%= answerList[j]%>", <%= dis_global[j]%>]]);        
        	<%}%>        
        	var option<%=i+1%>_1 = {
        		title:'Answer Distribution - Global',
        		pieSliceText:'none',
            	width:500,
            	height:400};        
        	var chart<%=i+1%>_1 = new google.visualization.PieChart(document.getElementById('global<%=i+1%>'));
        	chart<%=i+1%>_1.draw(data<%=i+1%>_1, option<%=i+1%>_1);
        
        
        	var data<%=i+1%>_2 = new google.visualization.DataTable();
    		data<%=i+1%>_2.addColumn('string', 'Answers');
    		data<%=i+1%>_2.addColumn('number', 'Counts');    
			<%for(int j=0; j<answerList.length; j++) {%>
    			data<%=i+1%>_2.addRows([["<%= answerList[j]%>", <%= dis_user[j]%>]]);        
    		<%}%>		
    		var option<%=i+1%>_2 = {
    			title:'Answer Distribution - <%=userName%>',
    			pieSliceText:'none',
                width:500,
                height:400};
    		var chart<%=i+1%>_2 = new google.visualization.PieChart(document.getElementById('user<%=i+1%>'));
    		chart<%=i+1%>_2.draw(data<%=i+1%>_2, option<%=i+1%>_2);
    	
    	
    		var data<%=i+1%>_3 = new google.visualization.DataTable();
        	data<%=i+1%>_3.addColumn('string', 'Answers');
        	data<%=i+1%>_3.addColumn('number', 'Global (%)');
        	data<%=i+1%>_3.addColumn('number', '<%=userName%> (%)');        
        	<%for(int j=0; j<answerList.length; j++) {%>
				data<%=i+1%>_3.addRows([["<%= answerList[j]%>", <%= per_global[j]%>, <%= per_user[j]%>]]);        
			<%}%>
        	var options<%=i+1%>_3 = {
        		title:'Comparison between <%=userName%> and global',
        		orientation:'vertical',        		
            	width:1000,
               	height:<%=30 * answerList.length + 200%>};
        	var chart<%=i+1%>_3 = new google.visualization.ColumnChart(document.getElementById('comp<%=i+1%>'));
        	chart<%=i+1%>_3.draw(data<%=i+1%>_3, options<%=i+1%>_3);
        <%
    	}
    	%>
      }      
      
    </script>
    
    <%
    for(int i=0; i<questionList.size(); i++) {
    	String[] questionInfo = st.getQuestionInfo(myDBBean, type, questionList.get(i));    
    %>   	
		<table id="table<%=i+1%>" width=100% <%if(i != 0){%>style="display:none"<%}%>>
      		<tr>
      			<td colspan="2">
      				<font color=#4682B4 face=arial size=5><b>Q<%=questionInfo[0] %>: </b><%=questionInfo[1]%></font>
      				<font color=#4682B4 face=arial size=4><%=questionInfo[2]%></font>
      			</td>
      		</tr>
      		<tr>
      			<td><div id="global<%=i+1%>"></div></td>
      			<td><div id="user<%=i+1%>"></div></td>
      		</tr>
      		<tr>
      			<td colspan="2"><div id="comp<%=i+1%>"></div></td>      	
      		</tr>      	
      	</table>
	<%
    }
	%>
      	
      <!--  input type="button" class="button button-big" value="New (card style)" id="new" onclick="window.location.href='report.jsp?q=new'" -->
        
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
