<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.Report"%>
<%@ page import="report.CalFingerprint"%>
<%@ page import="report.CaseForRank"%>
<%@ page import="report.ContinueAndEdit"%>
<%@ page import="nlp.Tokenization"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="re" scope="page" class="report.Report"/>
<jsp:useBean id="cf" scope="page" class="report.CalFingerprint"/>
<jsp:useBean id="cae" scope="page" class="report.ContinueAndEdit"/>
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
	System.out.println("[similarity_cf.jsp] queryID received: " + queryID);
	
	ResultSet rs_query = myDBBean.query("select * from report_general where uni_id='" + queryID + "'");	
	rs_query.next();
	String caseName = rs_query.getString("rname");	
	
	//get similarity list
	ArrayList<CaseForRank> simiList = cf.getSimilarityList_cf(myDBBean, queryID);
	CaseForRank query = new CaseForRank(queryID, caseName, 2.0);
	Collections.reverse(simiList);
	simiList.add(query);
	Collections.reverse(simiList);	
	
	//get cfs for query
	HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs_q = null;
    ArrayList<String[]> terms_q = new ArrayList<String[]>();
    String[] textPieces_q = null;

    if(!rs_query.getString("des").equals("NA")) {
    	cfs_q = nlp.annotateCF(myDBBean, rs_query.getString("des"));
      	terms_q = nlp.getSortedCF(myDBBean, cfs_q);
      	textPieces_q = Tokenization.sbd(rs_query.getString("des"), false);	
    }
%>

<script>
	function similarity(o, i)
	{		
		document.getElementById("button" + i).style.display="none"; 
		document.getElementById("loading" + i).style.display="block"; 
		o.form.submit(); 			
	}
	
	function similarity_(id, i)
	{		
		document.getElementById("simi_" + i).style.display="none";  
		document.getElementById("button" + i).style.display="none"; 
		document.getElementById("loading_" + i).style.display="block"; 
		document.getElementById("loading" + i).style.display="block"; 
		
		document.getElementById("changeQuery").queryID.value = id;
    	
    	document.forms["changeQuery"].submit();		
	}

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
	
	function showtable_cf_left(id, tabNum, termSize)
    {    			
      	for(var j=0; j<termSize; j++)
      	{      				
      		document.getElementById("t" + tabNum + "_" + j).style.display="none";      				
      	}
      	document.getElementById("basic_" + tabNum).style.display="none";   	
      	document.getElementById(id).style.display="block";
    }
	
	function showtable_cf_right(id, tabNum, termSize)
    {    			
      	for(var j=0; j<termSize; j++)
      	{      				
      		document.getElementById("t_q" + tabNum + "_" + j).style.display="none";      				
      	}
      	document.getElementById("basic_q_" + tabNum).style.display="none";   	
      	document.getElementById(id).style.display="block";
    }
	
	function view(uni_id, type)
	{
		document.getElementById("type").value = type;
		document.getElementById("uni").value = uni_id;
    	
    	document.forms["status"].submit();
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
   			
   			.STYLE4 {background-color:#F0F0F0;word-break:break-all;}
   			.STYLE5 {word-break:break-all;padding:10px;line-height:18px;}
   			.STYLE6 {background-color:#FFDAC8;word-break:break-all;padding:10px;}
		</style>
		
      	<font color=black size=4 face="arial">User: <b><font color=orange face="verdana"><%=rs.getString("last_name") %>, <%=rs.getString("first_name") %></font></b></font><br />
      	<font color=black size=2 face="arial"><i>The most similar cases to </i></font><font color=black size-3 face="arial"><b><a onclick="showtable('c0')" style="TEXT-DECORATION:none;cursor:pointer"><%=caseName %></a></b></font><br />
      	<font color=black size=2 face="arial"><i>Click any of them for details</i></font><br />     
      	
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
  		<table style="vertical-align:middle;" width=100%>
     			<tr><th><h3><font color=brown>General Information</font></h3></th></tr>
      			<tr><td align=center><font color=red size=3>(Differences from query case are highlighted.)</font></tr>
     	</table>
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
      			
      			<tr>      				
      				<td class="STYLE4" colspan=<%=colSpan %> align=center> 
      					<b><font color=#4682B4 face=arial size=3>Case Title: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0) { %>
      					<td class="STYLE5" width=50% align=center title="Click to review this case only" onmouseover="this.className='STYLE6'" onmouseout="this.className='STYLE5'" onclick="view('<%=rs_list.getString("uni_id") %>', 'view');" style="TEXT-DECORATION:none;cursor:pointer"> 
      						<font face="Consolas" color=black size=2><%=rs_list.getString("rname") %></font>
      					</td>
      				<%} %>
      				<td class="STYLE5" align=center title="Click to review the query case only" onmouseover="this.className='STYLE6'" onmouseout="this.className='STYLE5'" onclick="view('<%=rs_query.getString("uni_id") %>', 'view');" style="TEXT-DECORATION:none;cursor:pointer"> 
      					<font face="Consolas" color=black size=2><%=rs_query.getString("rname") %></font> <b><font color=black size=3>(QUERY)</font></b>
      				</td>
      			</tr>
      			<tr>
      				<td class="STYLE4" colspan=<%=colSpan %> align=center>
      					<b><font color=#4682B4 face=arial size=3>Reporter: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0) { %>
      					<td class="STYLE5" width=50% align=center> 
      						<font face="Consolas" color=black size=2><%=rs_list.getString("reporter") %></font>
      					</td>
      				<%} %>
      				<td class="STYLE5" align=center>
      					<font face="Consolas" color=black size=2><%=rs_query.getString("reporter") %></font>
      				</td>
      			</tr>
      			<tr>
      				<td class="STYLE4" colspan=<%=colSpan %> align=center>
      					<b><font color=#4682B4 face=arial size=3>Report Date: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0) { %>
      					<td class="STYLE5" width=50% align=center> 
      						<font face="Consolas" color=black size=2><%=rs_list.getString("initime") %></font>
      					</td>
      				<%} %>
      				<td class="STYLE5" align=center>
      					<font face="Consolas" color=black size=2><%=rs_query.getString("initime") %></font>
      				</td>
      			</tr>
      			<tr>
      				<td class="STYLE4" colspan=<%=colSpan %> align=center>
      					<b><font color=#4682B4 face=arial size=3>Update Date: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0) { %>
      					<td class="STYLE5" width=50% align=center> 
      						<font face="Consolas" color=black size=2><%=rs_list.getString("uptime") %></font>
      					</td>
      				<%} %>
      				<td class="STYLE5" align=center>
      					<font face="Consolas" color=black size=2><%=rs_query.getString("uptime") %></font>
      				</td>
      			</tr>
      			
      			<%
      			HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs = null;
      			ArrayList<String[]> terms = new ArrayList<String[]>();
      			String[] textPieces = null;

      			if(!rs_list.getString("des").equals("NA")) {
      				cfs = nlp.annotateCF(myDBBean, rs_list.getString("des"));
      				terms = nlp.getSortedCF(myDBBean, cfs);
      				textPieces = Tokenization.sbd(rs_list.getString("des"), false);	
      			}
      			%>
      			
      			<tr>
      				<td class="STYLE4" colspan=<%=colSpan %> align=center>
      					<b><font color=#4682B4 face=arial size=3>Description: </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0){ %>
      					<td class="STYLE5" width=50%>
      						<%if(rs_list.getString("des").equals("NA")) { %>   						
      							<font face="Consolas" color=black size=2>NA</font>
      						<%}else { %>
      							<table id="basic_<%=i%>">
									<tr><td>				
										<%for(int j=0; j<textPieces.length; j++) { %>
											<font face="Consolas" color=black size=2><%=textPieces[j] %></font>
										<%} %>					
									</td></tr>
								</table>
      							<%for(int t=0; t<terms.size(); t++) { %>	
									<table id="t<%=i%>_<%=t%>" style="display:none">
										<tr><td>				
											<%for(int j=0; j<textPieces.length; j++) { %>
												<%for(int k=0; k<textPieces[j].length(); k++) {%><font face="Consolas" color=black size=2><%if(cfs.containsKey(terms.get(t)[0])) {if(cfs.get(terms.get(t)[0]) != null && cfs.get(terms.get(t)[0]).containsKey(j)) {if(cfs.get(terms.get(t)[0]).get(j).contains(k)) {%></font><font face="Consolas" color=red size=2><%}}}%><%=textPieces[j].charAt(k)%></font><%}%>
											<%} %>					
										</td></tr>
									</table>	
								<%} %>
							<%} %>			
      					</td>
      				<%} %>
      				<td class="STYLE5">
      					<%if(rs_query.getString("des").equals("NA")) { %>   						
      						<font face="Consolas" color=black size=2>NA</font>
      					<%}else { %>
      						<table id="basic_q_<%=i%>">
								<tr><td>				
									<%for(int j=0; j<textPieces_q.length; j++) { %>
										<font face="Consolas" color=black size=2><%=textPieces_q[j] %></font>
									<%} %>					
								</td></tr>
							</table>
      						<%for(int t=0; t<terms_q.size(); t++) { %>	
								<table id="t_q<%=i%>_<%=t%>" style="display:none">
									<tr><td>				
										<%for(int j=0; j<textPieces_q.length; j++) { %>
											<%for(int k=0; k<textPieces_q[j].length(); k++) {%><font face="Consolas" color=black size=2><%if(cfs_q.containsKey(terms_q.get(t)[0])) {if(cfs_q.get(terms_q.get(t)[0]) != null && cfs_q.get(terms_q.get(t)[0]).containsKey(j)) {if(cfs_q.get(terms_q.get(t)[0]).get(j).contains(k)) {%></font><font face="Consolas" color=red size=2><%}}}%><%=textPieces_q[j].charAt(k)%></font><%}%>
										<%} %>					
									</td></tr>
								</table>	
							<%} %>
						<%} %>	
      				</td>
      			</tr>
      			
      			<tr>
      				<td class="STYLE4" colspan=<%=colSpan %> align=center>
      					<b><font color=brown face=arial size=3>Contributing Factors (CF): </font></b>
      				</td>
      			</tr>
      			<tr>
      				<td class="STYLE5" colspan=<%=colSpan %> align=center>
      					<b><font color=red face=arial size=2>(Click any CF below to highlight corresponding keywords in the DESCRIPTION above) </font></b>
      				</td>
      			</tr>
      			<tr>
      				<%if(i != 0){ %>
      					<td class="STYLE5" width=50%>
      						<%if(rs_list.getString("des").equals("NA")) { %>
      							<font face="Consolas" color=black size=2>NA</font>
      						<%} else {%>
      							<table id="terms_<%=i%>">
									<%for(int t=0; t<terms.size(); t++) { %>
										<tr title="Click to highlight the coressponding keywords in DESCRIPTION" onmouseover="this.className='STYLE1'" onmouseout="this.className='wrapper-style1'" onclick="showtable_cf_left('t<%=i%>_<%=t%>','<%=i %>',<%=terms.size()%>)" style="TEXT-DECORATION:none;cursor:pointer"> 				
											<td width=100><font face="Consolas" color=black size=2><%=terms.get(t)[0]%></font></td>
											<td><font face="Consolas" color=black size=2><%=terms.get(t)[1]%></font></td>				
										</tr>
									<%} %>								
								</table>
								<table id="simi_<%=i%>">
									<tr><td title="Apply this case as the query case" onmouseover="this.className='STYLE1'" onmouseout="this.className='wrapper-style1'" onclick="similarity_('<%=rs_list.getString("uni_id") %>','<%=i %>')" style="TEXT-DECORATION:none;cursor:pointer">
										<b><font color=brown face=arial size=2><i>Click to see cases with similar CF combinations.</i></font></b>
									</td></tr>
								</table>
								<div align=center><img src="images/loading.gif" style="display:none" id="loading_<%=i %>"></div>
      						<% } %>
      					</td>
      				<%} %>
      				<td class="STYLE5">
      					<%if(rs_query.getString("des").equals("NA")) { %>
      							<font face="Consolas" color=black size=2>NA</font>
      					<%} else {%>
      						<table id="terms_q_<%=i%>">
								<%for(int t=0; t<terms_q.size(); t++) { %>
									<tr title="Click to highlight the coressponding keywords in DESCRIPTION" onmouseover="this.className='STYLE1'" onmouseout="this.className='wrapper-style1'" onclick="showtable_cf_right('t_q<%=i%>_<%=t%>','<%=i %>',<%=terms_q.size()%>)" style="TEXT-DECORATION:none;cursor:pointer"> 				
										<td width=100><font face="Consolas" color=black size=2><%=terms_q.get(t)[0]%></font></td>
										<td><font face="Consolas" color=black size=2><%=terms_q.get(t)[1]%></font></td>				
									</tr>
								<%} %>								
							</table>
      					<% } %>
      				</td>
      			</tr>
      			<tr>
      				<td class="STYLE4" colspan=<%=colSpan %> align=center>
      					<b><font color=#4682B4 face=arial size=3>Link: </font></b>
      				</td>
      			<tr>
      			<tr>
      				<%if(i != 0){ %>
      					<td class="STYLE5" width=50%>
      						<font face="Consolas" color=black size=2><%=rs_list.getString("link") %></font>
      					</td>
      				<%} %>
      				<td class="STYLE5">
      					<font face="Consolas" color=black size=2><%=rs_query.getString("link") %></font>      					
      				</td>      				
      			</tr>
      			
      			<% //herf_2, severity, sir_7, sir_9, herf_7
      			String[] g_question = new String[5];
      			g_question[0] = "herf_2";
      			g_question[1] = "severity";
      			g_question[2] = "sir_7";
      			g_question[3] = "sir_9";
      			g_question[4] = "herf_7";
      			for(int k=0; k<g_question.length; k++) {
      				rs_q = myDBBean.query("select * from questions where qid='" + g_question[k] + "'");
					rs_q.next();					
					
					boolean userAnswered = true;
					boolean queryAnswered = true;
					
					if(rs_list.getString(g_question[k]) == null) {
						userAnswered = false;
					}
					if(rs_query.getString(g_question[k]) == null) {
						queryAnswered = false;
					}
					
					String[] ans_list = rs_q.getString("acontent").split("\\|\\|");
					String[] ans_user = null;
					String[] ans_query = null;
					
					if(userAnswered) {
						ans_user = rs_list.getString(g_question[k]).split("\\|\\|");
					}
					if(queryAnswered) {
						ans_query = rs_query.getString(g_question[k]).split("\\|\\|");
					}
				%>
					<tr>      				
      					<td class="STYLE4" colspan=<%=colSpan %> align=center> 
      						<b><font color=#4682B4 face=arial size=3><%= rs_q.getString("qcontent")%></font></b>
      						<font color=#4682B4 face=arial size=2><%= rs_q.getString("qref") %></font>
      					</td>      				
      				</tr>
      				<tr>      				
      					<%if(i != 0) {%>
      					<td class="STYLE5" width=50%>
      						<font face="Consolas" <%if(re.judgeDifference(rs_list.getString(g_question[k]), rs_query.getString(g_question[k]))){ %>color=red<%}else{ %>color=black<%} %> size=2>  						
      							<%if(!userAnswered) { %>
      								<i>Not answered</i>
      							<%}else {%>
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
      					<td class="STYLE5">
      						<font face="Consolas" color=black size=2>
      							<%if(!queryAnswered) { %>
      								<i>Not answered</i>
      							<%}else {%>					
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
      			
      			<%
      			//one of the nine categories
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
      						<td class="STYLE4" colspan=<%=colSpan %>> 
      							<b><font color=#4682B4 face=arial size=3><%=(k+1)%>. <%= rs_q.getString("qcontent")%></font></b>
      							<font color=#4682B4 face=arial size=2><%= rs_q.getString("qref") %></font>
      						</td>      				
      					</tr>
      					
      					<tr>      				
      						<%if(i != 0) { %>
      							<td class="STYLE5">
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
      						<td class="STYLE5">
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
      			
      			<tr><td class="STYLE5" align=center>      				
      				<form action="similarity_cf.jsp" method="post" name="simi"> 
      					<input type="hidden" value=<%=rs_list.getString("uni_id") %> id="queryID" name="queryID">	     					
      					<input type="submit" id="button<%=i %>" class="button button-small" value="Similar Cases to <%=rs_list.getString("uni_id") %>" id="con" onclick="similarity(this, '<%=i %>')">					
					</form>						
					<div align=center><img src="images/loading.gif" style="display:none" id="loading<%=i %>"></div>
				</td></tr>
      			
      		</table>      		
    	<%} %>  	
		<form method="post" id="status" action="view.jsp">
      		<input type="hidden" value="NA" id="type" name="type">
      		<input type="hidden" value="NA" id="uni" name="uni">
     	</form>
     	<form method="post" id="changeQuery" action="similarity_cf.jsp">
      		<input type="hidden" value="NA" id="queryID" name="queryID">
     	</form>
    </div>
   </div>
  </article>
 </div> 
 <footer>
 </footer>
<%rs_q.close(); %>  		
<%rs_list.close(); %> 	
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
