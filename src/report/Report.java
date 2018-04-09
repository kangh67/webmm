package report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import contributing_factor.CF_structured;
import contributing_factor.CF_unstructured;
import user.DBBean;

public class Report {
	public void case_create(DBBean dbb, NewCase nc) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String initime = df.format(new Date());		
		String uni_id = nc.uni_id;
		String reporter = nc.reporter;
		String rstatus = "0";
		String rname = nc.caseName;			
		
		String sql = "insert into report_general (uni_id,initime,uptime,reporter,rstatus,rname)values(?,?,?,?,?,?)";
	    PreparedStatement ps = dbb.getConn().prepareStatement(sql);
	    
	    ps.setString(1, uni_id);
	    ps.setString(2, initime);
	    ps.setString(3, initime);
	    ps.setString(4, reporter);
	 	ps.setString(5, rstatus);
	 	ps.setString(6, rname);
	 	
	 	ps.executeUpdate();
	 	ps.close();	 		 	
	 	
	 	System.out.println("[report.Report.java] New case created: " + uni_id);
	}
	
	public void case_update(DBBean dbb, NewCase nc, String lastpage) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String uptime = df.format(new Date());		
			
		Connection con= dbb.getConn();
		String sql;
		PreparedStatement ps;
		
		ArrayList<String> route = nc.getRoute(lastpage);
		HashMap<String, String> info = nc.getInfo(lastpage); 		
	
		if(lastpage.equals("general")) {	
			sql = "select * from report_general where uni_id='" + nc.uni_id + "'";
			ResultSet rs = dbb.query(sql);
			String status = "";
			String initime = "";
			String preChoice = "";
			
			if(rs.next()) {
				status = rs.getString("rstatus");	
				initime = rs.getString("initime");
				preChoice = rs.getString("formn");
			}
			
			rs.close();
			
			//----if not new, delete and insert, also delete records from the form wasn't selected----
			if(!status.equals("0")) {
				sql = "delete from report_general where uni_id='" + nc.uni_id + "'";
				dbb.delete(sql);
				
				sql = "insert into report_general (uni_id,initime,reporter,rname,des,link)values(?,?,?,?,?,?)";
			    ps = con.prepareStatement(sql);
			    
			    ps.setString(1, nc.uni_id);
			    ps.setString(2, initime);			   
			 	ps.setString(3, nc.reporter);
			 	ps.setString(4, nc.caseName);
			 	ps.setString(5, nc.description);
			 	ps.setString(6, nc.link);
			 	
			 	//System.out.println(nc.description);
			 	//System.out.println(nc.link);
			 	
			 	ps.executeUpdate();
			 	ps.close();
			 	
			 	//delete records from the form wasn't selected
			 	ArrayList<String> ava = nc.showAvaForms();
			 	String[] former = preChoice.split("\\|\\|");
			 	for(int i=0; i<former.length; i++) {
			 		if(ava == null)
			 			break;
			 		else if(!ava.contains(former[i])) {
			 			sql = "delete from report_" + former[i] + " where uni_id='" + nc.uni_id + "'";
			 			dbb.delete(sql);
			 		}
			 	}
			 	//--
			}
			//------
			
			//------update answers----
			for(int i=0; i<route.size(); i++) {
				sql = "update report_" + lastpage + " set " + route.get(i) + "='" + info.get(route.get(i)).replaceAll("'", "''") + "' where uni_id='" + nc.uni_id + "'"; 
				//System.out.println(sql);
				ps = con.prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			}
			//----			
			
			//----update formn and rname, set rstatus = 1---
			ArrayList<String> avaForms = nc.showAvaForms();
			String ava = "NA";
			if(avaForms.size() != 0) {	//whether only choose herf_7 as "others"
				ava = avaForms.get(0);
				for(int i=1; i<avaForms.size(); i++)
					ava = ava + "||" + avaForms.get(i);
			}				
			
			sql = "update report_" + lastpage + " set formn='" + ava + "',rname='" + nc.caseName.replaceAll("'", "''") + "',des='" + nc.description.replaceAll("'", "''") + "',link='" + nc.link.replaceAll("'", "''") + "',rstatus='1' where uni_id='" + nc.uni_id + "'"; 
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			
			//-----
			
			//----update fingerprint
			ArrayList<String> allQuestion = getAllQuestionsFromCertainType(dbb, "general");
			sql = "update report_general set " + allQuestion.get(0) + "_f='" + CalFingerprint.getFPofOneQuestion_weight(dbb, nc.uni_id, "general", allQuestion.get(0)) + "'";
			for(int i=1; i<allQuestion.size(); i++)
				sql += "," + allQuestion.get(i) + "_f='" + CalFingerprint.getFPofOneQuestion_weight(dbb, nc.uni_id, "general", allQuestion.get(i)) + "'";
			
			sql += " where uni_id='" + nc.uni_id + "'";
			//System.out.println(sql);
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();			
			//----
			
			/***
			//----update f_general
			sql = "update report_" + lastpage + " set f_" + lastpage + "='" + CalFingerprint.getWholeFP(lastpage, nc.uni_id) + "' where uni_id='" + nc.uni_id + "'"; 
			System.out.println(sql);
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			//-----
			 * 
			 */
		}else {
			//-----judge whether exist
			sql = "select * from report_" + lastpage + " where uni_id='" + nc.uni_id + "'";
			ResultSet rs = dbb.query(sql);
			boolean newcase;
			
			if(rs.next())
				newcase = false;
			else
				newcase = true;
			
			rs.close();
			//------
			
			//-----if not new, delete form record
			if(!newcase) {
				sql = "delete from report_" + lastpage + " where uni_id='" + nc.uni_id + "'";
				dbb.delete(sql);
			}
			//-----			
			
			//----insert
			sql = "insert into report_" + lastpage + " (uni_id)values(?)";
		    ps = con.prepareStatement(sql);
		    
		    ps.setString(1, nc.uni_id);			 	
		 	ps.executeUpdate();
		 	ps.close();
			//-------
			
			//------update answers----
			for(int i=0; i<route.size(); i++) {
				sql = "update report_" + lastpage + " set " + route.get(i) + "='" + info.get(route.get(i)).replaceAll("'", "''") + "' where uni_id='" + nc.uni_id + "'"; 
				//System.out.println(sql);
				ps = con.prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			}
			//----
			
			//----update fingerprint
			ArrayList<String> allQuestion = getAllQuestionsFromCertainType(dbb, lastpage);
			sql = "update report_" + lastpage + " set " + allQuestion.get(0) + "_f='" + CalFingerprint.getFPofOneQuestion_weight(dbb, nc.uni_id, lastpage, allQuestion.get(0)) + "'";
			for(int i=1; i<allQuestion.size(); i++)
				sql += "," + allQuestion.get(i) + "_f='" + CalFingerprint.getFPofOneQuestion_weight(dbb, nc.uni_id, lastpage, allQuestion.get(i)) + "'";
			
			sql += " where uni_id='" + nc.uni_id + "'";
			//System.out.println(sql);
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();			
			//----
			
			//----update contributing factor fingerprints for fall events
			if(lastpage.equals("fall")) {
				//calculate contributing factors_unstr
				String cf_unstr = CF_unstructured.getCFs_oneLineString(dbb, nc.description);		 	
				//get fingerprint of contributing factors_unstr
				String fingerprint_unstr = CF_structured.CFsToFingerprint(dbb, cf_unstr);
				//calculate contributing factors_str
				HashMap<String, String> allAnswers = new HashMap<String, String>();
				allAnswers.putAll(nc.info_gen);;
				allAnswers.putAll(nc.info_fall);
				ArrayList<String> cfs_str = CF_structured.getCFs(dbb, allAnswers);
			 	String cf_str = "NA";
			 	if(cfs_str.size() > 0) {
			 		cf_str = cfs_str.get(0);
			 		for(int i=1; i<cfs_str.size(); i++)
			 			cf_str += "||" + cfs_str.get(i);
			 	}			 	
			 	//get fingerprint of contributing factors_str
			 	String fingerprint_str = CF_structured.CFsToFingerprint(dbb, cf_str);
			 	
			 	sql = "update report_fall set cf_str='" + cf_str + "',fingerprint_str='" + fingerprint_str + "',cf_unstr='" + cf_unstr + "',fingerprint_unstr='" + fingerprint_unstr + "' where uni_id='" + nc.uni_id + "'";
			 	ps = con.prepareStatement(sql);
				ps.executeUpdate();
				ps.close();	
			}
			//----
			
			/**
			//----update fingerprint
			sql = "update report_" + lastpage + " set f_" + lastpage + "='" + CalFingerprint.getWholeFP(lastpage, nc.uni_id) + "' where uni_id='" + nc.uni_id + "'"; 
			System.out.println(sql);
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			//-----
			 * 
			 */
			
		}
		
		//----refresh update time
		sql = "update report_general set uptime='" + uptime + "' where uni_id='" + nc.uni_id + "'"; 
		ps = con.prepareStatement(sql);
		ps.executeUpdate();
	 	ps.close();	
	 	//------		
	}
	
	public boolean confirm(DBBean dbb, String uni_id) throws Exception {				
		Connection con= dbb.getConn();
		PreparedStatement ps;		
		String sql = "update report_general set rstatus='2' where uni_id='" + uni_id + "'";
		
		ps = con.prepareStatement(sql);
		ps.executeUpdate();
		ps.close();		
		
		return true;
	}
	
	public ResultSet getUserReports(DBBean dbb, String user) {				
		String sql1 = "select * from report_general where rstatus='2' AND reporter='" + user + "'";	
		String sql2 = "select * from report_general where rstatus='1' AND reporter='" + user + "'";
		String sql3 = "select * from report_general where rstatus='0' AND reporter='" + user + "'";
		String sql = sql1 + " union " + sql2 + " union " + sql3;
		
		ResultSet rs = dbb.query(sql);
		
		return rs;
	}
	
	public ResultSet getOtherReports(DBBean dbb, String user) {				
		String sql = "select * from report_general where rstatus='2' AND reporter<>'" + user + "'";	
		ResultSet rs = dbb.query(sql);
		
		return rs;
	}
	
	public boolean deleteReport(DBBean dbb, String uni_id) throws Exception {				
		ResultSet rs = dbb.query("select * from report_general where uni_id='" + uni_id + "'");
		if(rs.next()) {
			String forms = rs.getString("formn");
			rs.close();
		
			String sql = "delete from report_general where uni_id='" + uni_id + "'";
			dbb.delete(sql);
		
			if(forms != null) {
				String[] form = forms.split("\\|\\|");
				for(int i=0; i<form.length; i++) {
					sql = "delete from report_" + form[i] + " where uni_id='" + uni_id + "'";
					dbb.delete(sql);
				}
			}
		}else
			rs.close();		
		
		return true;
	}	
	
	public boolean judgeDifference(String s1, String s2) {
		if(s1 == null && s2 != null)
			return true;
		else if(s1 != null && s2 == null)
			return true;
		else if(s1 == null && s2 == null)
			return false;
		else if(s1.equals(s2))
			return false;
		else
			return true;		
	}
	
	public static ArrayList<String> getAllQuestionsFromCertainType(DBBean dbb, String type) throws SQLException {
		ArrayList<String> questions = new ArrayList<String>();		
		
		if(type.equalsIgnoreCase("general")) {							
			ResultSet rs = dbb.query("select * from questions where qid like '%herf%' OR qid like '%sir%' OR qid like '%pif%' OR qid like '%severity%'");
			
			while(rs.next()) 
				questions.add(rs.getString("qid"));			
			
			rs.close();			
			return questions;
		}else {
			ResultSet rs = dbb.query("select * from questions where qid like '%" + type + "%'");
			
			while(rs.next()) 
				questions.add(rs.getString("qid"));			
			
			rs.close();		
			return questions;
		}			
	}
	
	public ArrayList<String> getAnswerListOfOthers(DBBean dbb, String qid) throws Exception {		
		ResultSet rs = dbb.query("select * from report_" + qid.split("_")[0]);
		
		ArrayList<String> answerList = new ArrayList<String>();
		
		while(rs.next()) {						
			boolean overlap = false;
			if(rs.getString(qid) == null)
				continue;
			if(rs.getString(qid).contains("$$") && rs.getString(qid).split("\\$\\$").length > 1) {
				for(int i=0; i<answerList.size(); i++) {
					if(answerList.get(i).equalsIgnoreCase(rs.getString(qid).split("\\$\\$")[1]))
						overlap = true;
				}
				if(!overlap) {
					//standardize the first char to upper case
					String answer = rs.getString(qid).split("\\$\\$")[1];
					answer = answer.substring(0, 1).toUpperCase() + answer.substring(1);
					
					answerList.add(answer);
				}
			}				
		}
		
		rs.close();		
		
		//sort alphabetically
		Collections.sort(answerList);
		
		return answerList;
	}
	
	/**
	public static void main(String[] args) throws Exception {
		Report rt = new Report();
		
		ArrayList<String> aa = rt.getAnswerListOfOthers("fall_5");
		for(int i=0; i<aa.size(); i++)
			System.out.println(aa.get(i));
	}
	**/
}
