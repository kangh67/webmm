<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<jsp:useBean id="sw" scope="page" class="search.SearchWebMM"/>
<jsp:useBean id="sc" scope="page" class="mysql.SQLCommands"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String key = new String(request.getParameter("search"));
key = key.trim();
ArrayList<ArrayList<String[]>> allCaseAndPerspectiveInfo = new ArrayList<ArrayList<String[]>>();
ArrayList<ArrayList<String[]>> annotations = new ArrayList<ArrayList<String[]>>();

sc.getConn();

if(key.equals("")) {
	response.sendRedirect("index.html#search"); 
}else {
	System.out.println("keyword: " + key);	
	allCaseAndPerspectiveInfo = sw.getCaseID(key, sc);
	
	annotations = sw.getAnnotations(allCaseAndPerspectiveInfo.get(0), sc);
	//System.out.println(allCaseAndPerspectiveInfo.get(0).size());
	//System.out.println(allCaseAndPerspectiveInfo.get(1).size());
}

sc.disconnect();
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

<style type="text/css">
   	.STYLE1 {background-color:#FFDAC8}
   	.STYLE2 {background-color:#F0F0F0}
   	.STYLE3 {background-color:white}
   	a:link {color:#004B97}
   	a:visited {color:#004B97}
</style>    

<body>
<nav id="nav">
  <ul>
    <li><a href="index.html">Home</a></li>
    <li><a href="#search">Search</a></li>
    <li><a href="#report">Report</a></li>
    <li><a href="#contact">Contact</a></li>
  </ul>
</nav>

<div class="wrapper wrapper-style1 wrapper-first">
  <article class="5grid-layout" id="top">
    <div class="row">      
      <div class="4u">
      	<font color=black size=4 face="arial">Your Keyword: <b><font color=orange face="verdana"><%=key %></font></b></font><br />
      	<font color=black size=2 face="arial"><i>Click any case on the right, its annotations will be shown below.</i></font><br /><br />
      	
      	<script>
			function showwait()
    		{
      			var tableNum = <%=allCaseAndPerspectiveInfo.get(0).size()%>;      			
      			
      			for(var j=0; j<tableNum; j++)
      			{      				
      				document.getElementById("c" + j).style.display="none";      				
      			}
      			
      			document.getElementById("loadingside").style.display="block";
    		}      	
		</script>
      	
      	<%for(int i=0; i<annotations.size(); i++) { %>
      		<table id="c<%=i %>" style="display:none">
      			<%for(int j=0; j<annotations.get(i).size(); j++) { %>      				
      				<%if(annotations.get(i).get(j)[0].length() == 1) { %>
      					<tr><td><b><font size=3 color=brown face="arial"><%=annotations.get(i).get(j)[1] %></font></b></td></tr> 
      				<%}else if(annotations.get(i).get(j)[0].length() >= 3 && annotations.get(i).get(j)[0].length() <= 4){ %>
      					<tr><td><font size=3 color=black face="arial">&nbsp;&bull;&nbsp;<a href="search.jsp?search=<%=annotations.get(i).get(j)[1] %>" style="text-decoration:none" onclick="showwait();"><%=annotations.get(i).get(j)[1] %></a></font></td></tr>
      				<%}else if(annotations.get(i).get(j)[0].length() >= 5 && annotations.get(i).get(j)[0].length() <= 6){ %>
      					<tr><td><font size=2 color=black face="arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="search.jsp?search=<%=annotations.get(i).get(j)[1] %>" style="text-decoration:none" onclick="showwait();"><%=annotations.get(i).get(j)[1] %></a></font></td></tr>
      				<%}else { %>
      					<tr><td><font size=2 color=black face="arial">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="search.jsp?search=<%=annotations.get(i).get(j)[1] %>" style="text-decoration:none" onclick="showwait();"><%=annotations.get(i).get(j)[1] %></a></font></td></tr>
      				<%} %>
      			<%} %>      			  		
      		</table>
      	<%} %>
      	<div align=center><img src="images/loading.gif" style="display:none" id="loadingside"></div>
      </div>
      <div class="8u">      
      	<font size=4 color=brown face="verdana">CASES (<%=allCaseAndPerspectiveInfo.get(0).size() %>)</font>
      	<br />
      	<script>
			function showtable(id)
    		{
      			var tableNum = <%=allCaseAndPerspectiveInfo.get(0).size()%>;      			
      			
      			for(var j=0; j<tableNum; j++)
      			{      				
      				document.getElementById("c" + j).style.display="none";      				
      			}
      			
      			document.getElementById(id).style.display="block";
    		}      	
		</script>		
		
      	<table id="case" style="vertical-align:middle;" width=100%>
      		<tbody id="table2">
      		<%for(int i=0; i<allCaseAndPerspectiveInfo.get(0).size(); i++) {%>
      			<tr <%if(i%2==0) { %> bgcolor=#F0F0F0<%} %>>      				
      				<td> <div class="<%if(i%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>" onclick="showtable('c<%=i%>');" onmouseover="this.className='STYLE1'" onmouseout="this.className='<%if(i%2==0) { %>STYLE2<%}else{ %>STYLE3<%} %>'">     					
      					<b><font size=3 color=#004B97 face="arial"><%=allCaseAndPerspectiveInfo.get(0).get(i)[1]%></font></b><br/>
      					<font size=2 color="#4F4F4F" face="georgia"><%=allCaseAndPerspectiveInfo.get(0).get(i)[2]%>. By <%=allCaseAndPerspectiveInfo.get(0).get(i)[3] %></font>      					
      				</div></td>
      				<td width=70>
      					<br />   
      					<a href="https://psnet.ahrq.gov/webmm/case/<%=allCaseAndPerspectiveInfo.get(0).get(i)[0] %>" target="_blank"><img src="images/details.png" title="Find details in WebM&M" height=30></a>   					
      					<a href="similarity.jsp?id=<%=allCaseAndPerspectiveInfo.get(0).get(i)[0] %>"><img src="images/similarity.png" align=middle title="Find similar cases" height=30></a>
      				</td>
      			</tr>      			
      		<%} %>
      		</tbody>
      	</table>
      	<div align="right"><font size=4 color=#0066CC face="arial"><span id="spanFirst"><<</span>&nbsp;&nbsp;&nbsp;<span id="spanPre"><</span>&nbsp;&nbsp;&nbsp;<span id="spanNext">></span>&nbsp;&nbsp;&nbsp;<span id="spanLast">>></span>&nbsp;&nbsp;&nbsp;Page <span id="spanPageNum"></span> of <span id="spanTotalPage"></span></font></div>
      	
      	<br />
      	<font size=4 color=brown face="verdana">PERSPECTIVES (<%=allCaseAndPerspectiveInfo.get(1).size() %>)</font>
      	<br />      	
      	<table id="perspective" style="vertical-align:middle;" width=100%>
      		<%for(int i=0; i<allCaseAndPerspectiveInfo.get(1).size(); i++) {%>
      			<tr <%if(i%2==0) { %> bgcolor=#F0F0F0<%} %>>      				
      				<td>      					
      					<b><font size=3 color=#004B97 face="arial"><%=allCaseAndPerspectiveInfo.get(1).get(i)[1]%></font></b><br/>
      					<font size=2 color="#4F4F4F" face="georgia"><%=allCaseAndPerspectiveInfo.get(1).get(i)[2]%>. By <%=allCaseAndPerspectiveInfo.get(1).get(i)[3] %></font>     				
      				</td>
      				<td width=70>
      					<br />   
      					<a href="https://psnet.ahrq.gov/perspectives/perspective/<%=allCaseAndPerspectiveInfo.get(1).get(i)[0] %>" target="_blank"><img src="images/details.png" title="Find details in WebM&M" height=30></a>	
      				</td>
      			</tr>      			
      		<%} %>
      	</table>		
      	<br/><br/><br/><br/>
      
      </div>          
    </div>
  </article>
</div>

<div class="wrapper wrapper-style2">
  <article id="search">
    <header>
      <h2>Search from VISIT</h2>
      <span>Current version of VISIT contains <strong>323</strong> medical incident cases<br />integrated from ...</span> </header>
    <div class="5grid-layout">
      <div class="row">
        <div class="4u">
          <section class="box box-style1"> <span class="image image-centered"><img src="images/webmm.png" alt="" height=100></span>
            <h3>AHRQ WebM&M</h3>
            <p>Updated Feb 1st 2015.</p>
          </section>
        </div>
        <div class="4u">
          <section class="box box-style1"> <span class="image image-centered"><img src="images/cf.png" alt="" height=100></span>
            <h3>Common Formats</h3>
            <p>Coming Soon...</p>
          </section>
        </div>
        <div class="4u">
          <section class="box box-style1"> <span class="image image-centered"><img src="images/user.png" alt="" height=100></span>
            <h3>User Report</h3>
            <p>Coming Soon...</p>
          </section>
        </div>
      </div>
    </div>    
    <footer>          
      <form action="search.jsp" method="post" name="search">      		
      	<table cellpadding="0" cellspacing="0" border="0" width="100%">
 			<tr>
 				<td width=30%></td>
 				<td width=40%>     	
      			<input type="text" name="search" id="search" placeholder="Type Here..." style="color:black;background-color:white;border:lightgray 2px solid">
      			</td>
      			<td width=30%></td>
      		</tr>
      	</table>
      	<script>
      		function blocksubmit(o)
      		{      			
      			o.style.display="none";
      			document.getElementById("loading").style.display="block"; 
      			o.disabled=true;      			   			
      			o.form.submit();      			     			
      		}
      	</script>      
      	<input type="submit" class="button button-big" value="Search Your Case" id=sub onclick="blocksubmit(this);"> 
      	<div align=center><img src="images/loading.gif" style="display:none" id="loading"></div>
      	</form>
     </footer>     
  </article>
</div>

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
    			<td height=30></td>
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

<div class="wrapper wrapper-style4">
  <article id="contact">
    <header>
      <h2>Any Question or Comment?</h2>
      <span>Contact us!</span> </header>
    <div class="5grid">
      <div class="row">
        <div class="12u">
          <form method="post" action="#">
            <div class="5grid">
              <div class="row">
                <div class="6u">
                  <input type="text" name="name" id="name" placeholder="Name">
                </div>
                <div class="6u">
                  <input type="text" name="email" id="email" placeholder="Email">
                </div>
              </div>
              <div class="row">
                <div class="12u">
                  <input type="text" name="subject" id="subject" placeholder="Subject">
                </div>
              </div>
              <div class="row">
                <div class="12u">
                  <textarea name="message" id="message" placeholder="Message"></textarea>
                </div>
              </div>
              <div class="row">
                <div class="12u">
                  <input type="submit" class="button" value="Send Message">
                  <input type="reset" class="button button-alt" value="Clear Form">
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
      <div class="row row-special">
        <div class="12u">
          <h3>Find us on ...</h3>
          <ul class="social">
            <li class="facebook"><a href="#">Facebook</a></li>
            <li class="twitter"><a href="#">Twitter</a></li>
            <li class="dribbble"><a href="#">Dribbble</a></li>
            <li class="linkedin"><a href="#">LinkedIn</a></li>
            <li class="tumblr"><a href="#">Tumblr</a></li>
            <li class="googleplus"><a href="#">Google+</a></li>
          </ul>
        </div>
      </div>
    </div>
    <footer>
      <p id="copyright"> &copy; School of Biomedical Informatics, University of Texas Health Science Center at Houston 2015</p>
    </footer>
  </article>
</div>
</body>
</html>
<%@ include file="fenye.jsp" %>	
