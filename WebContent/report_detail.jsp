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
    function conti(allName)
	{   	
    	var names = allName.split(";");
    	var route = "";
    	
    	var allPass = true;
    	for(var i=0; i<names.length; i++)
    	{
    		if(document.getElementById(names[i]).style.display == "block")
    		{
    			route = route + names[i] + ";";
    			var obj = document.getElementsByName(names[i] + "_ans");
    			var selected = false;
    			for(var j=0; j<obj.length; j++) 
        		{
            		if(obj[j].checked) 
            		{
            			selected = true;            
  					}  				          
        		}
        		
        		if(!selected)
        		{
        			allPass = false;
        		}
    		}
    	}    	
        
        if(!allPass) {    	
    		alert("In order to continue, pleaes make response to all the questions.");
    	}else
    	{
    		document.getElementById("route").value=route;
    		document.forms["info"].submit();
    	}
    	  
    	
    }
</script>

<script>
	function shownext(shown, thisq, allNum, other)
	{		
		if(other == true)
		{
			document.getElementById(thisq + "_o").style.display = "inline";					
		}		
		
		var formTitle = thisq.split("_")[0];
		var start = Number(thisq.split("_")[1]) + 1;		
	
		for(var i=start; i<allNum+1; i++)
		{
			var thisform = document.getElementById(formTitle + "_" + i);
			if(thisform != null)
			{
				thisform.style.display = "none";			
			}
		}
		
		var needToBeShown = shown.split(";");
		for(var j=0; j<needToBeShown.length; j++)
		{
			var obj=document.getElementById(needToBeShown[j]);
			if(obj != null)
			{			
				obj.style.display="block";			
			}
		}
	}
</script>

<script>
	function jumpHistory(thisp, jumpp)
	{
		document.getElementById("route").value="NA";
		
	}
</script>

<% 
ResultSet rs = myDBBean.query(sql);
if(rs.next()) {
	String lastpage = new String(request.getParameter("page_type"));
	System.out.println("[report_detail.jsp] Former page is: " + lastpage);
	
	String lastRoute = new String(request.getParameter("route"));
	System.out.println("[report_detail.jsp] Former page route: " + lastRoute);
	
	String[] lastRouteList = lastRoute.split(";");	
	String sql_q = "";	
	ResultSet rs_q;
	
	if(!lastpage.equals("NA")) {
		answers.iniCertainForm(lastpage);
		for(int i=0; i<lastRouteList.length; i++) {
			sql_q = "select * from questions where qid ='" + lastRouteList[i] + "'";	
			rs_q = myDBBean.query(sql_q);
			rs_q.next();
		
			if(rs_q.getString("oindex").equals("NA")) {
				answers.saveInfo(lastpage, lastRouteList[i], request.getParameterValues(lastRouteList[i] + "_ans"));
			}else {
				String otherinfo = new String(request.getParameter(lastRouteList[i] + "_o"));			
				answers.saveInfo_other(lastpage, lastRouteList[i], request.getParameterValues(lastRouteList[i] + "_ans"), rs_q.getString("oindex"), otherinfo);
			}
		}
		System.out.println("[report_detail.jsp] Set " + lastpage + " to " + answers.general);
		System.out.println("[report_detail.jsp] Saved the route of " + lastpage + " to the session");
		System.out.println("[report_detail.jsp] Saved the info of " + lastpage + " to the session");		
	
		if(lastpage.equals("general")) {
			String[] ans = request.getParameterValues("herf_7_ans");		
			answers.setForms(ans);
			
			answers.caseName = new String(request.getParameter("caseName"));
			answers.description = new String(request.getParameter("des"));
			answers.link = new String(request.getParameter("link"));
			
			System.out.println("[report_detail.jsp] Forms should be completed: " + answers.showAvaForms());	
		}		
		
		re.case_update(myDBBean, answers, lastpage);
	}
	
	String thispage = "";
	
	if(lastpage.equals("NA")) {
		thispage = lastRoute;
	}else {
		thispage = answers.getThisPage(lastpage);
	}
	System.out.println("This page: " + thispage);
	
	if(thispage.equals("done")) {		
		rs.close();
		response.sendRedirect("review.jsp");
	}else{	

	sql_q = "select * from questions where qid like '%" + thispage + "%'";	
	rs_q = myDBBean.query(sql_q);
	
	int ansNum = 1;
	String allName = "";
	rs_q.next();
	allName = rs_q.getString("qid");
	
	while(rs_q.next()) {
		allName = allName + ";" + rs_q.getString("qid");
		ansNum ++;
	}
	
	System.out.println("Potential questions: " + allName);
	
	rs_q.beforeFirst();
	%>
		
<div class="wrapper wrapper-style2">
  <article id="report">
    <header>      
    
    	<table>
    		<tr>
    			<td width=25%>
    			</td>
    			<td width=1000 align=center>
    				<div style="margin-left:<%=(285 - 76 * answers.showAvaForms().size()) %>">
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
    						if(thisRoute != null || thispage.equals(activatedForms.get(i))) {%>
    							<a href="report_detail.jsp?page_type=NA&route=<%=activatedForms.get(i) %>"><img src="images/step<%=stepNum %>_on.png" style="display:inline;float:left" height=50 title="<%=thisTitle %>"></a>
    						<%}else {%>
    							<img src="images/step<%=stepNum %>_off.png" style="display:inline;float:left" height=50 title="<%=thisTitle %>">
    						<%} %>
    					<%} %>
    				<%} %>
    				
    				<%if(answers.whetherReviewable()) {%>
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
    
      	<h2 id="q_type">Questions about <font color=brown><%=answers.getFormRealName(thispage) %></font></h2>
      	( <font color=darkorange face=arial><%=answers.getMaxQues(thispage) %></font> questions for maximum )
      <br />        
      
      <form name="info" id="info" method="post" action="report_detail.jsp" onSubmit="return check()">      
      	<center>
      	<%
      	int qNum = 0;      
      	while(rs_q.next()) {       	
      		qNum ++;      		
      		String inputtype = rs_q.getString("qtype").replaceAll("single", "radio").replaceAll("multi", "checkbox");      		
      		String[] acon = rs_q.getString("acontent").split("\\|\\|");
      		String[] aref = null;  		
      		if(!rs_q.getString("aref").equals("NA")) {
      			aref = rs_q.getString("aref").split("\\|\\|");
      		}
      		
      		String style = "";
      		if(qNum == 1 || answers.inHistory(rs_q.getString("qid"))) {
      			style = "block";
      		}else{
      			style = "none";
      		}     		
      		
      		String[] nextq = rs_q.getString("nextq").split("\\|\\|");
      		
      		String historyAnswer = answers.getAnswerOfCertainQuestion(thispage, rs_q.getString("qid"));  
      		if(historyAnswer == null) {
      			historyAnswer = "NA";
      		}    		
      		String[] ha = historyAnswer.split("\\|\\|");
      		%>
      		
      		
      		<table id=<%=rs_q.getString("qid") %> style="display:<%=style %>" border="1">      			
      			<tr>
      				<td width=25%></td>
      				<td width=1000> 
      					<b><font color=#4682B4 face=arial size=4><%= rs_q.getString("qcontent")%></font></b>
      					<font color=#4682B4 face=arial size=3><%= rs_q.getString("qref") %></font>
      				</td>
      				<td width=25%></td>
      			</tr>
      			
      			<tr>
      				<td width=25%></td>
      				<td width=50%>
      					<font face="Consolas" color=black size=3>  						
      					<%for(int j=0; j<rs_q.getInt("anum"); j++) {
      						boolean checked = false;
      						for(int k=0; k<ha.length; k++) {
      							if(ha[k].equals(String.valueOf(j)) || ha[k].contains(String.valueOf(j) + "$$")) {
      								checked = true;
      							}
      						}%>     
      						<%if(checked) { %>	
      							<input type=<%=inputtype %> name="<%=rs_q.getString("qid") %>_ans" id="<%=rs_q.getString("qid") %>_ans" value = <%=j %> checked="true" onclick="shownext('<%=answers.getShown(nextq[j], answers.getRoute(thispage)) %>','<%=rs_q.getString("qid") %>','<%=ansNum %>',<%=rs_q.getString("oindex").equals(String.valueOf(j)) %>)"><b><%=acon[j] %></b>
      						<%}else { %>				
      							<input type=<%=inputtype %> name="<%=rs_q.getString("qid") %>_ans" id="<%=rs_q.getString("qid") %>_ans" value = <%=j %> onclick="shownext('<%=answers.getShown(nextq[j], answers.getRoute(thispage)) %>','<%=rs_q.getString("qid") %>','<%=ansNum %>',<%=rs_q.getString("oindex").equals(String.valueOf(j)) %>)"><b><%=acon[j] %></b>
      						<%} %>
      						<%if(aref != null) {
      							if(!aref[j].equals("NA")) {
      								%><font face=roma>: <%=aref[j]%></font><%
      							}
      						}%>.
      						<%if(rs_q.getString("oindex").equals(String.valueOf(j))) { 
      							String content = "NA";
      							for(int k=0; k<ha.length; k++) {
      								if(ha[k].contains(String.valueOf(j) + "$$")) {
      									String[] thiscontent = ha[k].split("\\$\\$");
      									if(thiscontent.length > 1){
      										content = thiscontent[1];
      									}
      								}
      							}
      							
      							ArrayList<String> answerList = re.getAnswerListOfOthers(myDBBean, rs_q.getString("qid"));
      							%>
      							
      							<b><font color=#4682B4 face=arial size=3>PLEASE SPECIFY</font></b>
      							
      							<%if(content.equals("NA")) {%>
      							<SELECT name="SpecificSelect" style="color:black;background-color:white;border:lightgray 2px solid;WIDTH: 320px; CLIP: rect(0px auto auto 300px);POSITION:absolute" onchange="document.getElementById('<%=rs_q.getString("qid") %>_o').value=this.options[this.selectedIndex].value">
                					<%for(int i=0; i<answerList.size(); i++) { %>
                						<OPTION value="<%=answerList.get(i)%>"><%=answerList.get(i)%></OPTION>
                					<%} %>
           	 					</SELECT>
           	 					<input type=text id="<%=rs_q.getString("qid") %>_o" name="<%=rs_q.getString("qid") %>_o" style="color:black;background-color:white;border:lightgray 2px solid;WIDTH: 301px;height:47px;POSITION:absolute">
           	 					<%}else { %>
           	 					<SELECT name="SpecificSelect" style="color:black;background-color:white;border:lightgray 2px solid;WIDTH: 320px; CLIP: rect(0px auto auto 300px);POSITION:absolute" onchange="document.getElementById('<%=rs_q.getString("qid") %>_o').value=this.options[this.selectedIndex].value">
                					<OPTION value="<%=content %>" selected><%=content %></OPTION>
                					<%for(int i=0; i<answerList.size(); i++) { %>
                						<OPTION value="<%=answerList.get(i)%>"><%=answerList.get(i)%></OPTION>
                					<%} %>
           	 					</SELECT>
           	 					<input type=text id="<%=rs_q.getString("qid") %>_o" name="<%=rs_q.getString("qid") %>_o" style="color:black;background-color:white;border:lightgray 2px solid;WIDTH: 301px;height:47px;POSITION:absolute" value="<%=content %>">
           	 					<%} %>
           	 					<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#4682B4 face=arial size=3>(Either type or select)</font>						     							
      										
      						<%} %>
      						<br />							   						
      					<%} %>
      					</font>
      				</td>
      				<td width=25%></td>
      			</tr>      			
      		</table>      		
      	<% }%>
      	
      	
      	    						
      	<input type="button" class="button button-small" value="Save & Continue" id="con" onclick="conti('<%=allName %>')">
      					
		
		</center>      
		<input type="hidden" value="NA" id="route" name="route"> 
		<input type="hidden" value=<%=thispage %> name="page_type">
      </form>    
    
    <footer>
    </footer>
  </article>
</div>

<%
	rs_q.close();
	}
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
