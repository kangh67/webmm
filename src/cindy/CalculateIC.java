package cindy;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CalculateIC {
	public static int treeSize1 = 49; //we are only concerned with type 1 for now
/*	public static int treeSize2 = 7;
	public static int treeSize3 = 74;
	public static int treeSize4 = 53;
	public static int treeSize5 = 20;
	public static int treeSize6 = 16;*/
	
	public static void main(String[] args) throws Exception {
		double score = averageScore("1","2"); 
		System.out.println("score is  "+ score);
		
		System.out.println(findLCA("C1_1", "C1_1_1"));
		//System.out.println(getSignificantFigure(0.122614, 3));

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
	
	//gets the information content for a parent category
	//No need to find IC for leaves because they have no children
	public static double getICForCate(String cate)throws Exception {
		double prob = getProbForCate(cate);
		return (prob == 1) ? Math.log10(prob): (-1)*Math.log10(prob);
		
	}
	//gets the probability of a category occurring in a case
	public static double getProbForCate(String cate) throws Exception {
		int freqTerm = getFreqForCate(cate);
		int freqRoot = getFreqForCate("1");  //since we only work with type 1, the cate is "1"
		double p = (double)freqTerm/freqRoot;
		return p;
	}
	
	//gets the frequency of a parent category
	public static int getFreqForCate(String cate) throws Exception {
		MySQL sc = new MySQL();
		ResultSet rs = sc.getTreeByCate(cate); 
		if(rs.next())
		{
			System.out.println("looping once ....");
			String freq = rs.getString("caseNum"); 
			System.out.println("freq is "+ freq);
			rs.close();
			return Integer.valueOf(freq);
		}else 
		{
			rs.close();
			return 0;
		}
		
		
	}
	

	//finds the lowest common ancestor for two categories
	/*
	 * return the cate if the pair is identical. 
	 * else return the shared parent term with highest IC,
	 * which is not necessarily the shared cate that is one level up
	 */
	public static String findLCA(String cate1, String cate2) throws Exception{
		MySQL object = new MySQL();
		HashMap<String, Double> icValues = new HashMap<String, Double>();
		String parent = higherLevel(cate1, cate2);
		if(cate1.equals(cate2))
			return cate1;  
		else {									
		//	System.out.println("cate1 is " + cate1);
		//	System.out.println("cate2 is " + cate2);
		//	String parent = s;
			while(parent.indexOf('_')!=-1)
			{
				int i = parent.lastIndexOf('_');
			//	System.out.println("last index of _ is " + i);
			
				parent = parent.substring(0, i);
				double ic = getICForCate(parent);
				icValues.put(parent, ic);
			//	System.out.println("parent is " + parent);
			//	System.out.println("icValues is " + icValues.toString());			
			}
		}
		List<Entry<String, Double>> list = object.sortHashMapByValue(icValues);
		return (list.isEmpty()) ? parent : list.get(0).getKey();
	}
	
	//gets the cate that is of a higher level
	//counts the times '_' occurs in each string
	public static String higherLevel(String s1, String s2){
		int count1 = s1.length() - s1.replace("_", "").length();
		int count2 = s2.length() - s2.replace("_", "").length();
		return ((count1 - count2) < 0 ) ? s1 : s2;
	}
	public static double calculateLin(String cate1, String cate2) throws Exception {
		String LCA = findLCA(cate1, cate2);
		double LCAic = getICForCate(LCA);
		double cate1IC = getICForCate(cate1);
		double cate2IC = getICForCate(cate2);
		return (2*LCAic)/(cate1IC + cate2IC);
	}
	
	public static double calculateJiang(String cate1, String cate2) throws Exception {
		String LCA = findLCA(cate1, cate2);
		double LCAic = getICForCate(LCA);
		double cate1IC = getICForCate(cate1);
		double cate2IC = getICForCate(cate2);
		return 1/(cate1IC + cate2IC - 2*LCAic +1);
	}
	
	//extend the similarity between categories to similarity between cases
	//use the average of the similarity scores between all possible combinations of categories for 2 cases
	public static double averageScore(String case1, String case2) throws Exception{
		//find out which categories each case has. AKA given a result set of a case, which columns have the value 1?
		double sum = 0;
		int countAnno = 0;
		MySQL sc = new MySQL();
		ResultSet rs1 = sc.getCaseByID(case1); 
		ResultSet rs2 = sc.getCaseByID(case2); 
	
		
		ResultSetMetaData meta1 = rs1.getMetaData();
		ResultSetMetaData meta2 = rs2.getMetaData();
		
		
	
		while(rs1.next() && rs2.next()){
			System.out.println("inside while loop....");

		
			//calculating scores for each cate pair
			for(int j = 3; j<= treeSize1+2; j++){	
				System.out.println("inside outer for loop....");

				String ColName2 = meta2.getColumnName(j);
				System.out.println("colname2 is " + ColName2);

				for(int i = 3; i<= treeSize1+2; i++){
					System.out.println("inside inner for loop....");

					String ColName1 = meta1.getColumnName(i);
					System.out.println("colname1 is " + ColName1);

					if(rs1.getString(ColName1).equals("1") && rs2.getString(ColName2).equals("1")){
						System.out.println("inside if statement comparing two cates ....");

						countAnno++;
						System.out.println("the combinations right now is "+ countAnno);
						//THE BIG FUNCTION TAKES CATEGORY NAMES WITHOUT 'C"
						sum+=calculateLin(ColName1.substring(1),ColName2.substring(1));	
	
					}
				}		
			}
		}
		rs1.close();
		rs2.close();
		System.out.println("the total combinations is "+ countAnno);
	
		
		return sum/countAnno;

		
	}
	
	public static double getSignificantFigure(double data, int SignificantNum) {
		double size = Math.pow(10, SignificantNum);		
		long l1 = Math.round(data * size);
		
		return l1 / size;
	}
}
