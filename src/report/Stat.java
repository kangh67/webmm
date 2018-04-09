package report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import user.DBBean;

public class Stat {
	public static void main(String[] args) throws SQLException {
		DBBean dbb = new DBBean();
		Stat st = new Stat();
		int[] dis = st.getDistribution_user(dbb, "fall", "fall_10", "hkang");
		for(int i=0; i<dis.length; i++)
			System.out.println(dis[i]);
	}
	
	public double getPercent(DBBean dbb, String type, String question, String ans) throws SQLException {		
		ResultSet rs = dbb.query("select * from report_" + type);
		
		int sum = 0;
		int count = 0;
		
		while(rs.next()) {
			if(rs.getString(question) != null) {
				String[] allans = rs.getString(question).split("\\|\\|");
				for(int i=0; i<allans.length; i++) {
					if(allans[i].equalsIgnoreCase(ans)) {						
						count ++;
						break;
					}
				}
			}
			sum ++;
		}
		
		rs.close();		
		
		return getSignificantFigure(100 * count / (double)sum, 1);
	}
	
	public String[] getAnswerList(DBBean dbb, String question) throws SQLException {		
		ResultSet rs = dbb.query("select * from questions where qid ='" + question + "'");
		rs.next();
		
		String[] res = rs.getString("acontent").split("\\|\\|");		
		
		rs.close();		
		
		return res;
	}
	
	public ArrayList<String> getQuestionList(DBBean dbb, String type) throws SQLException {
		ArrayList<String> questionList = new ArrayList<String>();		
		ResultSet rs = dbb.query("select * from questions where qid like '%" + type + "%'");
		
		while(rs.next())
			questionList.add(rs.getString("qid"));
		
		rs.close();		
		
		return questionList;
	}
	
	/*
	 * e.g, if question=fall_1, return [0]=1, [1]=Was the fall unassisted or assisted?, [2]=CHECK ONE:
	 */
	public String[] getQuestionInfo(DBBean dbb, String type, String question) throws SQLException {
		String[] questionInfo = new String[3];		
		ResultSet rs = dbb.query("select * from questions where qid ='" + question + "'");
		while(rs.next()){		
			questionInfo[0] = question.replace(type + "_", "");
			questionInfo[1] = rs.getString("qcontent");
			questionInfo[2] = rs.getString("qref");
		}
		rs.close();		
		
		return questionInfo;
	}
	
	public int[] getDistribution_global(DBBean dbb, String type, String question) throws SQLException {		
		ResultSet rs = dbb.query("select * from questions where qid ='" + question + "'");
		rs.next();
		int size = Integer.valueOf(rs.getString("anum"));
		int[] dis = new int[size];
		
		for(int i=0; i<dis.length; i++)
			dis[i] = 0;
		
		rs = dbb.query("select * from report_" + type);
		
		while(rs.next()) {
			if(rs.getString(question) != null) {
				String[] allans = rs.getString(question).split("\\|\\|");
				for(int i=0; i<allans.length; i++)
					try {
						dis[Integer.valueOf(allans[i])] ++;
					}
					catch(java.lang.NumberFormatException e) {
						dis[dis.length-1] ++;
					}
			}
		}
		
		rs.close();		
		
		return dis;
	}
	
	public int[] getDistribution_user(DBBean dbb, String type, String question, String userName) throws SQLException {		
		ResultSet rs = dbb.query("select * from questions where qid ='" + question + "'");
		rs.next();
		int size = Integer.valueOf(rs.getString("anum"));
		int[] dis = new int[size];
		
		for(int i=0; i<dis.length; i++)
			dis[i] = 0;
		
		rs = dbb.query("select * from report_general where reporter='" + userName + "' AND formn='" + type + "' AND rstatus='2'");
		
		ArrayList<String> uni_ids = new ArrayList<String>();
		
		while(rs.next())
			uni_ids.add(rs.getString("uni_id"));
		
		for(int i=0; i<uni_ids.size(); i++) {
			rs = dbb.query("select * from report_" + type + " where uni_id ='" + uni_ids.get(i) + "'");
			while(rs.next()) {
				if(rs.getString(question) != null) {
					String[] allans = rs.getString(question).split("\\|\\|");
					for(int j=0; j<allans.length; j++)
						try {
							dis[Integer.valueOf(allans[j])] ++;
						}
						catch(java.lang.NumberFormatException e) {
							dis[dis.length-1] ++;
						}
				}
			}
		}		
		rs.close();
				
		return dis;
	}
	
	public double[] getPercentage(int[] count) {
		int sum = 0;
		for(int i=0; i<count.length; i++)
			sum += count[i];
		
		double[] res = new double[count.length];
		
		if(sum == 0) {
			for(int i=0; i<res.length; i++)
				res[i] = 0.0;
			return res;
		}
		
		for(int i=0; i<res.length; i++)
			res[i] = getSignificantFigure(100 * count[i] / (double) sum, 1);
		
		return res;
	}
	
	public double getSignificantFigure(double data, int SignificantNum) {
		double size = Math.pow(10, SignificantNum);		
		long l1 = Math.round(data * size);
		
		return l1 / size;
	}
}
