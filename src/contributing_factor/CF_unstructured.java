package contributing_factor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import nlp.RegularExpression;
import user.DBBean;

public class CF_unstructured {
	public static void main(String[] args) throws Exception {
		DBBean dbb = new DBBean();
		String CFs = getCFs_oneLineString(dbb, "brain surgery. patient reports.");
		
		System.out.println(CFs);
		dbb.disConnect();
	}

	public static String getCFs_oneLineString(DBBean dbb, String text) throws Exception {
		RegularExpression re = new RegularExpression();				
		HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs = re.annotateCF(dbb, text);		
		ArrayList<String[]> term = re.getSortedCF(dbb, cfs);
		String CFs = "NA";
		
		if(term.size() > 0) {
			CFs = term.get(0)[0];
			
			for(int i=1; i<term.size(); i++)
				CFs += "||" + term.get(i)[0];
		}
		
		return CFs;
	}
	
	public static int getLevel(String CFID) {
		return CFID.split("_").length - 1;
	}
	
	public static boolean isSon(String son, String father) {
		if(father.equals("NA"))
			return false;
		int sonSize = son.split("_").length;
		int fatherSize = father.split("_").length;
		return sonSize > fatherSize ;
	}
	
	public static int countLevelDiff(String front, String behind) {
		int frontLevel = front.split("_").length - 1;
		if(behind.equals("NA"))
			return frontLevel;
		int behindLevel = behind.split("_").length - 1;
		return frontLevel - behindLevel;
	}
	
	public static String polishText(String text) {
		return text.replaceAll("\\'", "\\\\'").replaceAll("\\xE9", "e");
	}
	
}
