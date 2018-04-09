<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>
<%@ page import="user.DBBean"%>
<%@ page import="user.UserBean"%>
<%@ page import="report.NewCase"%>
<%@ page import="report.ContinueAndEdit"%>

<jsp:useBean id="myDBBean" scope="page" class="user.DBBean"/>
<jsp:useBean id="user" scope="session" class="user.UserBean"/>
<jsp:useBean id="answers" scope="session" class="report.NewCase"/>
<jsp:useBean id="cae" scope="page" class="report.ContinueAndEdit"/>
<jsp:setProperty name="user" property="*" />
<jsp:setProperty name="answers" property="*" />

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String userName = user.getUserName();
String userPWD = user.getUserPWD();

String sql = "select * from users where user_id='" + userName + "' and passwords='" + userPWD + "'";

ResultSet rs = myDBBean.query(sql);
if(rs.next()) {
	rs.close();
	String type = new String(request.getParameter("type"));
	String uni_id = new String(request.getParameter("uni"));	
	
	sql = "select * from report_general where uni_id='" + uni_id + "'";
	ResultSet rs_report = myDBBean.query(sql);
	
	if(rs_report.next()) {
		//----basic info
		answers.uni_id = uni_id;
		answers.reporter = rs_report.getString("reporter");
		answers.caseName = rs_report.getString("rname");
		answers.initime = rs_report.getString("initime");
		answers.uptime = rs_report.getString("uptime");
		answers.description = rs_report.getString("des");
		answers.link = rs_report.getString("link");
		//------
		
		
		//------general
		ArrayList<String> general = cae.getGeneralQuestions(myDBBean);
		//be sure general form has been finished
		if(!rs_report.getString("rstatus").equals("0"))	{
			answers.createRouteAndInfo("general");			
			for(int i=0; i<general.size(); i++) {
				if(rs_report.getString(general.get(i)) != null) {					
					if(!rs_report.getString(general.get(i)).equals("NA"))
						answers.putRouteAndInfo("general", general.get(i), rs_report.getString(general.get(i)));
				}
			}	
			if(answers.route_gen.size() >= 1) {
				answers.general = true;
			}
			String ans = rs_report.getString("herf_7");
			answers.setForms(ans.split("\\|\\|"));
		}		
		
		//System.out.println("!!!" + answers.route_gen);
		//System.out.println("!!!" + answers.info_gen);
		//System.out.println("!!!" + answers.general);		
		//--------
		
		
		//-----other info
		String forms = rs_report.getString("formn");
		if(forms != null) {
			String[] eachform = forms.split("\\|\\|");
			for(int i=0; i<eachform.length; i++) {
				sql = "select * from report_" + eachform[i] + " where uni_id='" + rs_report.getString("uni_id") + "'";
				ResultSet rs_detail = myDBBean.query(sql);
				
				if(rs_detail.next()) {
					ArrayList<String> detail = cae.getDetailQuestions(myDBBean, eachform[i]);
					answers.createRouteAndInfo(eachform[i]);	
					for(int j=0; j<detail.size(); j++) {
						if(rs_detail.getString(detail.get(j)) != null) {
							answers.putRouteAndInfo(eachform[i], detail.get(j), rs_detail.getString(detail.get(j)));
						}
					}												
				}
				
				rs_detail.close();
			}
		}	
		//------
		
	}else {  //no such record in database
		rs_report.close();
		response.sendRedirect("login.jsp");
	}	
	//-----
	
	
	if(type.equals("view")) {
		answers.review = true;
		response.sendRedirect("review.jsp");
	}else if (type.equals("continue")) {
		answers.review = false;
		if(rs_report.getString("rstatus").equals("0")) {
			answers.general = true;
			response.sendRedirect("report_general.jsp");
		}else if(rs_report.getString("rstatus").equals("1")) {
			response.sendRedirect("report_detail.jsp?page_type=NA&route=" + answers.getContinuePosition());
		}
	}
	
	rs_report.close();
}
rs.close();
myDBBean.disConnect();

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>



</html>
