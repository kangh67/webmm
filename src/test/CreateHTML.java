//generate html file for clinician to review
package test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import mysql.SQLCommands;



public class CreateHTML {
	//assign term
	public static String term = "1";
	
	//assign method
	public static String method = "vs";
	
	//assign type
	public static String type = "1";
	
	//HTML file 
	public static String HTMLFile = "C:\\Users\\hkang1\\Desktop\\AHRQ\\WEBMM\\test\\";
	
	//Title
	public static String title = "";
	
	//specific method
	public static String speMethod = "";
	
	//specific type
	public static String speType = "";
		
	public static void main(String[] args) throws Exception {
		//get caseIDs, edit HTMLFile, title, specific method and specific type
		ArrayList<String> caseID = getCaseID();
		
		//get scoreList for every query case in caseID
		ArrayList<ArrayList<SingleCase>> scoreList = getScoreListForEveryCase(caseID);
		
		//write HTML file
		writeHTML(caseID, scoreList);
		
	}
	
	public static ArrayList<String> getCaseID() throws Exception {
		if(method.equals("vs"))
			speMethod = "Vector Space";
		else if(method.equals("ic"))
			speMethod = "Information Content";
		else
			speMethod = "Term Overlap";
		
		if(type.equals("1"))
			speType = "Safety Target";
		else if(type.equals("2"))
			speType = "Error Type";
		else if(type.equals("3"))
			speType = "Approach to Improving Safety";
		else if(type.equals("4"))
			speType = "Clinical Area";
		else if(type.equals("5"))
			speType = "Target Audience";
		else
			speType = "Setting of Care";		
		
		
		SQLCommands sc = new SQLCommands();
		ResultSet rs = sc.getTreeByCate(term);
		rs.next();
		
		HTMLFile += rs.getString("name") + "_" + type + ".html";
		title = rs.getString("name");
		String[] caseID = rs.getString("caseID").split(";");
		
		rs.close();
		sc.disconnect();
		
		ArrayList<String> al = new ArrayList<String>();
		
		for(int i=0; i<caseID.length; i++) 
			al.add(caseID[i]);
		
		return al;
	}
	
	public static ArrayList<ArrayList<SingleCase>> getScoreListForEveryCase(ArrayList<String> caseID) throws Exception {
		ArrayList<ArrayList<SingleCase>> scoreList = new ArrayList<ArrayList<SingleCase>>();
		SQLCommands sc = new SQLCommands();
		
		for(int i=0; i<caseID.size(); i++) {
			ResultSet rs = sc.getCaseSimilarity(caseID.get(i), method, type);
			ArrayList<SingleCase> thisCase = new ArrayList<SingleCase>();
			rs.next();
			
			for(int j=0; j<caseID.size(); j++)
				if(i != j)
					thisCase.add(new SingleCase(caseID.get(j), keepNdigit(Double.valueOf(rs.getString("N" + caseID.get(j))), 4)));
				
			Collections.sort(thisCase);
			scoreList.add(thisCase);
			
			rs.close();
			sc.disconnect();
		}		
		
		return scoreList;
	}
	
	public static void writeHTML(ArrayList<String> caseID, ArrayList<ArrayList<SingleCase>> scoreList) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(HTMLFile)));
		
		dos.writeBytes("<html>" + "\r\n");
		dos.writeBytes("\t" + "<head></head>" + "\r\n");
		dos.writeBytes("\t" + "<body>" + "\r\n");
		dos.writeBytes("\t" + "\t" + "<script language=javascript>" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "function sh(id)" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "{" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "\t" + "a=document.getElementsByTagName(\"table\");" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "\t" + "for(i=0; i<a.length; i++)" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "\t" + "{" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "\t" + "\t" + "a[i].style.display=\"none\";" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "\t" + "}" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "\t" + "a[document.getElementById(\"select\").selectedIndex-1].style.display=\"block\"" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "}" + "\r\n");
		dos.writeBytes("\t" + "\t" + "</script>" + "\r\n");
		
		dos.writeBytes("\t" + "\t" + "<p align=center><b><font size=5>Similarity Analysis for <font color=red>" + title + " </font>Cases</font></b></p>" + "\r\n");
		dos.writeBytes("\t" + "\t" + "<p align=center><font size=3>Perspective: <font color=red>" + speType + " </font></font></p>" + "\r\n");
		dos.writeBytes("\t" + "\t" + "<p align=center><font size=3>Method: <font color=red>" + speMethod + " </font></font></p>" + "\r\n");
		dos.writeBytes("\t" + "\t" + "</br>" + "\r\n");
		dos.writeBytes("\t" + "\t" + "<p align=center><b>" + caseID.size() + "</b> cases have <b>" + title + "</b> label. Please choose one as a query: " + "\r\n");
		dos.writeBytes("\t" + "\t" + "<select id=\"select\" onchange=\"sh()\">" + "\r\n");
		dos.writeBytes("\t" + "\t" + "\t" + "<option selected=\"true\">Please Select...</option>" + "\r\n");
		for(int i=0; i<caseID.size(); i++)
			dos.writeBytes("\t" + "\t" + "\t" + "<option value=" + (i+1) + ">" + "case " + caseID.get(i) + "</option>\r\n");	
		dos.writeBytes("\t" + "\t" + "</select></p>" + "\r\n");
		
		dos.writeBytes("\t" + "\t" + "<p align=center>" + "\r\n");
		for(int i=0; i<caseID.size(); i++) {
			dos.writeBytes("\t" + "\t" + "<table style=\"display:none\" id=" + (i+1) + " border=1 width=500>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "<tr>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<th align=left colspan=3>Current Query: <a href=\"http://webmm.ahrq.gov/case.aspx?caseID=" + caseID.get(i) + "\" target=\"_blank\">case " + caseID.get(i) + "</a></th>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "</tr>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "<tr>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<th align=center bgcolor=\"lightblue\">Rank</th>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<th align=center bgcolor=\"lightblue\">Case Num</th>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<th align=center bgcolor=\"lightblue\">Score</th>" + "\r\n");
			dos.writeBytes("\t" + "\t" + "\t" + "</tr>" + "\r\n");
			for(int j=0; j<caseID.size()-1; j++) {
				dos.writeBytes("\t" + "\t" + "\t" + "<tr>" + "\r\n");
				dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<td align=center>" + (j+1) + "</td>" + "\r\n");
				dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<td align=center><a href=\"http://webmm.ahrq.gov/case.aspx?caseID=" + scoreList.get(i).get(j).name + "\" target=\"_blank\">case " + scoreList.get(i).get(j).name + "</a></td>" + "\r\n");
				dos.writeBytes("\t" + "\t" + "\t" + "\t" + "<td align=center>" + scoreList.get(i).get(j).score + "</td>" + "\r\n");
				dos.writeBytes("\t" + "\t" + "\t" + "</tr>" + "\r\n");
			}
			dos.writeBytes("\t" + "\t" + "</table>" + "\r\n");
		}
		dos.writeBytes("\t" + "\t" + "</p>" + "\r\n");
		dos.writeBytes("\t" + "</body>" + "\r\n");
		dos.writeBytes("</html>");
		
		dos.close();
	}
	
	public static Double keepNdigit(double data, int scope) {
		double tempDouble = Math.pow(10, scope);
		data = data * tempDouble;
		int tempint = (int)(data);
		return tempint / tempDouble;
	}
}
