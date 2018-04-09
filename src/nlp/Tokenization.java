package nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import user.DBBean;

public class Tokenization {
	public static String bin_sbd = "C:\\Users\\hkang1\\Google Drive\\jar\\apache-opennlp-1.8.2\\bin\\" + "en-sent.bin";
	public static String bin_token = "C:\\Users\\hkang1\\Google Drive\\jar\\apache-opennlp-1.8.2\\bin\\" + "en-token.bin";
	
	public static void main(String[] args) throws Exception {
		DBBean dbb = new DBBean();
		Tokenization to = new Tokenization();
		
		String paragraph = "Pt stated 'I am having a panic attack', pt assisted to sit in chair by copy machine in ER.";
		
		HashMap<String, HashSet<String>> cfs = to.annotateCF(dbb, paragraph);	
		
		System.out.println(cfs);		
		
	}
	
	//annotate a paragraph with contributing factors <contributing factor IDs, positions in paragraph>
	public HashMap<String, HashSet<String>> annotateCF(DBBean dbb, String paragraph) throws Exception {			
		String sql = "select * from contributing_factors";
		ResultSet rs = dbb.query(sql);
		
		//<contributing factor IDs, positions in paragraph>
		HashMap<String, HashSet<String>> cfs = new HashMap<String, HashSet<String>>();
		
		String[] sentences = sbd(paragraph, false);		
		
		for(int i=0; i<sentences.length; i++) {			
			//get tokens of each sentence
			List<String> tokens = new ArrayList<String>();
			tokens = Arrays.asList(doTokenization(sentences[i], true, true));	
			
			System.out.println("Tokens with stemming:");
			System.out.println(i + ": " + tokens);
			
			//traverse all factors for each sentence
			rs.beforeFirst();
			while(rs.next()) {
				HashSet<String> indexs = activeOneCF(tokens, rs.getString("Basic_rule").trim(), rs.getString("Restriction").trim(), i);
				
				//index != null means this cf is eligible
				if(indexs != null) {					
					String cfid = rs.getString("CFID");
					if(cfs.containsKey(cfid))
						cfs.get(cfid).addAll(indexs);
					else
						cfs.put(cfid, indexs);					
				}
			}				
		}
		
		removeConflicts(cfs);
		
		rs.close();
		
		return cfs;
	}
	
	//simply tokenize paragraph for presentation 
	public String[][] simpleTokenizeParagraph(String paragraph) {
		String[] sentences = sbd(paragraph, false);	//no toLowerCase
		String[][] res = new String[sentences.length][];
		
		System.out.println("Tokens without stemming:");
		
		for(int i=0; i<res.length; i++) {
			String[] tokens = doTokenization(sentences[i], false, false);	//no stemming, no toLowerCase
			res[i] = tokens;
			
			System.out.print(i + ": [" + tokens[0]);
			for(int j=1; j<tokens.length; j++) 
				System.out.print(", " + tokens[j]);
			System.out.println("]");
		}
		
		return res;
	}
	
	//Sort the identified CFs
	public ArrayList<String[]> getSortedCF(DBBean dbb, HashMap<String, HashSet<String>> cfs) throws Exception {
		ArrayList<String[]> res = new ArrayList<String[]>();		
			
		String sql = "select * from contributing_factors";
		ResultSet rs = dbb.query(sql);
		
		while(rs.next()) {
			if(cfs.containsKey(rs.getString("CFID"))) {
				String[] term = new String[2];
				term[0] = rs.getString("CFID");
				term[1] = rs.getString("Term");
				res.add(term);
			}
		}
		
		rs.close();		
		
		return res;
	}
	
	//Sentence Boundary Disambiguation
	public static String[] sbd(String paragraph, boolean toLower) {
		String[] sentences = null;
		try (InputStream is = new FileInputStream(new File(bin_sbd))) {
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME detector = new SentenceDetectorME(model);
			if(toLower)
				sentences = detector.sentDetect(paragraph.trim().toLowerCase());
			else
				sentences = detector.sentDetect(paragraph.trim());
		}catch (FileNotFoundException ex) {
			System.out.println("Failed to find en-sent.bin file. (Tokenization.sbd)");
		}catch (IOException ex) {
			System.out.println("IO Exception was detected. (Tokenization.sbd)");
		}
		return sentences;
	}
	
	//Split a sentence to words
	public static String[] doTokenization(String sentence, boolean stemmer, boolean toLower) {
		String[] tokens = null;
		try {
			InputStream modelInputStream = new FileInputStream(new File(bin_token));
			TokenizerModel model = new TokenizerModel(modelInputStream);
			Tokenizer tokenizer = new TokenizerME(model);			
			tokens = tokenizer.tokenize(sentence.trim());
		}catch(IOException ex) {
			System.out.println("IO Exception was detected. (Tokenization.doTokenization)");
		}
		//stemming
		if(toLower) {
			for(int i=0; i<tokens.length; i++)
				tokens[i] = tokens[i].toLowerCase();
		}
		
		if(stemmer)
			return stemming(tokens);
		
		return tokens;
	}
	
	//Stemming
	public static String[] stemming(String[] tokens) {
		PorterStemmer ps = new PorterStemmer();
		for(int i=0; i<tokens.length; i++) {
			tokens[i] = ps.stem(tokens[i]);
		}
		return tokens;
	}
	
	//if certain cf is eligible for certain sentence <index of recognized words>, return null if not eligible
	public static HashSet<String> activeOneCF(List<String> tokens, String rule, String restrict, Integer sentenceIndex) {
		//no rules
		if(rule.equals("NA"))
			return null;
		
		//has restriction
		if(!restrict.equals("NA")) {
			String[] singleRes = restrict.toLowerCase().split("\\|");
			
			//each restriction phrase
			for(String res : singleRes) {
				boolean matchRes = true;	//true: has this restriction phrase
				//a phrase may have multiple words
				String[] resCell = res.split("[ &]");
				//stemming 
				resCell = stemming(resCell);
				
				for(String cell : resCell) {
					if(!tokens.contains(cell))
						matchRes = false;
				}
				if(matchRes)
					return null;
			}
		}
		
		//result
		HashSet<String> indexs = new HashSet<String>();
		//this cf is eligible or not
		boolean match = false;
		
		String[] singleRule = rule.toLowerCase().split("\\|");
		for(int i=0; i<singleRule.length; i++) {
			String[] ruleCell = singleRule[i].split("[ &]");
			//stemming
			ruleCell = stemming(ruleCell);
			boolean matchRule = true;	//true: has this keyword
			HashSet<String> index_temp = new HashSet<String>();
			
			for(int j=0; j<ruleCell.length; j++) {
				boolean matchRuleCell = false;
				for(int k=0; k<tokens.size(); k++) {
					String[] slash = tokens.get(k).split("/");
					slash = stemming(slash);
					for(int t=0; t<slash.length; t++) {
						if(slash[t].equals(ruleCell[j])) {
							index_temp.add(String.valueOf(sentenceIndex) + "_" + String.valueOf(k));
							matchRuleCell = true;
							break;
						}
					}					
				}
				if(!matchRuleCell)
					matchRule = false;
			}
			
			if(matchRule) {
				indexs.addAll(index_temp);
				match = true;				
			}
		}			
		
		if(match)
			return indexs;
		else
			return null;
	}
	
	//remove conflicts. e.g., 12_1 and 12_2 cannot appear together; factors in 13 have priorities
	public static void removeConflicts(HashMap<String, HashSet<String>> cfs) {
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
