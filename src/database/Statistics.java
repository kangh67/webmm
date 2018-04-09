package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import report.Stat;

import user.DBBean;

public class Statistics {
	public static void main(String[] args) throws SQLException {
		System.out.println(getAnswerDistribution("fall", "fall_13"));
	}
	
	/*
	 * Get answer distribution for a certain question
	 * type = one of the 9 subtypes of patient safety event
	 * question = qid of the question
	 */
	public static HashMap<String, Double> getAnswerDistribution(String type, String question) throws SQLException {
		DBBean dbb = new DBBean();
		ResultSet rs = dbb.query("select * from questions where qid='" + question + "'");
		
		//----get answer list
		rs.next();		
		String[] answers = rs.getString("acontent").trim().split("\\|\\|");		
		rs.close();
		//----
		
		//----initialize hashmap
		HashMap<String, Double> ansDis = new HashMap<String, Double>();
		for(int i=0; i<answers.length; i++) 
			ansDis.put(answers[i], 0.0);
		//----
		
		//----statistic
		rs = dbb.query("select * from report_" + type);
		int sum = 0;		
		while(rs.next()) {
			if(rs.getString(question) != null) {
				String[] multiAns = rs.getString(question).split("\\|\\|");
				for(int j=0; j<multiAns.length; j++) {
					String realAns = multiAns[j].split("\\$\\$")[0];
					ansDis.put(answers[Integer.valueOf(realAns)], ansDis.get(answers[Integer.valueOf(realAns)]) + 1);
					
				}
				sum ++;
			}
		}		
		rs.close();
		dbb.disConnect();
		//----
		
		//to percentage
		Stat st = new Stat();
		for(int i=0; i<answers.length; i++)
			ansDis.put(answers[i], st.getSignificantFigure(ansDis.get(answers[i]) * 100 / (double)sum, 1));
		//---	
		
		return ansDis;
	}
}
