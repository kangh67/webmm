package similarity_to;

import java.sql.ResultSet;
import java.util.Vector;

import mysql.SQLCommands;

public class CalculateInside_to {
	public static int treeSize1 = 49;
	public static int treeSize2 = 7;
	public static int treeSize3 = 74;
	public static int treeSize4 = 53;
	public static int treeSize5 = 20;
	public static int treeSize6 = 16;
	
	public static void main(String[] args) throws Exception {
		getScore_to("1", "224", "3");
	}
	
	/** 
	Calculate similarity score for 2 cases.
	type:
		1 = Safety Target
		2 = Error Type
		3 = Approach to Improving Safety
		4 = Clinical Area
		5 = Target Audience
		6 = Setting of Care
	case1 and case2 is the ID of case.
	 * @throws Exception 
	**/
	public static double getScore_to(String type, String case1, String case2) throws Exception {
		SQLCommands sc = new SQLCommands();
		ResultSet rs1 = sc.getCaseByID(case1);
		ResultSet rs2 = sc.getCaseByID(case2);
		
		rs1.next();
		rs2.next();
		
		String finger1 = rs1.getString("f" + type);
		String finger2 = rs2.getString("f" + type);
		
		rs1.close();
		rs2.close();
		
		//startsWith 0 means this case wasn't put in this category type.
		if(finger1.startsWith("0") || finger2.startsWith("0"))
			return 0;
		System.out.println(finger1);
		Vector<Integer> v1 = leaveSons(finger1, type);
		System.out.println(v1);
		
		return 0;
	}
	
	//leave only sons
	public static Vector<Integer> leaveSons(String fingerprint, String type) throws Exception {
		//cut root
		Vector<Integer> cutRoot = new Vector<Integer>();
		for(int i=1; i<fingerprint.length(); i++)
			cutRoot.add(Integer.valueOf(fingerprint.substring(i, i+1)));
		
		int start;
		
		//make sure the start of "db_id" based on different types
		if(type.equals("1"))
			start = 2;
		else if(type.equals("2"))
			start = 2 + treeSize1;
		else if(type.equals("3"))
			start = 2 + treeSize1 + treeSize2;
		else if(type.equals("4"))
			start = 2 + treeSize1 + treeSize2 + treeSize3;
		else if(type.equals("5"))
			start = 2 + treeSize1 + treeSize2 + treeSize3 + treeSize4;
		else
			start = 2 + treeSize1 + treeSize2 + treeSize3 + treeSize4 + treeSize5;
		
		//if annotated as 1 but not the leaf point, set it as 0
		SQLCommands sc = new SQLCommands();
		for(int i=0; i<cutRoot.size(); i++) {
			if(cutRoot.get(i) == 1) {
				ResultSet rs = sc.getTreeBydb_id(String.valueOf(i + start));			
				rs.next();
			
				if(!rs.getString("son").equals("NA"))
					cutRoot.set(i, 0);
					
				rs.close();
			}
		}
		
		return cutRoot;
	}
	
	//get all the associated parent terms (excluding the root of the hierarchy), return a String[] contains both these parents and original annotations.
	public static Vector<Integer> getTerms(String fingerprint, String type) throws Exception {
		//cut root
		Vector<Integer> cutRoot = new Vector<Integer>();
		for(int i=1; i<fingerprint.length(); i++)
			cutRoot.add(Integer.valueOf(fingerprint.substring(i, i+1)));
		
		int start;
		
		//make sure the start of "db_id" based on different types
		if(type.equals("1"))
			start = 2;
		else if(type.equals("2"))
			start = 2 + treeSize1;
		else if(type.equals("3"))
			start = 2 + treeSize1 + treeSize2;
		else if(type.equals("4"))
			start = 2 + treeSize1 + treeSize2 + treeSize3;
		else if(type.equals("5"))
			start = 2 + treeSize1 + treeSize2 + treeSize3 + treeSize4;
		else
			start = 2 + treeSize1 + treeSize2 + treeSize3 + treeSize4 + treeSize5;
		System.out.println(cutRoot);
		//find fathers and annotate
		SQLCommands sc = new SQLCommands();
		for(int i=0; i<cutRoot.size(); i++) {
			ResultSet rs = sc.getTreeBydb_id(String.valueOf(i + start));
			
			rs.next();
			
			if(!rs.getString("father").contains("_"))
				continue;
			else {
				ResultSet rsFather = sc.getTreeByCate(rs.getString("father"));
				rsFather.next();
				
				cutRoot.set(rsFather.getInt("db_id") - start, 1);
				
				rsFather.close();
			}
			
			rs.close();
		}
		System.out.println(cutRoot);
		return cutRoot;
	}
}
