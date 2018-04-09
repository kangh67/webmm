package contributing_factor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import user.DBBean;

public class CF_structured {
	public static void main(String[] args) throws SQLException {
		DBBean dbb = new DBBean();
		/***
		HashMap<String, String> ans = new HashMap<String, String>();
		ans.put("sir_9","11");
		ans.put("fall_2", "1");
		//ans.put("fall_10", "1||8||12||13");
		
		System.out.println(getCFs(dbb, ans));
		**/
		
		
		String fingerprint = CFsToFingerprint(dbb, "1_1||1_3||5_5_1_1");
		System.out.println(fingerprint.length() + ": " +fingerprint);
		
	}
	
	public static ArrayList<String> getCFs(DBBean dbb, HashMap<String, String> answers) throws SQLException {
		ArrayList<String> res = new ArrayList<String>();		
		String sql = "select * from contributing_factors_str";
		ResultSet rs = dbb.query(sql);
		
		while(rs.next()) {
			String[] active_on = rs.getString("Active_on").split("\\|\\|");
			String[] active_off = rs.getString("Active_off").split("\\|\\|");
			
			if(!active_on[0].equals("NA")) {
				for(int i=0; i<active_on.length; i++) {
					String[] q_a = active_on[i].split("\\$");
					if(answers.containsKey(q_a[0])) {
						String[] thisans = answers.get(q_a[0]).split("\\|\\|");
						for(int j=0; j<thisans.length; j++) {
							if(thisans[j].equals(q_a[1]) && !res.contains(rs.getString("CFID"))) {
								res.add(rs.getString("CFID"));
								break;
							}	
						}
					}								
				}
			}
			
			if(!active_off[0].equals("NA")) {			
				for(int i=0; i<active_off.length; i++) {
					String[] q_a = active_off[i].split("\\$");
					if(answers.containsKey(q_a[0])) {
						String[] thisans = answers.get(q_a[0]).split("\\|\\|");
						boolean exist = false;
						for(int j=0; j<thisans.length; j++) {
							if(thisans[j].equals(q_a[1])) {
								exist = true;
							}	
						}
						if(!exist && !res.contains(rs.getString("CFID"))) {
							res.add(rs.getString("CFID"));
							break;
						}
					}					
				}
			}
		}
		
		rs.close();		
		return res;
	}
	
	public static String CFsToFingerprint(DBBean dbb, String CFs) throws SQLException {		
		String sql = "select * from contributing_factors_list";
		ResultSet rs = dbb.query(sql);		
		rs.last();
		char[] f = new char[rs.getRow()];
		rs.beforeFirst();
		
		String[] CF = CFs.split("\\|\\|");		
		
		while(rs.next()) {
			boolean activate = false;
			String thisID = rs.getString("CFID");
			for(int i=0; i<CF.length; i++) {
				if(CF[i].startsWith(thisID + "_") || CF[i].equals(thisID)) {
					activate = true;
					//System.out.println(thisID + " (" + rs.getRow() + ") is activated by " + CF[i]);
					break;
				}
			}
			if(activate)
				f[rs.getRow()-1] = '1';
			else
				f[rs.getRow()-1] = '0';
		}
		
		rs.close();		
		
		return new String(f);
	}
}
