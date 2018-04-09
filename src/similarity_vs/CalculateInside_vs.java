package similarity_vs;

import java.util.Vector;

public class CalculateInside_vs {
	
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
	public static double getScore_vs(String finger1, String finger2) throws Exception {		
		//startsWith 0 means this case wasn't put in this category type.
		if(finger1.startsWith("0") || finger2.startsWith("0"))
			return 0;
		
		Vector<Integer> v1 = getVector(finger1);
		Vector<Integer> v2 = getVector(finger2);
		
		return mulVector(v1, v2) / absMulVector(v1, v2);
	}
	
	//turn String to Vector
	public static Vector<Integer> getVector(String text) {
		Vector<Integer> v = new Vector<Integer>();
		
		//Noted: i=1 because the first bit stands for the category, it should not be considered when calculate the similarity score
		for(int i=1; i<text.length(); i++)
			v.add(Integer.valueOf(text.substring(i, i + 1)));
			
		return v;
	}
	
	//calculate v1.v2
	public static Integer mulVector(Vector<Integer> v1, Vector<Integer> v2) {
		int result = 0;
		for(int i=0; i<v1.size(); i++)
			result += v1.get(i) * v2.get(i);		
		
		return result;
	}
	
	//calculate |v1||v2|
	public static double absMulVector(Vector<Integer> v1, Vector<Integer> v2) {
		double abs1 = 0;
		double abs2 = 0;
		
		for(int i=0; i<v1.size(); i++) {
			abs1 += Math.pow(v1.get(i), 2);
			abs2 += Math.pow(v2.get(i), 2);
		}
		
		return Math.sqrt(abs1) * Math.sqrt(abs2);
	}
}
