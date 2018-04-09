<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="nlp.Tokenization"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="to" scope="page" class="nlp.Tokenization"/>

<%
String text = request.getParameter("des");

HashMap<String, HashSet<String>> cfs = null;
ArrayList<String[]> terms = null;
String[][] textPieces = null;

if(text != null) {
	cfs = to.annotateCF(myDBBean, text);
	terms = to.getSortedCF(myDBBean, cfs);
	textPieces = to.simpleTokenizeParagraph(text);
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Annotation Tool</title>
</head>

<%if(text != null) { %>
<script>
	function showtable(id)
    {
    	var tableNum = <%=terms.size()%>;      			
      	for(var j=0; j<tableNum; j++)
      	{      				
      		document.getElementById("t" + j).style.display="none";      				
      	}
      			
      	document.getElementById(id).style.display="block";
    }      	
</script>
<%} %>


<body>
	<form name="info" id="info" method="post" action="testAnnotation.jsp">
		<textarea id="des" name="des" style="color:black;background-color:white;border:lightgray 2px solid;width:800px;height:200px;"></textarea>
		<input value='Annotate' type='submit'>
	</form>
	
	<%if(text != null) { %>
	
	<table id=terms width=800>
		<%for(int i=0; i<terms.size(); i++) { %>
			<tr> 				
				<td> <a href="javascript:void(0)" onclick="showtable('t<%=i%>')"><%=terms.get(i)[0]%></a></td>
				<td><%=terms.get(i)[1]%></td>				
			</tr>
		<%} %>
	</table>
	
	<%for(int i=0; i<terms.size(); i++) { %>
	
		<table id=t<%=i %> width=800 style="display:none">
			<tr><td>
				<%for(int j=0; j<textPieces.length; j++) { %>					
					<%for(int k=0; k<textPieces[j].length; k++) {%>
						<%if(cfs.get(terms.get(i)[0]) == null) {%>
							<font color=black><%=textPieces[j][k]%></font>&nbsp;
						<%}else if(cfs.get(terms.get(i)[0]).contains(String.valueOf(j) + "_" + String.valueOf(k))) { %>
							<span style="background:yellow;"><font color=red><%=textPieces[j][k]%></font></span>&nbsp;
						<%}else { %>
							<font color=black><%=textPieces[j][k]%></font>&nbsp;
						<%} %>						
					<%} %>					
				<%} %>
			</td></tr>
		</table>
	
	<%} %>
	
	<%} %>
	<%myDBBean.disConnect(); %>
</body>
</html>