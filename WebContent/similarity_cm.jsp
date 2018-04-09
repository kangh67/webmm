<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.Report"%>
<%@ page import="report.CalFingerprint"%>
<%@ page import="report.CaseForRank"%>
<%@ page import="report.ContinueAndEdit"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="re" scope="page" class="report.Report"/>
<jsp:useBean id="cf" scope="page" class="report.CalFingerprint"/>
<jsp:useBean id="cae" scope="page" class="report.ContinueAndEdit"/>
<jsp:setProperty name="user" property="*" />
<jsp:setProperty name="answers" property="*" />


<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String userName = user.getUserName();
String userPWD = user.getUserPWD();

System.out.println(userName + "\t" + userPWD);

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
	
	//get uni_id and caseName of query
	String queryID = new String(request.getParameter("queryID"));
	
	ResultSet rs_query = myDBBean.query("select * from report_general where uni_id='" + queryID + "'");	
	rs_query.next();
	String caseName = rs_query.getString("rname");	
	
	//get similarity list
	ArrayList<CaseForRank> simiList = cf.getSimilarityList(myDBBean, queryID);
	CaseForRank query = new CaseForRank(queryID, caseName, 1.0);
	Collections.reverse(simiList);
	simiList.add(query);
	Collections.reverse(simiList);	
%>

<script>
	function similarity(o, i)
	{		
		document.getElementById("button" + i).style.display="none"; 
		document.getElementById("loading" + i).style.display="block"; 
		o.form.submit(); 			
	}

</script>

<script>
	function showtable(id)
    {
      	var tableNum;
      	if(<%=simiList.size()%> > 10)
      	{
      		tableNum = 11;
      	}
      	else
      	{
      		tableNum = <%=simiList.size()%>;
      	}      		      			
      			
      	for(var j=0; j<tableNum; j++)
      	{      				
      		document.getElementById("c" + j).style.display="none";      				
      	}    		
      			
      	document.getElementById(id).style.display="block";
	}   	
</script>
		
<div class="wrapper wrapper-style1 wrapper-first">
  <article class="5grid-layout" id="top">
    <div class="row">      
      <div class="4u">
     	<style type="text/css">
   			.STYLE1 {background-color:#FFDAC8}
   			.STYLE2 {background-color:white}
   			.STYLE3 {background-color:#F0F0F0}
		</style>
		
      	<font color=black size=4 face="arial">User: <b><font color=orange face="verdana"><%=rs.getString("last_name") %>, <%=rs.getString("first_name") %></font></b></font><br />
      	<font color=black size=2 face="arial"><i>The most similar cases to </i></font><font color=black size-3 face="arial"><b><a onclick="showtable('c0')" style="TEXT-DECORATION:none;cursor:pointer"><%=caseName %></a></b></font><br />
      	<font color=black size=2 face="arial"><i>Click any of them of details</i></font><br />     
      	
      	<table id="list" style="vertical-align:middle;" width=100%>      		
      		<%
      		//c0 is query, c1 - c10 is the similarity list
      		for(int i=1; i<simiList.size() && i<=10; i++) {%>
      			<tr <%if(i%2==0) { %> bgcolor=#F0F0F0<%} %> class="<%if(i%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>" onclick="showtable('c<%=i%>');" onmouseover="this.className='STYLE1'" onmouseout="this.className='<%if(i%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>'" style="TEXT-DECORATION:none;cursor:pointer">      				
      				<td>
      					<font size=3 color=black face="arial"><%=i %></font>
      				</td>
      				<td>
      					<font size=3 color=black face="arial"><%=simiList.get(i).name%></font>     					
      				</td> 
      				<td>
      					<%double score = cf.getSignificantFigure(simiList.get(i).score, 3);
      					if(score >= 0.8) {%>
      						<b><font size=3 color=darkgreen face="arial">
      					<%}else if(score >= 0.71) {%>
      						<font size=3 color=green face="arial">
      					<%}else { %>
      						<font size=3 color=black face="arial">
      					<%} %>
      					<%=score%>
      					<%if(score >=0.8) { %>
      						**</b>
      					<%}else if(score >= 0.71) {%>
      						*
      					<%}%>
      					     							
      				</td>	      				
      			</tr>      			
      		<%} %>      		
      	</table>
      	<font color=black size=2 face="arial"><i>** quite similar (p value<0.01)</i></font><br /> 
      	<font color=black size=2 face="arial"><i>* similar (p value<0.05)</i></font><br />
      </div>


  	<div class="8u"> 	
     	<%ResultSet rs_list = null; %>
     	<%ResultSet rs_q = null; //question%>     	
     	<%for(int i=0; i<simiList.size() && i<=10; i++) { 
     		rs_list = myDBBean.query("select * from report_general where uni_id='" + simiList.get(i).id + "'");
     		rs_list.next(); 
     		rs_query = myDBBean.query("select * from report_general where uni_id='" + queryID + "'");
			rs_query.next();
     		String colSpan = "2";
     		if(i==0) {
     			colSpan = "1";
     		}
     		%>
    		<table id="c<%=i %>" style="vertical-align:middle;display:none;" width=100%>      			
      			<tr><th colspan=<%=colSpan %>><h3><font color=brown>General Information</font></h3></th></tr>
      			<tr>      				
      				<td style="word-break:break-all" colspan=<%=colSpan %>> 
      					<b><font color=#4682B4 face=arial size=3>Case Title: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0) { %>
      					<td style="word-break:break-all" width=50%> 
      						<b><font face="Consolas" <%if(re.judgeDifference(rs_list.getString("rname"), rs_query.getString("rname"))){ %>color=red<%}else{ %>color=black<%} %> size=2><%=rs_list.getString("rname") %></font></b>
      					</td>
      				<%} %>
      				<td style="word-break:break-all"> 
      					<b><font face="Consolas" color=black size=2><%=rs_query.getString("rname") %>(query)</font></b>
      				</td>
      			</tr>
      			<tr>
      				<td style="word-break:break-all" colspan=<%=colSpan %>>
      					<b><font color=#4682B4 face=arial size=3>Reporter: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0) { %>
      					<td style="word-break:break-all" width=50%> 
      						<font face="Consolas" <%if(re.judgeDifference(rs_list.getString("reporter"), rs_query.getString("reporter"))){ %>color=red<%}else{ %>color=black<%} %> size=2><%=rs_list.getString("reporter") %></font>
      					</td>
      				<%} %>
      				<td style="word-break:break-all">
      					<font face="Consolas" color=black size=2><%=rs_query.getString("reporter") %></font>
      				</td>
      			</tr>
      			<tr>
      				<td style="word-break:break-all" colspan=<%=colSpan %>>
      					<b><font color=#4682B4 face=arial size=3>Description: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0){ %>
      					<td style="word-break:break-all" width=50%>
      						<font face="Consolas" <%if(re.judgeDifference(rs_list.getString("des"), rs_query.getString("des"))){ %>color=red<%}else{ %>color=black<%} %> size=2><%=rs_list.getString("des") %></font>
      					</td>
      				<%} %>
      				<td style="word-break:break-all">
      					<font face="Consolas" color=black size=2><%=rs_query.getString("des") %></font>
      				</td>
      			</tr>
      			<tr>
      				<td style="word-break:break-all" colspan=<%=colSpan %>>
      					<b><font color=#4682B4 face=arial size=3>Link: </font></b>
      				</td>
      			<tr>
      			<tr>
      				<%if(i != 0){ %>
      					<td style="word-break:break-all" width=50%>
      						<font face="Consolas" <%if(re.judgeDifference(rs_list.getString("link"), rs_query.getString("link"))){ %>color=red<%}else{ %>color=black<%} %> size=2><%=rs_list.getString("link") %></font>
      					</td>
      				<%} %>
      				<td style="word-break:break-all">
      					<font face="Consolas" color=black size=2><%=rs_query.getString("link") %></font>      					
      				</td>      				
      			</tr>
      			
      			<%
      			//herf_2, herf_7
      			String[] g_question = new String[2];
      			g_question[0] = "herf_2";
      			g_question[1] = "herf_7";
      			for(int k=0; k<g_question.length; k++) {
      				rs_q = myDBBean.query("select * from questions where qid='" + g_question[k] + "'");
					rs_q.next();					
					String[] ans_list = rs_q.getString("acontent").split("\\|\\|");
					String[] ans_user = rs_list.getString(g_question[k]).split("\\|\\|");
					String[] ans_query = rs_query.getString(g_question[k]).split("\\|\\|"); %>
					<tr>      				
      					<td colspan=<%=colSpan %>> 
      						<b><font color=#4682B4 face=arial size=3><%= rs_q.getString("qcontent")%></font></b>
      						<font color=#4682B4 face=arial size=2><%= rs_q.getString("qref") %></font>
      					</td>      				
      				</tr>
      				<tr>      				
      					<%if(i != 0) {%>
      					<td style="word-break:break-all" width=50%>
      						<font face="Consolas" <%if(re.judgeDifference(rs_list.getString(g_question[k]), rs_query.getString(g_question[k]))){ %>color=red<%}else{ %>color=black<%} %> size=2>  						
      							<%for(int j=0; j<ans_user.length; j++) { 
      								if(ans_user[j].contains("$$")) {
      									String[] textans = ans_user[j].split("\\$\\$");
      									if(textans.length > 1) {%>
      										<%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %>
      									<%}else { %>
      										<%=ans_list[Integer.valueOf(textans[0])] %>
      									<%} %>
      								<%}else { %>      								
      									<%=ans_list[Integer.valueOf(ans_user[j])] %>
      								<%} %>
      								<br />
      							<%} %>
      						</font>
      					</td>
      					<%} %>
      					<td>
      						<font face="Consolas" color=black size=2>  						
      							<%for(int j=0; j<ans_query.length; j++) { 
      								if(ans_query[j].contains("$$")) {
      									String[] textans = ans_query[j].split("\\$\\$");
      									if(textans.length > 1) {%>
      										<%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %>
      									<%}else { %>
      										<%=ans_list[Integer.valueOf(textans[0])] %>
      									<%} %>
      								<%}else { %>      								
      									<%=ans_list[Integer.valueOf(ans_query[j])] %>
      								<%} %>
      								<br />
      							<%} %>
      						</font>
      					</td>
      				</tr>      				
      			<%} %>  
      			
      			<%
      			//one of the nive categories
      			if(!rs_list.getString("formn").equals("NA")) {
      				String thisID = rs_list.getString("uni_id");
      				String thisTitle = answers.getTitle(rs_list.getString("formn"));
      				ArrayList<String> allQuestions = cae.getDetailQuestions(myDBBean, rs_list.getString("formn"));
     				
     				rs_list = myDBBean.query("select * from report_" + rs_list.getString("formn") + " where uni_id='" + thisID + "'");
     				rs_list.next();   
     				
     				rs_query = myDBBean.query("select * from report_" + rs_query.getString("formn") + " where uni_id='" + queryID + "'"); 
     				rs_query.next();	
     				%>
     				
     				<tr><th colspan=<%=colSpan %>><h3><font color=brown><%=thisTitle %></font></h3></th></tr>
      				
      				<%for(int k=0; k<allQuestions.size(); k++) { 
      					rs_q = myDBBean.query("select * from questions where qid='" + allQuestions.get(k) + "'");
						rs_q.next();					
						String[] ans_list = rs_q.getString("acontent").split("\\|\\|");					
						String[] ans_user = null;
						String[] ans_query = null;
						if(rs_list.getString(allQuestions.get(k)) != null) {
							ans_user = rs_list.getString(allQuestions.get(k)).split("\\|\\|");
						}
						if(rs_query.getString(allQuestions.get(k)) != null) {
							ans_query = rs_query.getString(allQuestions.get(k)).split("\\|\\|");
						}%>						
      					
      					<tr>      				
      						<td colspan=<%=colSpan %>> 
      							<b><font color=#4682B4 face=arial size=3><%=(k+1)%>. <%= rs_q.getString("qcontent")%></font></b>
      							<font color=#4682B4 face=arial size=2><%= rs_q.getString("qref") %></font>
      						</td>      				
      					</tr>
      					
      					<tr>      				
      						<%if(i != 0) { %>
      							<td>
      								<font face="Consolas" <%if(re.judgeDifference(rs_list.getString(allQuestions.get(k)), rs_query.getString(allQuestions.get(k)))){ %>color=red<%}else{ %>color=black<%} %> size=2>       							
      								<%if(ans_user == null) {%>
      									<i>Not answered</i>
      								<%} else {%>
      									<%for(int j=0; j<ans_user.length; j++) { 
      										if(ans_user[j].contains("$$")) {
      											String[] textans = ans_user[j].split("\\$\\$");
      											if(textans.length > 1) {%>
      												<%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %>
      											<%}else { %>
      												<%=ans_list[Integer.valueOf(textans[0])] %>
      											<%} %>
      										<%}else { %>      								
      											<%=ans_list[Integer.valueOf(ans_user[j])] %>
      										<%} %>
      										<br />
      									<%} %>
      								<%} %>      							
      								</font>
      							</td>     
      						<%} %> 	
      						<td>
      							<font face="Consolas" color=black size=2>       							
      								<%if(ans_query == null) {%>
      									<i>Not answered</i>
      								<%} else {%>
      									<%for(int j=0; j<ans_query.length; j++) { 
      										if(ans_query[j].contains("$$")) {
      											String[] textans = ans_query[j].split("\\$\\$");
      											if(textans.length > 1) {%>
      												<%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %>
      											<%}else { %>
      												<%=ans_list[Integer.valueOf(textans[0])] %>
      											<%} %>
      										<%}else { %>      								
      											<%=ans_list[Integer.valueOf(ans_query[j])] %>
      										<%} %>
      										<br />
      									<%} %>
      								<%} %>      							
      							</font>
      						</td> 			
      					</tr> 
      			
      				<%} %>
      				
      			<%} %>
      			
      			<tr><td align=center colspan=<%=colSpan %>>      				
      				<form action="similarity_cm.jsp" method="post" name="simi"> 
      					<input type="hidden" value=<%=rs_list.getString("uni_id") %> id="queryID" name="queryID">	     					
      					<input type="submit" id="button<%=i %>" class="button button-small" value="Similar Cases to <%=rs_list.getString("uni_id") %>" id="con" onclick="similarity(this, '<%=i %>')">					
					</form>						
					<div align=center><img src="images/loading.gif" style="display:none" id="loading<%=i %>"></div>
				</td></tr>
      			
      		</table>   
      		
      		<%rs_q.close(); %>  		
      		<%rs_list.close(); %> 		
      		
    	<%} %>  	
		
    </div>
   </div>
  </article>
 </div> 
 <footer>
 </footer>
  
<%rs_query.close(); %>

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
