package nlp;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import user.DBBean;

public class RegularExpression {
	public static void main(String[] args) throws Exception {		
		DBBean dbb = new DBBean();
		RegularExpression re = new RegularExpression();
		String para = "brain surgery. patient reports.";
				
		HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs = re.annotateCF(dbb, para);
		
		System.out.println(cfs);		
		
		ArrayList<String[]> term = re.getSortedCF(dbb, cfs);
		for(int i=0; i<term.size(); i++)
			System.out.println(term.get(i)[0] + ": " + term.get(i)[1]);
		
		dbb.disConnect();
	}
	
	public HashMap<String, HashMap<Integer, HashSet<Integer>>> annotateCF(DBBean dbb, String paragraph) throws Exception {
		//Sentence Boundary Disambiguation
		HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs = new HashMap<String, HashMap<Integer, HashSet<Integer>>>();
		String[] sentences = Tokenization.sbd(paragraph, false);	//toLowercase = true
			
		//Apply inclusion rules
		String sql = "select * from contributing_factors_re where Inclusion='true'";
		ResultSet rs = dbb.query(sql);
		
		for(int i=0; i<sentences.length; i++) {				
			//traverse all inclusion factors for each sentence
			rs.beforeFirst();
			while(rs.next()) {
				HashSet<Integer> charIndex = applyOneRuleToOneSentence(sentences[i].toLowerCase(), rs.getString("Rule"));
				
				//index != null means this cf is eligible
				if(charIndex.size() > 0) {					
					String cfid = rs.getString("CFID");
					if(cfs.containsKey(cfid)) {	//this contributing factor exists in the paragraph
						if(cfs.get(cfid).containsKey(i))	//this contributing factor exists in this sentence
							cfs.get(cfid).get(i).addAll(charIndex);
						else	//new to this sentence
							cfs.get(cfid).put(i, charIndex);
					}else {	//new contributing factor
						HashMap<Integer, HashSet<Integer>> newMatch = new HashMap<Integer, HashSet<Integer>>();
						newMatch.put(i, charIndex);
						cfs.put(cfid, newMatch);
					}					
				}
			}				
		}	
		
		//Apply exclusion rules
		sql = "select * from contributing_factors_re where Inclusion='false'";
		rs = dbb.query(sql);
		
		for(int i=0; i<sentences.length; i++) {				
			//traverse all exclusion factors for each sentence
			rs.beforeFirst();
			while(rs.next()) {
				if(cfs.containsKey(rs.getString("CFID"))) {	//has this contributing factor
					if(cfs.get(rs.getString("CFID")).containsKey(i) && matchExclusion(sentences[i].toLowerCase(), rs.getString("Rule"))) 
						cfs.get(rs.getString("CFID")).remove(i);					
				}
			}				
		}	
		
		rs.close();		
		
		removeEmpty(cfs);
		removeConflicts(cfs);
		return cfs;
	}
	
	//Sort the identified CFs
	public ArrayList<String[]> getSortedCF(DBBean dbb, HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs) throws Exception {
		ArrayList<String[]> res = new ArrayList<String[]>();		
		
		//String sql = "select a.* from contributing_factors_re a right join (select max(db_id) db_id from contributing_factors_re group by Term) b on b.db_id = a.db_id where a.db_id is not null order by db_id";
		String sql = "select * from contributing_factors_list";
		ResultSet rs = dbb.query(sql);
		HashSet<String> uniqueID = new HashSet<String>();
			
		while(rs.next()) {			
			if(cfs.containsKey(rs.getString("CFID")) && !uniqueID.contains(rs.getString("CFID"))) {
				String[] term = new String[2];
				term[0] = rs.getString("CFID");
				term[1] = rs.getString("Term");
				uniqueID.add(term[0]);
				res.add(term);				
			}
		}
			
		rs.close();		
			
		return res;
	}
	
	//Sort the identified CFs, rewritten
		public ArrayList<String[]> getSortedCF(DBBean dbb, String cfs) throws Exception {
			ArrayList<String[]> res = new ArrayList<String[]>();		
			
			//String sql = "select a.* from contributing_factors_re a right join (select max(db_id) db_id from contributing_factors_re group by Term) b on b.db_id = a.db_id where a.db_id is not null order by db_id";
			String sql = "select * from contributing_factors_list";
			ResultSet rs = dbb.query(sql);
			String[] cf = cfs.split("\\|\\|");
			HashSet<String> uniqueCF = new HashSet<String>();
			for(int i=0; i<cf.length; i++)
				uniqueCF.add(cf[i]);
				
			while(rs.next()) {			
				if(uniqueCF.contains(rs.getString("CFID"))) {
					String[] term = new String[2];
					term[0] = rs.getString("CFID");
					term[1] = rs.getString("Term");
					res.add(term);				
				}
			}
				
			rs.close();		
				
			return res;
		}
	
	//return char index of the matched words
	public static HashSet<Integer> applyOneRuleToOneSentence(String sen, String rule) {
		HashSet<Integer> charIndex = new HashSet<Integer>();
		
		Pattern p = Pattern.compile(rule);
		Matcher m = p.matcher(sen);
		
		while(m.find()) {			
			for(int i=1; i<m.groupCount() + 1; i++) {
				int startIndex = sen.indexOf(m.group(i), m.start());				
				for(int j=0; j<m.group(i).length(); j++)
					charIndex.add(startIndex + j);
			}			         	
		}
		
		return charIndex;
	}
	
	//check if exclusion criteria were matched
	public static boolean matchExclusion(String text, String rule) {
		String[] singleRule = rule.toLowerCase().split("\\|");
		boolean res = false;
		
		for(int i=0; i<singleRule.length; i++) {
			Pattern p = Pattern.compile("\\b" + singleRule[i] + "\\b");
			Matcher m = p.matcher(text.toLowerCase());
			if(m.find()) {
				res = true;
				break;
			}
		}
		return res;
	}
	
	public static void removeEmpty(HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs) {
		//remove empty HashMap<String, HashMap<Integer, HashSet<Integer>>>
		ArrayList<String> toRemove = new ArrayList<String>();
		for(String cf : cfs.keySet()) {
			if(cfs.get(cf).size() < 1)
				toRemove.add(cf);
		}
		for(String tr : toRemove)
			cfs.remove(tr);		
	}
	
	//remove conflicts. e.g., 12_1 and 12_2 cannot appear together; factors in 13 have priorities
	public static void removeConflicts(HashMap<String, HashMap<Integer, HashSet<Integer>>> cfs) {
		//12_1 and 12_2 cannot appear together
		if(cfs.containsKey("12_1") && cfs.containsKey("12_2")) {
			cfs.remove("12_1");
			cfs.remove("12_2");
		}
			
		//factors in 13 have priorities
		if(cfs.containsKey("13_2")) {
			cfs.remove("13_1");
			cfs.remove("13_3");
			cfs.remove("13_4");
		}else if(cfs.containsKey("13_3")) {
			cfs.remove("13_1");			
			cfs.remove("13_4");			
		}else if(cfs.containsKey("13_4")) {
				
		}else if(cfs.containsKey("13_1")) {
				
		}else
			cfs.put("13_1", null);	//13_1 is default
	}
}
