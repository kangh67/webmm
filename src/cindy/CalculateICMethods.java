package cindy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

public class CalculateICMethods {
	
	private static HashMap<String, Integer> annots = new HashMap<String, Integer>();
	private static HashMap<String, Integer> freqs = new HashMap<String, Integer>();
	private static HashMap<String, Double> icValues = new HashMap<String, Double>();
	
	public static int treeSize1 = 51;
	
	public static void main(String[] args) throws Exception {
		//get all annot, save to hashmap annots
		getAllAnnot();
		
		//get all freq, save to hashmap freqs
		getAllFreq();
		
		//get all IC, save to hashmap icValues
		getAllICs();
		
		//calculate score for two cases
		double score = averageICScores("1", "1");
		System.out.println("Similarity score is  " + score);	
	}
	
	//gets all annotation for the categories start with 1. table treeinfo has no "C"
	public static void getAllAnnot() throws Exception {
		MySQL my = new MySQL();
		Connection con = my.getConn();
		Statement stmt = con.createStatement();
		String sql = "select * from treeinfo";
		ResultSet rs = stmt.executeQuery(sql); 
		
		while(rs.next()){
			if(rs.getString("cate").startsWith("1"))
				annots.put(rs.getString("cate"), Integer.valueOf(rs.getString("caseNum")));
			else
				break;
		}		
		rs.close();		
	}
	
	//gets all the frequency for the categories start with 1. table treeinfo has no "C"
	public static void getAllFreq() throws Exception {		
		MySQL my = new MySQL();
		Connection con = my.getConn();
		Statement stmt = con.createStatement();
		String sql = "select * from treeinfo";
		ResultSet rs = stmt.executeQuery(sql); 		
		
		ArrayList<String> allCate = new ArrayList<String>();
		while(rs.next()) {
			if(rs.getString("cate").startsWith("1"))
				allCate.add(rs.getString("cate"));
			else
				break;
		}
		rs.beforeFirst();
		
		for(int i=0; i<allCate.size(); i++) {
			int annotSum = 0;
			while(rs.next()) {				
				if(!rs.getString("cate").startsWith("1"))
					break;
				if(rs.getString("cate").equals(allCate.get(i)) || rs.getString("cate").startsWith(allCate.get(i) + "_")) 
					annotSum += annots.get(rs.getString("cate"));				
			}
			freqs.put(allCate.get(i), annotSum);
			rs.beforeFirst();
		}
		
		rs.close();		
	}
	
	//generates all the IC values for all categories start with 1
	public static void getAllICs() throws Exception{
		MySQL my = new MySQL();
		Connection con = my.getConn();
		Statement stmt = con.createStatement();
		String sql = "select * from treeinfo";
		ResultSet rs = stmt.executeQuery(sql); 
		
		while(rs.next()){
			if(rs.getString("cate").startsWith("1"))
				icValues.put(rs.getString("cate"), getICForACate(rs.getString("cate")));
			else
				break;
		}		
		rs.close();			
	}
	
	// extend the similarity between categories to similarity between cases
	// use the average of the similarity scores among all possible combinations of categories for 2 cases 
	public static double averageICScores(String case1, String case2) throws Exception{		
		MySQL sc = new MySQL();		
		
		Vector<String> nameList1 = new Vector<String>();
		Vector<String> nameList2 = new Vector<String>();
		
		//gets the annotation set for first case. excluding C1
		ResultSet rs1 = sc.getCaseByID(case1); 
		ResultSetMetaData meta1 = rs1.getMetaData();		
		if(rs1.next()){
			for(int i = 4; i<= treeSize1; i++){
				String ColName1 = meta1.getColumnName(i);
				String val1 = rs1.getString(ColName1);
				if(val1.equals("1"))
					nameList1.add(ColName1);
			}
		}
		rs1.close();
		System.out.println(case1 + " has categories..."+ nameList1);
		
		//gets the annotation set for second case. excluding C1
		ResultSet rs2 = sc.getCaseByID(case2); 
		ResultSetMetaData meta2 = rs2.getMetaData();
		if(rs2.next()){
			for(int i = 4; i<= treeSize1; i++){
				String ColName2 = meta2.getColumnName(i);
				String val2 = rs2.getString(ColName2);
				if(val2.equals("1"))
					nameList2.add(ColName2);
			}
		}
		rs2.close();
		System.out.println(case2 + " has categories..."+ nameList2);
		
		//every term in annotation set for first case is compared with every term in annotation set for second case
		double sum = 0;
		int countAnno = 0;
		
		for(int i = 0; i<nameList1.size(); i++) {
			for(int j = 0; j<nameList2.size(); j++) {
				countAnno++;
				String s1 = nameList1.get(i);
				String s2 =  nameList2.get(j);
				
				/*
				if(s1.equals("C1")|| s2.equals("C2"))
					sum+=0;
				else
					sum+=calculateLin(s1, s2);
				*/
				
				//if one or both of the categories being compared is the root, the calculation of that is always 0, so sum doesn't change
				//if(!(s1.equals("C1")|| s2.equals("C2")))  //this if statement is commented out because we decided to exclude C1 from our comparison
				
				sum += calculateLin(s1, s2);
				System.out.println("sum right now is "+ sum);
			}
		}
		System.out.println("the total sum is "+ sum);

		System.out.println("the total combinations is "+ countAnno);
		
		//gets the average 
		return sum/countAnno;
			
	}
	
	//gets the information content for a category, input has no "C"
	public static double getICForACate(String cate)throws Exception {		
		double prob = (double)freqs.get(cate) / freqs.get("1");		
		return (prob == 1) ? Math.log10(prob): (-1)*Math.log10(prob);		
	}		
	
	//if either denominator or numerator is 0, the whole score is 0
	public static double calculateLin(String cate1, String cate2) throws Exception {
		String LCA = getLCA(cate1, cate2);
		System.out.println("LCA for "+ cate1 +" and " + cate2 + " is " + LCA);
		double LCAic = icValues.get(LCA.substring(1));
		double cate1IC = icValues.get(cate1.substring(1));
		double cate2IC = icValues.get(cate2.substring(1));

		return ((LCAic == 0.0) || (cate1IC + cate2IC) ==0.0 ) ? 0.0 : (2*LCAic)/(cate1IC + cate2IC);
	}
	
	//finds the lowest common ancestor from two categories
	//DEFINTION OF LCA CHANGED:a category can be a parent of itself (EX: C1_6 and C1_6_2 have parent set [C1, C1_6])
	public static String getLCA(String cate1, String cate2) throws Exception{
		if(cate1.equals(cate2))
			return cate1;  
		
		MySQL object = new MySQL();
		ArrayList<String> parentList1 = getParents(cate1);
		ArrayList<String> parentList2 = getParents(cate2);
		
		//parentList1 becomes the shared set: destructive method
		parentList1.retainAll(parentList2);
	
		//stores the shared set in a local hashmap to get the LCA with highest IC
		HashMap<String, Double> sharedList = new HashMap<String, Double>();
		for(String s: parentList1)
			sharedList.put(s, icValues.get(s.substring(1)));
	
		List<Entry<String, Double>> sorted = object.sortHashMapByValue(sharedList);
		//System.out.println("shared parent set sorted is "+ list);
		
		//ORIGINAL definition of LCA may result in no shared parent term (EX:C1 & C1_3, because C1 has no parent). in that case use C1 as LCA
		//return (list.isEmpty()) ?  "C1" : list.get(0).getKey();
		return sorted.get(0).getKey(); //new definition of LCA will NOT have an empty shared set	
	}
	
	//gets an arraylist of parents for a category
	public static ArrayList<String> getParents(String cate){
		ArrayList<String> parentList = new ArrayList<String>();
		String copy = cate;	
		parentList.add(copy); //uncomment this line if you want to keep the ORIGINAL definition of LCA (a category is not a parent of itself)
		while(copy.indexOf('_')!=-1)
		{
			int i = copy.lastIndexOf('_');
			copy = copy.substring(0, i);
			parentList.add(copy);	

		}
		//System.out.println("final parent list for "+ cate +" is " + parentList);
		return parentList;
		
	}	
}