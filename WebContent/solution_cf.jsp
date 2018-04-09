<%@ page language="java" import="java.util.*,java.sql.*,java.util.Map.Entry" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.Report"%>
<%@ page import="report.CalFingerprint"%>
<%@ page import="report.CaseForRank"%>
<%@ page import="report.ContinueAndEdit"%>
<%@ page import="solution.MatchSolution"%>
<%@ page import="solution.Solution_entry"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="re" scope="page" class="report.Report"/>
<jsp:useBean id="cf" scope="page" class="report.CalFingerprint"/>
<jsp:useBean id="cae" scope="page" class="report.ContinueAndEdit"/>
<jsp:useBean id="ms" scope="page" class="solution.MatchSolution"/>
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
	
	//get uni_id of query
	String queryID = new String(request.getParameter("query_ID"));		
	
	//get event type
	String type = new String(request.getParameter("event_type"));	
	
	System.out.println("[solution_cf.jsp] queryID = " + queryID + ", event_type = " + type);
	
	ResultSet rs_query = myDBBean.query("select * from report_general where uni_id='" + queryID + "'");
	rs_query.next();
	String queryName = rs_query.getString("rname");	
	rs_query.close();
	
	HashMap<String, ArrayList<String>> cf_sid = ms.getCFID_sids(myDBBean, queryID, type);	
	
	NewCase answerCopy = ms.copyAnswerSession(myDBBean, queryID);
	
	ArrayList<Solution_entry> solutions = ms.getEligibleSolutions_cf(myDBBean, ms.sid_name.keySet(), type);
	
	String absPath = new java.io.File(application.getRealPath("solution_cf.jsp")).getParent();	
	String downloadFileName = ms.generateSolutionFileForDownload(myDBBean, solutions, absPath, queryID);
	
	//HashMap<String, Integer> contributingFactor = ms.ContributingFactorStat(solutions);
	//HashMap<String, Integer> solutionType = ms.SolutionTypeStat(solutions);
	
	//System.out.println(solutions.size());
	//System.out.println(contributingFactor);
	//System.out.println(solutionType);
%>

<script>
	function similarity(o, i)
	{		
		document.getElementById("button" + i).style.display="none"; 
		document.getElementById("loading" + i).style.display="block"; 
		o.form.submit(); 			
	}

	function show_hiddendiv(o){
		document.getElementById(o + "_details").style.display="block";
		document.getElementById(o).href="javascript:hidden_showdiv('" + o + "');";
		document.getElementById("span_" + o).innerHTML='<img src="images/minus.png" height=20>';
	}
	
	function hidden_showdiv(o){
		document.getElementById(o + "_details").style.display="none";
		document.getElementById(o).href="javascript:show_hiddendiv('" + o + "');";
		document.getElementById("span_" + o).innerHTML='<img src="images/plus.png" title="Get solutions belong to this contributing factor" height=20>';
	}
	
	function changetopic(presentingType){
		document.getElementById("contributing_factor").style.display="none";
		document.getElementById("solution_type").style.display="none";
		document.getElementById(presentingType).style.display="block";
	}
</script>
		
<div class="wrapper wrapper-style1 wrapper-first">
  <article class="5grid-layout" id="top">
    <div class="row">      
      <div class="4u">
      <b><font color=black size=4 face="arial">Case Summary:</font></b>
      	<table width=100%>
      		<tr><td>      				
      			<b><font color=black>Name: </font><font color=darkorange face="verdana"><a href="view.jsp?uni=<%=queryID%>&type=view" style="text-decoration:none;color:darkorange"><%=queryName %></a></font>, </b>
      			<b><font color=black size=2 face="arial">Event Type: </font><font color=darkorange size=2 face="arial"><%=answerCopy.getTitle(type)%></font></b>			
      		</td></tr>
      		<tr><td style="line-height:20px">
      			<font size=3 color=black face="arial">
      				<b><i>Description: </i></b> <br />
      				<%=answerCopy.description%>
      			</font>
      		</td></tr>
      		<tr><td style="line-height:20px"><font size=3 color=#004B97 face="arial">
      			<font color=black>
      				<b><i>Q&A:</i></b> <br />
      			</font>
      		<%
      			ArrayList<String> route = answerCopy.getRoute(type);
      			HashMap<String, String> info = answerCopy.getInfo(type);
      			
      			for(int i=0; i<route.size(); i++) {
      				String sql_q = "select * from questions where qid ='" + route.get(i) + "'";	
					ResultSet rs_q = myDBBean.query(sql_q);
					rs_q.next();
					
					String[] ans_list = rs_q.getString("acontent").split("\\|\\|");
					String[] ans_user = info.get(route.get(i)).split("\\|\\|");
      		%>      			
      			Q<%=route.get(i).replaceAll(type + "_", "") %>: <%=rs_q.getString("qcontent") %><br />
      			<font color=black>
      			
      				<%for(int k=0; k<ans_user.length; k++) { 
      					if(ans_user[k].contains("$$")) {
      						String[] textans = ans_user[k].split("\\$\\$");
      						if(textans.length > 1) {%>      								
      							&nbsp;&nbsp;&nbsp;<%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %><br/>
      						<%}else { %>
      							&nbsp;&nbsp;&nbsp;<%=ans_list[Integer.valueOf(textans[0])] %><br/>
      						<%} %>
      					<%}else { %>      								
      						&nbsp;&nbsp;&nbsp;<%=ans_list[Integer.valueOf(ans_user[k])] %><br/>
      					<%} 
      				}%> 				
      			
      			</font>
      		
      		<%
      				rs_q.close();
      			}
      		%>
      		</font>
      		</td></tr> 		
      		
      	</table>
     	
      </div>


  	<div class="8u"> 
  		<div align=center><b><font size=5 color=brown face="verdana">SOLUTION RECOMMENDATION</font></b></div>
  		<div align=center>
  			<b><font size=3 color=darkorange face="verdana"><%=solutions.size() %></font></b>
  			<font size=3 color=#004B97 face="arial"> unique solutions were recommended for your case</font>
  			<br />  			
      		
      		<a href="solutionFiles/<%=downloadFileName%>"><img src="images/download.png" title="Download all recommended solutions" height=25 align=bottom></a>
  		</div>
  	
     	<table id="contributing_factor">    	
     	<%for (int i=0; i<ms.orderedCFs.size(); i++) { %>
     		<tr>
     			<td>
     				<a style="text-decoration:none" id="cf_<%=i %>" href="javascript:show_hiddendiv('cf_<%=i%>');">
     					<span id="span_cf_<%=i%>">
     						<img src="images/plus.png" title="Click to review all <%=cf_sid.get(ms.orderedCFs.get(i)).size() %> solutions belong to this contributing factor" height=20>
     					</span>
     				</a>
     			</td>
     			<td colspan = "3">
     				<font size=4 color=brown face="arial"><%=ms.orderedCFs.get(i) %>   <%=ms.cf_name.get(ms.orderedCFs.get(i)) %>(<font size=4 color=darkorange face="verdana"><%=cf_sid.get(ms.orderedCFs.get(i)).size() %></font>)</font>
     			</td>
     		</tr>
     		<tr>
     			<td></td>
     			<td>
     				<span style="display:none" id="cf_<%=i %>_details">
     					<table>     						
     						<% for (int j=0; j<cf_sid.get(ms.orderedCFs.get(i)).size(); j++) { %>     							
     							<tr>     										
     								<td><b><font size=3 color=black face="arial">&bull;</font></b></td>
     								<td><b><font size=3 color=black face="arial"><%=ms.sid_name.get(cf_sid.get(ms.orderedCFs.get(i)).get(j)) %></font></b></td>
     								<td><img src="images/like.png" title="This solution is helpful" height=20>&nbsp;<img src="images/dislike.png" title="This solution is not helpful" height=20></td>
     							</tr>     							
     						<%} %>     							
     					</table>     					
     				</span>     				
     			</td>
     		</tr>     		
     	<%} %>     	
		</table>			
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
