/*
 * getCaseID() the search engine. based on the WebMM searching
 */
package search;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mysql.SQLCommands;

import url.CatchInf;

public class SearchWebMM {
	public static void main(String[] args) throws Exception {
		SQLCommands sc = new SQLCommands();
		sc.getConn();
		SearchWebMM sw = new SearchWebMM();
		
		//---test getCaseID
		
		ArrayList<ArrayList<String[]>> allInfo = sw.getCaseID("Active Errors", sc);
		for(int i=0; i<allInfo.get(0).size(); i++){
			System.out.print(allInfo.get(0).get(i)[0] + "\t");
		}
		
		//------
		
		
		//---test getAnnotations
		/**
		ArrayList<ArrayList<String[]>> annotations = sw.getAnnotations(sw.getCaseID("infection").get(0));
		System.out.println(annotations.size());
		**/
		//------
		
		sc.disconnect();
	}
	
	//Given a keyword, return the searching list concluding caseID(0) and perspectiveID(1)
	//0:case/perspective ID, 1:title, 2:date, 3:author
	public ArrayList<ArrayList<String[]>> getCaseID(String keyword, SQLCommands sc) throws Exception {
		ArrayList<ArrayList<String>> caseAndPerspective = new ArrayList<ArrayList<String>>();
		ArrayList<String> caseIDList = new ArrayList<String>();
		
		//-----test whether keyword is a direct caaeID
		/*** The website of WebM&M changed
		String directResult = getDirectResult(keyword);		
		
		if(!directResult.equals("NA"))
			caseIDList.add(directResult);
		***/
		//-----
		
		//if keyword is the number of annotation (like 1_1_1), add all the cases with this annotation to the list
		//caseIDList = getTermSearchForCase(keyword, caseIDList);
		
		//if keyword is the annotation name (like "patient fall"), add these cases to the list
		caseIDList = getAnnotationNameSearch(keyword, caseIDList, sc);				
		
		//get caseIDList(0) and perspectiveIDList(1) from WebMM searching
		caseAndPerspective = getTop20CaseID(keyword, caseIDList);		
		
		
		/**waste too much time, now changed to search from case and perspective list in MySQL (as below)
		//get the ID, title, date, author for every case in the searching list
		ArrayList<String[]> allCaseInfo = getAllCaseInfo(caseAndPerspective.get(0));
		
		//get the ID, title, date, author for every perspective in the searching list
		ArrayList<String[]> allPerspectiveInfo = getAllPerspectiveInfo(caseAndPerspective.get(1));
		**/
		
		//get the ID, title, date, author for every case in the searching list
		ArrayList<String[]> allCaseInfo = getAllCaseInfo_db(caseAndPerspective.get(0), sc);
		
		//get the ID, title, date, author for every perspective in the searching list
		ArrayList<String[]> allPerspectiveInfo = getAllPerspectiveInfo_db(caseAndPerspective.get(1), sc);
		
		//---save the information of cases at 0, perspectives at 1
		ArrayList<ArrayList<String[]>> allCaseAndPerspectiveInfo = new ArrayList<ArrayList<String[]>>();
		allCaseAndPerspectiveInfo.add(allCaseInfo);
		allCaseAndPerspectiveInfo.add(allPerspectiveInfo);
		//----
		
		/*
		//----test the results of case part
		for(int i=0; i<allCaseAndPerspectiveInfo.get(0).size(); i++) {
			System.out.println(allCaseAndPerspectiveInfo.get(0).get(i)[0]);
			System.out.println(allCaseAndPerspectiveInfo.get(0).get(i)[1]);
			System.out.println(allCaseAndPerspectiveInfo.get(0).get(i)[2]);
			System.out.println(allCaseAndPerspectiveInfo.get(0).get(i)[3]);
			System.out.println();
		}
		//---
		
		//----test the results of case part
		for(int i=0; i<allCaseAndPerspectiveInfo.get(1).size(); i++) {
			System.out.println(allCaseAndPerspectiveInfo.get(1).get(i)[0]);
			System.out.println(allCaseAndPerspectiveInfo.get(1).get(i)[1]);
			System.out.println(allCaseAndPerspectiveInfo.get(1).get(i)[2]);
			System.out.println(allCaseAndPerspectiveInfo.get(1).get(i)[3]);
			System.out.println();
		}
		//---
		*/
		
		return allCaseAndPerspectiveInfo;
	}
	
	public static String getDirectResult(String keyword) throws IOException {
		String url = "http://webmm.ahrq.gov/case.aspx?caseID=" + keyword;		
		String content = "";
		
		try {
			content = CatchInf.getHtml(url);
		}catch(Exception e) {
			return "NA";
		}
		
		//---if the keyword is a number, whether is it available in WebMM
		Pattern p = Pattern.compile("LabelCaseDate\"></span>");
		Matcher m;
		
		m = p.matcher(content);
		
		if(m.find())
			return "NA";
		//-----
		
		//---if the keyword is a word, the return should also be NA
		p = Pattern.compile("caseID=-1");
		m = p.matcher(content);
		
		if(m.find())
			return "NA";
		//---
		
		//---else, the keyword must be an available caseID
		else
			return keyword;
		//---
	}
	
	public static ArrayList<String> getTermSearchForCase(String keyword, ArrayList<String> caseIDList, SQLCommands sc) throws Exception {
		ResultSet rs = sc.getResultWithTheAnnotation(keyword);
		
		if(rs == null)
			return caseIDList;
		
		while(rs.next()) {
			String caseID = rs.getString("caseID");
			if(!caseIDList.contains(caseID))
				caseIDList.add(caseID);
		}
		
		rs.close();
		return caseIDList;
	}
	
	public static ArrayList<String> getAnnotationNameSearch(String keyword, ArrayList<String> caseIDList, SQLCommands sc) throws Exception {
		ResultSet rs = sc.getAnnotationWithName(keyword);
		
		while(rs.next()) {
			String[] cases = rs.getString("caseID").split(";");
			for(int i=0; i<cases.length; i++) 
				if(!caseIDList.contains("cases[i]"))
					caseIDList.add(cases[i]);			
		}
		
		rs.close();
		
		return caseIDList;
	}
	
	public static ArrayList<ArrayList<String>> getTop20CaseID(String keyword, ArrayList<String> caseIDList) {
		String url = "https://psnet.ahrq.gov/search?Site2Search=PSNet&q=" + keyword.replaceAll("\\s+", "+") + "&f_resource_typeID=7";
		
		String content = "";
		ArrayList<ArrayList<String>> caseAndPerspective= new ArrayList<ArrayList<String>>();
		ArrayList<String> perspective = new ArrayList<String>();
		
		try {
			content = CatchInf.getHtml(url);			
		}catch(Exception e) {			
			System.out.println("Exception when searching cases with the keyword=" + keyword);
			perspective = null;
			caseAndPerspective.add(caseIDList);
			caseAndPerspective.add(perspective);
			return caseAndPerspective;
		}		
		
		//match cases
		Pattern p = Pattern.compile("href=\"/webmm/case/([0-9]+)/");		
		Matcher m;
		
		m = p.matcher(content);		

		while(true) {
			if(m.find()) {
				if(!caseIDList.contains(m.group(1)))
					caseIDList.add(m.group(1));
			}else
				break;
		}
		
		
		url = "https://psnet.ahrq.gov/search?Site2Search=PSNet&q=" + keyword.replaceAll("\\s+", "+") + "&f_resource_typeID=8";
		
		try {
			content = CatchInf.getHtml(url);			
		}catch (Exception e){
			System.out.println("Exception when searching perspectives with the keyword=" + keyword);
		}
		
		//match perspectives
		p = Pattern.compile("href=\"/perspectives/perspective/([0-9]+)/");
		m = p.matcher(content);
		
		while(true) {
			if(m.find())
				perspective.add(m.group(1));
			else
				break;
		}
		
		caseAndPerspective.add(caseIDList);
		caseAndPerspective.add(perspective);
		
		return caseAndPerspective;
	}
	
	public static ArrayList<String[]> getAllCaseInfo(ArrayList<String> caseIDList) {
		ArrayList<String[]> allInfoList = new ArrayList<String[]>();
		
		for(int i=0; i<caseIDList.size(); i++)
			allInfoList.add(getOneCase(caseIDList.get(i)));
		
		return allInfoList;
	}
	
	public static ArrayList<String[]> getAllCaseInfo_db(ArrayList<String> caseIDList, SQLCommands sc) throws Exception {
		ArrayList<String[]> allInfoList = new ArrayList<String[]>();		
		
		ResultSet rs = null;
		
		for(int i=0; i<caseIDList.size(); i++) {
			rs = sc.getCaseInfoWithID(caseIDList.get(i)); 
			
			if(rs.next()) {
				String[] oneCase = new String[4];
				oneCase[0] = caseIDList.get(i);//case ID
				oneCase[1] = rs.getString("name").trim();//case name
				oneCase[2] = rs.getString("uploadDate").trim();//date
				oneCase[3] = rs.getString("author").trim();//author
				allInfoList.add(oneCase);
			}
			
			rs.close();
		}		
		
		return allInfoList;
	}	
	
	public String[] getOneCaseInfo_db(String caseID, SQLCommands sc) throws Exception {
		String[] oneCase = new String[4];		
		
		ResultSet rs = sc.getCaseInfoWithID(caseID);
		
		if(rs.next()){
			oneCase[0] = caseID;//case ID
			oneCase[1] = rs.getString("name").trim();//case name
			oneCase[2] = rs.getString("uploadDate").trim();//date
			oneCase[3] = rs.getString("author").trim();//author
		}
		
		rs.close();		
		
		return oneCase;
	}
	
	public static ArrayList<String[]> getAllPerspectiveInfo(ArrayList<String> perspective) {
		ArrayList<String[]> allInfoList = new ArrayList<String[]>();
		
		for(int i=0; i<perspective.size(); i++)
			allInfoList.add(getOnePerspective(perspective.get(i)));
		
		return allInfoList;
	}
	
	public static ArrayList<String[]> getAllPerspectiveInfo_db(ArrayList<String> perspective, SQLCommands sc) throws Exception {
		ArrayList<String[]> allInfoList = new ArrayList<String[]>();		
		
		ResultSet rs = null;
		
		for(int i=0; i<perspective.size(); i++) {
			rs = sc.getPerspectiveInfoWithID(perspective.get(i)); 
			
			if(rs.next()) {
				String[] oneCase = new String[4];
				oneCase[0] = perspective.get(i);//perspective ID
				oneCase[1] = rs.getString("name").trim();//perspective name
				oneCase[2] = rs.getString("uploadDate").trim();//date
				oneCase[3] = rs.getString("author").trim();//author
				allInfoList.add(oneCase);
			}
			
			rs.close();
		}		
		
		return allInfoList;
	}
	
	public static String[] getOneCase(String caseID) {
		//0:caseID, 1:title, 2:date, 3:author
		String[] info = new String[4];
		
		String url = "http://webmm.ahrq.gov/case.aspx?caseID=" + caseID;
		String content = "";
		
		try {
			content = CatchInf.getHtml(url);			
		}catch(Exception e) {			
			return null;
		}	
		
		//match caseID
		info[0] = caseID;
		
		//----match title---
		Pattern p = Pattern.compile("lblTitle\">(.+)</span>");
		Matcher m = p.matcher(content);
		
		if(m.find())
			info[1] = m.group(1).replaceAll("<i>", "").replaceAll("</i>", "").trim();
		else
			info[1] = "NA";
		//----
		
		//----match date----
		p = Pattern.compile("LabelCaseDate\">(.+)</span>");		
		m = p.matcher(content);
		
		if(m.find())
			info[2] = m.group(1).trim();
		else
			info[2] = "NA";
		//------
		
		//----match author----
		p = Pattern.compile("lblCommentators\">(.+)</span></b>");		
		m = p.matcher(content);
		
		if(m.find())
			info[3] = m.group(1).trim();
		else
			info[3] = "NA";
		//------		
		
		return info;
	}
	
	public static String[] getOnePerspective(String perspectiveID) {
		//0:perspectiveID, 1:title, 2:date, 3:author
		String[] info = new String[4];
		
		String url = "http://webmm.ahrq.gov/perspective.aspx?perspectiveID=" + perspectiveID;
		String content = "";
		
		try {
			content = CatchInf.getHtml(url);			
		}catch(Exception e) {			
			return null;
		}	
		
		//match perspectiveID
		info[0] = perspectiveID;
		
		//----match title---
		Pattern p = Pattern.compile("hometitle16\">(.+)</p>");
		Matcher m = p.matcher(content);
		
		if(m.find())
			info[1] = m.group(1).replaceAll("<i>", "").replaceAll("</i>", "").trim();
		else
			info[1] = "NA";
		//----
		
		//----match date----
		p = Pattern.compile("lblMonthPerspectiveType\">(.+[0-9]+)&nbsp");		
		m = p.matcher(content);
		
		if(m.find())
			info[2] = m.group(1).trim();
		else
			info[2] = "NA";
		//------
		
		//----match author----
		p = Pattern.compile("<p class=\"font12\"><b>by(.+)</b>");		
		m = p.matcher(content);
		
		if(m.find())
			info[3] = m.group(1).trim();
		else
			info[3] = "NA";
		//------		
		
		return info;
	}
	
	//given allCaseAndPerspectiveInfo, return the annotations for every case;
	public ArrayList<ArrayList<String[]>> getAnnotations(ArrayList<String[]> allCaseInfo, SQLCommands sc) throws Exception {
		ArrayList<ArrayList<String[]>> annotations = new ArrayList<ArrayList<String[]>>();
		
		for(int i=0; i<allCaseInfo.size(); i++){
			ArrayList<String[]> oneAnno = getOneAnnotations(allCaseInfo.get(i)[0], sc);
			annotations.add(oneAnno);
		}
		
		return annotations;
	}
	
	//given simiList, return the annotations for every case;
	public ArrayList<ArrayList<String[]>> getAnnotations_simi(ArrayList<SingleCase> simiList, SQLCommands sc) throws Exception {
		ArrayList<ArrayList<String[]>> annotations = new ArrayList<ArrayList<String[]>>();
		
		for(int i=0; i<simiList.size(); i++){
			ArrayList<String[]> oneAnno = getOneAnnotations(simiList.get(i).id, sc);
			annotations.add(oneAnno);
		}
		
		return annotations;
	}
	
	public ArrayList<String[]> getAnnotationForOneCase(String ID, SQLCommands sc) throws Exception {
		ArrayList<String[]> oneAnnotations = new ArrayList<String[]>();
		
		ResultSet rs = sc.browseAnnotation();
		
		while(rs.next()) {
			String caseID = ";" + rs.getString("caseID") + ";";
			if(caseID.contains(";" + ID + ";")) {
				String[] anno = new String[2];
				anno[0] = rs.getString("cate").trim();
				anno[1] = rs.getString("name").trim();
				oneAnnotations.add(anno);
			}
		}
		
		rs.close();		
		
		return oneAnnotations;
	}
	
	public static ArrayList<String[]> getOneAnnotations(String ID, SQLCommands sc) throws Exception {
		ArrayList<String[]> oneAnnotations = new ArrayList<String[]>();
		
		ResultSet rs = sc.browseAnnotation();
		
		while(rs.next()) {
			String caseID = ";" + rs.getString("caseID") + ";";
			if(caseID.contains(";" + ID + ";")) {
				String[] anno = new String[2];
				anno[0] = rs.getString("cate").trim();
				anno[1] = rs.getString("name").trim();
				oneAnnotations.add(anno);
			}
		}
		
		rs.close();		
		
		return oneAnnotations;
	}
}
