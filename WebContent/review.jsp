<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.Report"%>
<%@ page import="report.Stat"%>
<%@ page import="nlp.Tokenization"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="re" scope="page" class="report.Report"/>
<jsp:useBean id="st" scope="page" class="report.Stat"/>
<jsp:useBean id="nlp" scope="page" class="nlp.RegularExpression"/>
<jsp:useBean id="token" scope="page" class="nlp.Tokenization"/>
<jsp:setProperty name="user" property="*" />
<jsp:setProperty name="answers" property="*" />
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String userName = user.getUserName();
String userPWD = user.getUserPWD();

String sql = "select * from users where user_id='" + userName + "' and passwords='" + userPWD + "'";

System.out.println("[review.jsp] Case ID: " + answers.uni_id);
System.out.println("[review.jsp] If any GENERAL questions answered: " + answers.general);
System.out.println("[review.jsp] The answered GENERAL questions:" + answers.route_gen);
System.out.println("[review.jsp] Answered GENERAL questions & answers: " + answers.info_gen);

System.out.println("[review.jsp] If any FALL questions answered: " + answers.fall);
System.out.println("[review.jsp] The answered FALL questions:" + answers.route_fall);
System.out.println("[review.jsp] Answered FALL questions & answers: " + answers.info_fall);

System.out.println("[review.jsp] If any PU questions answered: " + answers.pu);
System.out.println("[review.jsp] The answered PU questions:" + answers.route_pu);
System.out.println("[review.jsp] Answered PU questions & answers: " + answers.info_pu);

//---contributing factors
HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs = null;
ArrayList<String[]> terms = new ArrayList<String[]>();
String[] textPieces = null;

if(!answers.description.equals("NA")) {
	cfs = nlp.annotateCF(myDBBean, answers.description);
	terms = nlp.getSortedCF(myDBBean, cfs);
	textPieces = Tokenization.sbd(answers.description, false);	
}
//-------
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
	function finalSubmit()
	{
		var obj = <%=re.confirm(myDBBean, answers.uni_id)%>;
		if(confirm("Thanks for your reporting. Please click OK to confirm submission.")) {
			window.location.href="login.jsp";
		}		
	}

	function backToLogin()
	{		
		window.location.href="login.jsp";			
	}

	function similarity(o)
	{		
		if(<%=terms.size()%> != 0) {
			document.getElementById("simi_0").style.display="none";
			document.getElementById("loading_0").style.display="block";
		}
		document.getElementById("buttons").style.display="none";		
		document.getElementById("loading").style.display="block"; 
		o.form.submit(); 			
	}
	
	function showtable(id)
    {
    	var tableNum = <%=terms.size()%>;      			
      	for(var j=0; j<tableNum; j++)
      	{      				
      		document.getElementById("t" + j).style.display="none";      				
      	}
      	document.getElementById("basic").style.display="none";   	
      	document.getElementById(id).style.display="block";
    }

</script>

<style type="text/css">
   	.STYLE1 {background-color:#FFDAC8} 	 
   	.STYLE2 {background-color:#FAFAFA} 	 
</style>

<% 
ResultSet rs = myDBBean.query(sql);
if(rs.next()) {
	answers.review = true;
	ArrayList<String> filledForms = answers.showAvaForms();	
	
%>
		
<div class="wrapper wrapper-style2">
  <article id="report">
    <header>     
    
    	<%if(userName.equals(answers.reporter)) { %>   
    	<table>
    		<tr>
    			<td width=25%>
    			</td>
    			<td width=1000 align=center>
    				<div style="margin-left:<%=(285 - 76 * filledForms.size()) %>">
    				<%ArrayList<String> activatedForms = answers.showAvaForms();    				
    				
    				if(answers.general) { %>
    					<a href="report_general.jsp"><img src="images/step1_on.png" style="display:inline;float:left" height=50 title="General Information"></a>       				
    				<%}%>
    				
    				<%for(int i=0; i<activatedForms.size(); i++) {
    					boolean thisAva = answers.getActivated(activatedForms.get(i));
    					String thisTitle = answers.getTitle(activatedForms.get(i));
    					ArrayList<String> thisRoute = answers.getRoute(activatedForms.get(i));
    					
    					if(thisAva) { 
    						int stepNum = i + 2;
    						if(thisRoute != null) {%>
    							<a href="report_detail.jsp?page_type=NA&route=<%=activatedForms.get(i) %>"><img src="images/step<%=stepNum %>_on.png" style="display:inline;float:left" height=50 title="<%=thisTitle %>"></a>
    						<%}else {%>
    							<img src="images/step<%=stepNum %>_off.png" style="display:inline;float:left" height=50 title="<%=thisTitle %>">
    						<%} %>
    					<%} %>
    				<%} %>
    				
    				<%if(answers.review) {%>
    					<a href="review.jsp"><img src="images/review_on.png" style="display:inline;float:left" height=50 title="Review and Submit"></a>
    				<%}else {%>
    					<img src="images/review_off.png" style="display:inline;float:left" height=50 title="Review and Submit">
    				<%} %>
    				</div>
    			</td>
    			<td width=25%>
    			</td>
    		<tr>
    	</table>
    	<%}else { %>    		
      		<input type="button" class="button button-small" style="width:220" value="Return" id="con" onclick="backToLogin()">
      		<br /> <br /> 	
    	<%} %>
    
      	<h2>Report Review</h2>      	
                   
      	<center>  
      		<h3><font color=brown>General Information </font><%if(userName.equals(answers.reporter)) { %><a href="report_general.jsp"><img src="images/edit.jpg" height=30 title="Edit This Form"></a><%} %></h3> 		
      		<table id="general" border="1">      			
      			<tr>
      				<td width=25%></td>
      				<td width=50% style="word-break:break-all"> 
      					<b><font color=#4682B4 face=arial size=3>Case Title: </font></b> <br />
      					<font face="Consolas" color=black size=2><%=answers.caseName %></font> <br />
      					<b><font color=#4682B4 face=arial size=3>Reporter: </font></b> <br />
      					<font face="Consolas" color=black size=2><%=answers.reporter %></font><br />
      					<b><font color=#4682B4 face=arial size=3>Report Date: </font></b> <br />
      					<font face="Consolas" color=black size=2><%=answers.initime %></font><br />
      					<b><font color=#4682B4 face=arial size=3>Update Date: </font></b> <br />
      					<font face="Consolas" color=black size=2><%=answers.uptime %></font><br />
      					
      					<b><font color=#4682B4 face=arial size=3>Description: </font></b> <br />
      					<%if(answers.description.equals("NA")) {%>
      						<font face="Consolas" color=black size=2><%=answers.description %></font><br />
      					<%}else { %>
      						<table id="basic">
								<tr><td>				
									<%for(int j=0; j<textPieces.length; j++) { %>
										<font face="Consolas" color=black size=2><%=textPieces[j] %></font>
									<%} %>					
								</td></tr>
							</table>
      						<%for(int i=0; i<terms.size(); i++) { %>	
								<table id=t<%=i %> style="display:none">
									<tr><td>				
										<%for(int j=0; j<textPieces.length; j++) { %>
											<%for(int k=0; k<textPieces[j].length(); k++) {%><font face="Consolas" color=black size=2><%if(cfs.containsKey(terms.get(i)[0])) {if(cfs.get(terms.get(i)[0]) != null && cfs.get(terms.get(i)[0]).containsKey(j)) {if(cfs.get(terms.get(i)[0]).get(j).contains(k)) {%></font><font face="Consolas" color=red size=2><%}}}%><%=textPieces[j].charAt(k)%></font><%}%>
										<%} %>					
									</td></tr>
								</table>	
							<%} %>
							<b><font color=brown face=arial size=3>Contributing Factors (CF): </font></b> <br />
							<font color=brown face=arial size=2>(Click any CF below to highlight corresponding keywords in the DESCRIPTION above)</font><br />
							<table id=terms>
								<%for(int i=0; i<terms.size(); i++) { %>
									<tr title="Click to highlight the coressponding keywords in DESCRIPTION" onmouseover="this.className='STYLE1'" onmouseout="this.className='wrapper-style2'" onclick="showtable('t<%=i%>')" style="TEXT-DECORATION:none;cursor:pointer"> 				
										<td width=100><font face="Consolas" color=black size=2><%=terms.get(i)[0]%></font></td>
										<td><font face="Consolas" color=black size=2><%=terms.get(i)[1]%></font></td>				
									</tr>
								<%} %>								
							</table>
							<table id="simi_0">
								<tr>
									<td colspan=2>
										<form action="similarity_cf.jsp" method="post" name="simi_0"> 
      										<input type="hidden" value=<%=answers.uni_id %> id="queryID" name="queryID"> 
											<input type="hidden" value=<%=answers.caseName %> id="cName" name="cName">		
      										<input type="submit" class="button button-small" style="width:550" value="Click to see cases with similary CF combinations" id="con" onclick="similarity(this)">			
										</form>
									</td>
								</tr>
							</table>
							<div align=center><img src="images/loading.gif" style="display:none" id="loading_0"></div>
      					<%} %>
      					
      					<b><font color=#4682B4 face=arial size=3>Link: </font></b> <br />
      					<font face="Consolas" color=black size=2><%=answers.link %></font>
      				</td>
      				<td width=25%></td>
      			</tr>
      			
      			<%for(int i=0; i<answers.route_gen.size(); i++) { 
      				String sql_q = "select * from questions where qid ='" + answers.route_gen.get(i) + "'";	
					ResultSet rs_q = myDBBean.query(sql_q);
					rs_q.next();
					
					String[] ans_list = rs_q.getString("acontent").split("\\|\\|");
					String[] ans_user = answers.info_gen.get(answers.route_gen.get(i)).split("\\|\\|");
				%>    			
      			
      			<tr>
      				<td width=25%></td>
      				<td width=1000> 
      					<b><font color=#4682B4 face=arial size=3><%= rs_q.getString("qcontent")%></font></b>
      					<font color=#4682B4 face=arial size=2><%= rs_q.getString("qref") %></font>
      				</td>
      				<td width=25%></td>
      			</tr>
      			
      			<tr>
      				<td width=25%></td>
      				<td width=50%>
      					<font face="Consolas" color=black size=2>  						
      						<%for(int j=0; j<ans_user.length; j++) { 
      							if(ans_user[j].contains("$$")) {
      								String[] textans = ans_user[j].split("\\$\\$");
      								if(textans.length > 1) {%>
      									<b><%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %></b>
      								<%}else { %>
      									<b><%=ans_list[Integer.valueOf(textans[0])] %></b>
      								<%} %>
      							<%}else { %>      								
      								<b><%=ans_list[Integer.valueOf(ans_user[j])] %></b>
      							<%} %>
      							<br />
      						<%} %>
      					</font>
      				</td>
      				<td width=25%></td>
      			</tr>      
      			<%
      			rs_q.close(); 
      			}%>		
      		</table>      		
      		
      		<%for(int i=0; i<filledForms.size(); i++) {
      			String thisTitle = answers.getTitle(filledForms.get(i));
      			ArrayList<String> thisRoute = answers.getRoute(filledForms.get(i));
      			HashMap<String, String> thisInfo = answers.getInfo(filledForms.get(i));     			
      			%>
      			
      			<h3><font color=brown><%=thisTitle %></font><%if(userName.equals(answers.reporter)) { %> <a href="report_detail.jsp?page_type=NA&route=<%=filledForms.get(i) %>"><img src="images/edit.jpg" height=30 title="Edit This Form"></a><%} %></h3> 		
      			<table id=<%=filledForms.get(i) %> border="1">      			
      			<%for(int j=0; j<thisRoute.size(); j++) { 
      				String sql_q = "select * from questions where qid ='" + thisRoute.get(j) + "'";	
					ResultSet rs_q = myDBBean.query(sql_q);
					rs_q.next();
					
					String[] ans_list = rs_q.getString("acontent").split("\\|\\|");
					String[] ans_user = thisInfo.get(thisRoute.get(j)).split("\\|\\|");
				%>
      			<tr>
      				<td width=25%></td>
      				<td width=1000> 
      					<b><font color=#4682B4 face=arial size=3><%= rs_q.getString("qcontent")%></font></b>
      					<font color=#4682B4 face=arial size=2><%= rs_q.getString("qref") %></font>
      				</td>
      				<td width=25%></td>
      			</tr>
      			
      			<tr>
      				<td width=25%></td>
      				<td width=50%>
      					<font face="Consolas" color=black size=2>  						
      						<%for(int k=0; k<ans_user.length; k++) { 
      							if(ans_user[k].contains("$$")) {
      								String[] textans = ans_user[k].split("\\$\\$");
      								if(textans.length > 1) {%>      								
      									<b><%=ans_list[Integer.valueOf(textans[0])] %>: <%=textans[1] %></b>
      								<%}else { %>
      									<b><%=ans_list[Integer.valueOf(textans[0])] %></b>
      								<%} %>
      							<%}else { %>      								      								
      								<b><%=ans_list[Integer.valueOf(ans_user[k])] %></b>      								
      							<%} %>
      							<%if(rs_q.getString("importance").equals("1")) {
      								double percentage = st.getPercent(myDBBean, filledForms.get(i), thisRoute.get(j), ans_user[k]);	
      							%>
      							<font color=green>(<%=percentage %>%)</font>
      							<img src="images/question_mark.jpg" height=12 title="<%=percentage %> % reporters chose this">
      							<%} %>
      							<br />
      						<%} %>
      					</font>
      				</td>
      				<td width=25%></td>
      			</tr>      
      			<%
      			rs_q.close(); 
      			}%>		
      		</table>
      		<%} %>      	
      	
      	<table id="buttons">
      		<tr><td>
      		<%if(userName.equals(answers.reporter)) { %>    						
	      		<input type="button" class="button button-small" style="width:220" value="Save & Submit" id="con" onclick="finalSubmit()">
	      	<%}else { %>
    	  		<input type="button" class="button button-small" style="width:220" value="Return" id="con" onclick="backToLogin()">
    	  	<%} %>
      		</td>      		
      		<td>
      		<form action="similarity_cf.jsp" method="post" name="simi"> 
      			<input type="hidden" value=<%=answers.uni_id %> id="queryID" name="queryID"> 
				<input type="hidden" value=<%=answers.caseName %> id="cName" name="cName">		
      			<input type="submit" class="button button-small" style="width:220" value="Similar Cases" id="con" onclick="similarity(this)">			
			</form>
			</td>
			<td>
			<form action="solution_cf.jsp" method="post" name="solu"> 
      			<input type="hidden" value=<%=answers.uni_id %> id="query_ID" name="query_ID">      			
				<input type="hidden" value=<%=filledForms.get(0) %> id="event_type" name="event_type">		
      			<input type="submit" class="button button-small" style="width:220" value="Solutions" id="con" onclick="similarity(this)">			
			</form>			
			</td>
			</tr>
		</table>
		<div align=center><img src="images/loading.gif" style="display:none" id="loading"></div>
		
		</center>      
	   
    
    <footer>
    </footer>
  </article>
</div>

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
    			<td height=30 align=center><font color=red face=arial>Time out. Please login again.</font></td>
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
