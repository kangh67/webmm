package search;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import mysql.SQLCommands;

public class ShowSimilarity {

	public static void main(String[] args) throws Exception {
		SQLCommands sc = new SQLCommands();
		sc.getConn();
		
		ShowSimilarity ss = new ShowSimilarity();
		ArrayList<SingleCase> simiList = ss.getSimiList("241", "vs", "1", sc);		
			
		for(int i=0; i<simiList.size(); i++) {
			System.out.println(simiList.get(i).id + "\t" + simiList.get(i).name + "\t" + simiList.get(i).author + "\t" + simiList.get(i).date + "\t" + simiList.get(i).score);
		}
		
		sc.disconnect();
	}
	
	//given queryID, method(VS, TO, or IC), return its similarity list
	public ArrayList<SingleCase> getSimiList(String queryID, String method, String type, SQLCommands sc) throws Exception {
		ArrayList<SingleCase> simiList = new ArrayList<SingleCase>();
		ArrayList<String> caseID = new ArrayList<String>();
		
		ResultSet rs = sc.getCaseNum(method, type);	
		
		while(rs.next()) 
			caseID.add(rs.getString("caseID").trim());			
		
		rs.close();		
		
		rs = sc.getCaseSimilarity(queryID, method, type);
		rs.next();
		
		String[] scores = rs.getString("scores").split(";");			
			
		for(int i=0; i<scores.length; i++) {
			if(!scores[i].equals("0.0") && !caseID.get(i).equals(queryID)) {
				SingleCase single = new SingleCase(caseID.get(i), Double.valueOf(scores[i]));
				simiList.add(single);				
			}
		}						
		
		rs.close();		
		
		Collections.sort(simiList);
		
		simiList = getAllInfoForSimiCase(simiList, sc);
		
		return simiList;
	}
	
	
	public static ArrayList<SingleCase> getAllInfoForSimiCase(ArrayList<SingleCase> simiList, SQLCommands sc) throws Exception {		
		ResultSet rs = null;
		
		for(int i=0; i<simiList.size(); i++) {
			rs = sc.getCaseInfoWithID(simiList.get(i).id); 
			
			if(rs.next()) {				
				simiList.get(i).name = rs.getString("name").trim();//case name
				simiList.get(i).date = rs.getString("uploadDate").trim();//date
				simiList.get(i).author = rs.getString("author").trim();//author				
			}
			
			rs.close();
		}		
		
		return simiList;
	}
	
}
