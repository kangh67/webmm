package report;

import java.sql.ResultSet;
import java.util.ArrayList;

import user.DBBean;

public class ContinueAndEdit {
	public ArrayList<String> getGeneralQuestions(DBBean dbb) throws Exception {
		ArrayList<String> general = new ArrayList<String>();			
		
		String sql = "select * from questions";
		ResultSet rs = dbb.query(sql);
		
		while(rs.next()) { 
			String qid = rs.getString("qid");
			if(qid.contains("herf") || qid.contains("sir") || qid.contains("pif") || qid.contains("severity"))
			general.add(qid);
		}
		
		rs.close();	
		
		return general;
	}
	
	public ArrayList<String> getDetailQuestions(DBBean dbb, String formName) throws Exception {
		ArrayList<String> detail = new ArrayList<String>();			
		
		String sql = "select * from questions where qid like '%" + formName + "%'";
		ResultSet rs = dbb.query(sql);
		
		while(rs.next())
			detail.add(rs.getString("qid"));
		
		rs.close();		
		
		return detail;
	}
	
	public static void main(String[] args) throws Exception {
		ContinueAndEdit cae = new ContinueAndEdit();
		ArrayList<String> detail = cae.getDetailQuestions(new DBBean(), "fall");
		
		System.out.println(detail);
	}
}
