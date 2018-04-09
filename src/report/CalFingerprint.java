package report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import contributing_factor.CF_structured;
import similarity_vs.CalculateInside_vs;
import user.DBBean;

public class CalFingerprint {	
	public static double weight_low = 0.5;
	public static String weight_fall = "1;1;1;1;1;1;2;1;1;1;2;1;1";
	public static double maxScore = 1.1539;	//average of weight_fall

	public static void main(String[] args) throws Exception {
		/**
		String fp = getFPofOneQuestion("fall_6", "10");
		System.out.println(fp);
		**/
		
		/**
		String fp = getWholeFP("fall", "UM100");
		System.out.println(fp);
		**/		
		
		/**
		 * watch the distribution of similarity scores
		 */		
		/**
		ArrayList<Double> allScores = new ArrayList<Double>();
		for(int i=1; i<=40; i++) {
			System.out.println("Processing MCPS" + String.valueOf(i));
			ArrayList<CaseForRank> SimiList = getSimilarityList("MCPS" + String.valueOf(i));
			for(int j=0; j<SimiList.size(); j++) {
				allScores.add(SimiList.get(j).score);
			}			
		}
		Collections.sort(allScores);
		Collections.reverse(allScores);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("C:\\Users\\hkang1\\Desktop\\AHRQ\\WEBMM\\similarity score distribution\\allScores.txt")));
		for(int i=0; i<allScores.size(); i++)
			dos.writeBytes(allScores.get(i) + "\r\n");
		dos.close();
		**/
		
		/**
		ArrayList<CaseForRank> SimiList = getSimilarityList("R2");

		for(int i=0; i<SimiList.size(); i++) {
			System.out.println(SimiList.get(i).id + "\t" + SimiList.get(i).score);
		}
		**/
		
		/**
		DBBean dbb = new DBBean();
		ResultSet rs = dbb.query("select * from report_general where uni_id='MCPS300'");
		rs.next();
		double score = calSimialrityBetweenTwoCases_vs_weight("MCPS1", rs, "fall");
		System.out.println(score);
		rs.close();
		dbb.disConnect();
		**/
		
		/**
		DBBean dbb = new DBBean();
		CalFingerprint cf = new CalFingerprint();
		cf.getSimilarityList_cf(dbb, "UM76005");
		dbb.disConnect();
		**/
		
		System.out.println(commonFactors("1_3||13||10_2||5", "1_3||4_2_5||4_2_7||5_4||13_1"));
	}	
	
	public static String getFPofOneQuestion(DBBean dbb, String question, String answer) throws SQLException {		
		ResultSet rs = dbb.query("select * from questions where qid='" + question + "'");
		rs.next();
		
		//how many options of this question
		String qnum = rs.getString("anum");
		
		rs.close();		
		
		//---get answer index
		String[] eachAns;
		ArrayList<String> index = new ArrayList<String>();
		if(answer != null) {
			eachAns = answer.split("\\|\\|");		
			for(int i=0; i<eachAns.length; i++) {			
				String[] content = eachAns[i].split("\\$\\$");
				index.add(content[0]);
			}
		}
		//----
		
		//---get fingerprint
		String fp = "";		
		for(int i=0; i<Integer.valueOf(qnum); i++) {
			if(index.contains(String.valueOf(i)))				
				fp += "1";
			else
				fp += "0";
		}
		//-----

		return fp;
	}
	
	public static String getFPofOneQuestion_weight(DBBean dbb, String uni_id, String type, String question) throws SQLException {
		ResultSet rs = dbb.query("select * from report_" + type + " where uni_id='" + uni_id  + "'");
		
		rs.next();		
		String answer = rs.getString(question);		
		rs.close();
		
		String res = getFPofOneQuestion(dbb, question, answer);
		
		return res;
	}
	
	public static String getWholeFP(DBBean dbb, String form, String uni_id) throws SQLException {
		ResultSet rs;
		String sql = "";
		int herfNum = 0;
		int pifNum = 0;
		int sirNum = 0;
		ArrayList<String> herfQ = new ArrayList<String>();
		ArrayList<String> pifQ = new ArrayList<String>();
		ArrayList<String> sirQ = new ArrayList<String>();
		
		//----get question nums
		if(form.equals("general")) {
			rs = dbb.query("select * from questions where qid like '%herf%'");
			while(rs.next()) {
				herfQ.add(rs.getString("qid"));
				herfNum ++;
			}
			rs.close();
			
			rs = dbb.query("select * from questions where qid like '%pif%'");
			while(rs.next()) {
				pifQ.add(rs.getString("qid"));
				pifNum ++;
			}
			rs.close();
			
			rs = dbb.query("select * from questions where qid like '%sir%'");
			while(rs.next()) {
				sirQ.add(rs.getString("qid"));
				sirNum ++;
			}
			rs.close();
			
			sql = "select * from questions where qid like '%herf%' OR qid like '%pif%' OR qid like '%sir%'";
			
		}
		else
			sql = "select * from questions where qid like '%" + form + "%'";
		
		rs = dbb.query(sql);
		int num = 0;
		while(rs.next())
			num ++;
		
		rs.close();
		//-----
		
		//----fingerprint
		rs = dbb.query("select * from report_" + form + " where uni_id='" + uni_id + "'");
		rs.next();
		String fp = "";
		
		if(form.equals("general")) {
			for(int i=0; i<herfNum; i++)
				fp += getFPofOneQuestion(dbb, herfQ.get(i), rs.getString(herfQ.get(i)));
			for(int i=0; i<pifNum; i++)
				fp += getFPofOneQuestion(dbb, pifQ.get(i), rs.getString(pifQ.get(i)));
			for(int i=0; i<sirNum; i++)
				fp += getFPofOneQuestion(dbb, sirQ.get(i), rs.getString(sirQ.get(i)));
		}else {
			for(int i=0; i<num; i++)
				fp += getFPofOneQuestion(dbb, form + "_" + (i+1), rs.getString(form + "_" + (i+1)));
		}
		
		rs.close();		
		//----
		
		return fp;			
	}
	
	public ArrayList<CaseForRank> getSimilarityList(DBBean dbb, String uni_id) throws SQLException {
		ArrayList<CaseForRank> rankList = new ArrayList<CaseForRank>();
		
		ResultSet rs = dbb.query("select * from report_general where uni_id='" + uni_id + "'");
		rs.next();
		String form = rs.getString("formn"); 
		
		rs = dbb.query("select * from report_general where formn='" + form + "' AND rstatus='2'");
		
		while(rs.next()) {
			String thisid = rs.getString("uni_id");
			String thisname = rs.getString("rname");
			if(!thisid.equals(uni_id)) {				
				double score = calSimialrityBetweenTwoCases_vs_weight(dbb, uni_id, rs, form);
				CaseForRank cfr = new CaseForRank(thisid, thisname, score);
				rankList.add(cfr);
			}
		}
		rs.close();
		
		Collections.sort(rankList);
		
		return rankList;
	}
	
	public ArrayList<CaseForRank> getSimilarityList_cf(DBBean dbb, String uni_id) throws Exception {
		ArrayList<CaseForRank> rankList = new ArrayList<CaseForRank>();		
		
		ResultSet rs = dbb.query("select * from report_general where uni_id='" + uni_id + "'");
		rs.next();
		String form = rs.getString("formn");		
		
		rs = dbb.query("select * from report_general where formn='" + form + "' AND rstatus='2' AND des!='NA'");
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();		
		
		while(rs.next()) {
			if(!rs.getString("uni_id").equals(uni_id)) {
				ids.add(rs.getString("uni_id"));
				names.add(rs.getString("rname"));
			}
		}
		
		rs = dbb.query("select * from report_" + form + " where uni_id='" + uni_id + "'");
		rs.next();
		
		String fingerprint1 = "";
		if(rs.getString("fingerprint_unstr").equals("NA"))
			fingerprint1 = rs.getString("fingerprint_str");
		else
			fingerprint1 = rs.getString("fingerprint_unstr");
		
		for(int i=0; i<ids.size(); i++) {
			rs = dbb.query("select * from report_" + form + " where uni_id='" + ids.get(i) + "'");
			rs.next();
			String fingerprint2 = rs.getString("fingerprint_unstr");
			double score = CalculateInside_vs.getScore_vs(fingerprint1, fingerprint2);
			CaseForRank cfr = new CaseForRank(ids.get(i), names.get(i), score);
			rankList.add(cfr);
		}		
		
		rs.close();		
		
		Collections.sort(rankList);		
		
		return rankList;
	}
	
	public ArrayList<CaseForRank> getSimilarityList_cf_only(DBBean dbb, String queryCFs, String type) throws Exception {
		ArrayList<CaseForRank> rankList = new ArrayList<CaseForRank>();		
		
		ResultSet rs = dbb.query("select * from report_general where formn='" + type + "' AND rstatus='2' AND des!='NA'");
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();		
		
		while(rs.next()) {			
			ids.add(rs.getString("uni_id"));
			names.add(rs.getString("rname"));			
		}		
		
		String fingerprint1 = CF_structured.CFsToFingerprint(dbb, queryCFs);
		
		for(int i=0; i<ids.size(); i++) {
			rs = dbb.query("select * from report_" + type + " where uni_id='" + ids.get(i) + "'");
			rs.next();
			String fingerprint2 = rs.getString("fingerprint_unstr");
			double score = CalculateInside_vs.getScore_vs(fingerprint1, fingerprint2);
			//add common factor numbers to the score
			score += commonFactors(queryCFs, rs.getString("cf_unstr"));
			
			CaseForRank cfr = new CaseForRank(ids.get(i), names.get(i), score);
			rankList.add(cfr);
		}		
		
		rs.close();		
		
		Collections.sort(rankList);		
		
		return rankList;
	}
	
	public static double commonFactors(String queryCF, String databaseCF) {
		double res = 0.0;
		String[] s1 = queryCF.split("\\|\\|");		
		String[] s2 = databaseCF.split("\\|\\|");
		
		for(int i=0; i<s1.length; i++) {
			for(int j=0; j<s2.length; j++) {
				if(s2[j].startsWith(s1[i] + "_") || s2[j].equals(s1[i]))
					res += 1.0;
			}
		}
		
		return res;
	}
	
	public static double calSimialrityBetweenTwoCases_vs(DBBean dbb, String uni_id1, ResultSet case2, String form) throws SQLException {
		String uni_id2 = case2.getString("uni_id");
					
		ResultSet rs1 = dbb.query("select * from report_" + form + " where uni_id='" + uni_id1 + "'");
		ResultSet rs2 = dbb.query("select * from report_" + form + " where uni_id='" + uni_id2 + "'");
		ResultSet questions = dbb.query("select * from questions where qid like '%" + form + "%'");
		
		rs1.next();
		rs2.next();
		
		int questionNum = 0;
		double score_sum = 0;
		double score_this = 0;		
		
		while(questions.next()) {
			String q = questions.getString("qid");
			String ans1 = rs1.getString(q);
			String ans2 = rs2.getString(q);
			
			if(ans1 == null && ans2 == null) {
				score_this = weight_low;
			}else if(ans1 == null || ans2 == null) {
				score_this = 0;
			}else if(ans1.equals(ans2)) {
				if(ans1.contains("||") || ans1.contains("$$")) {						
					score_this = 1;
				}else {
					String[] allAns = questions.getString("acontent").split("\\|\\|");
					if(allAns[Integer.valueOf(ans1)].contains("Unknown"))
						score_this = weight_low;
					else
						score_this = 1;							
				}
			}else {
				score_this = getVS(rs1.getString(q + "_f"), rs2.getString(q + "_f"));
			}				
			
			//System.out.println(q + " " + score_this);
			
			score_sum += score_this;
			
			questionNum ++;
		}			
		
		questions.close();
		rs1.close();
		rs2.close();	
		
		return score_sum / (double)questionNum;
		
		/*
		 * 
		DBBean dbb = new DBBean();
		ResultSet rs;
		String sql = "";
		
		String f_case1 = "";
		String f_case2 = "";
				 
		//----get f_general for uni_id1
		sql = "select * from report_general where uni_id='" + uni_id1 + "'";
		rs = dbb.query(sql);
		rs.next();
		f_case1 = rs.getString("herf_2_f");
		//---
		
		//---get f_general for case 2
		String uni_id2 = case2.getString("uni_id");
		f_case2 = case2.getString("herf_2_f");
		//---

		
		if(!form.equals("NA")) {			
			sql = "select * from report_" + form + " where uni_id='" + uni_id1 + "'";
			rs = dbb.query(sql);
			rs.next();
			f_case1 += rs.getString("f_" + form);
			
			sql = "select * from report_" + form + " where uni_id='" + uni_id2 + "'";
			rs = dbb.query(sql);
			rs.next();
			f_case2 += rs.getString("f_" + form);			
		}
		
		rs.close();
		
		return getVS(f_case1, f_case2);
		*/
	}
	
	//compare each question separately
	public static double calSimialrityBetweenTwoCases_vs_weight(DBBean dbb, String uni_id1, ResultSet case2, String form) throws SQLException {
		if(form.equals("fall")) {
			String uni_id2 = case2.getString("uni_id");
						
			ResultSet rs1 = dbb.query("select * from report_fall where uni_id='" + uni_id1 + "'");
			ResultSet rs2 = dbb.query("select * from report_fall where uni_id='" + uni_id2 + "'");
			ResultSet questions = dbb.query("select * from questions where qid like '%fall%'");
			
			rs1.next();
			rs2.next();
			
			int questionNum = 0;
			double score_sum = 0;
			double score_this = 0;
			
			//-----get the weights
			String[] weights_string = weight_fall.split(";");
			ArrayList<Double> weights = new ArrayList<Double>();
			for(int i=0; i<weights_string.length; i++)
				weights.add(Double.valueOf(weights_string[i]));			
			//------
			
			while(questions.next()) {
				String q = questions.getString("qid");
				String ans1 = rs1.getString(q);
				String ans2 = rs2.getString(q);
				
				if(ans1 == null && ans2 == null) {
					score_this = weight_low;
				}else if(ans1 == null || ans2 == null) {
					score_this = 0;
				}else if(ans1.equals(ans2)) {
					if(ans1.contains("||") || ans1.contains("$$")) {						
						score_this = weights.get(questionNum);
					}else {
						String[] allAns = questions.getString("acontent").split("\\|\\|");
						if(allAns[Integer.valueOf(ans1)].contains("Unknown"))
							score_this = weight_low * weights.get(questionNum);
						else
							score_this = weights.get(questionNum);							
					}
				}else {
					score_this = weights.get(questionNum) * getVS(rs1.getString(q + "_f"), rs2.getString(q + "_f"));
				}				
				
				//System.out.println(q + " " + score_this);
				
				score_sum += score_this;
				
				questionNum ++;
			}			
			
			questions.close();
			rs1.close();
			rs2.close();
			
			
			return (score_sum / (double)questionNum) / maxScore;
		}else
			return calSimialrityBetweenTwoCases_vs(dbb, uni_id1, case2, form);
		
		/***
		DBBean dbb = new DBBean();
		ResultSet rs;
		String sql = "";
		
		String f_case1 = "";
		String f_case2 = "";
		
		//----get f_general for uni_id1
		sql = "select * from report_general where uni_id='" + uni_id1 + "'";
		rs = dbb.query(sql);
		rs.next();
		f_case1 = rs.getString("f_general");
		//---
		
		//---get f_general for case 2
		String uni_id2 = case2.getString("uni_id");
		f_case2 = case2.getString("f_general");
		//---
		
		if(!form.equals("NA")) {
			String[] forms = form.split("\\|\\|");
		
			for(int i=0; i<forms.length; i++) {
				sql = "select * from report_" + forms[i] + " where uni_id='" + uni_id1 + "'";
				rs = dbb.query(sql);
				rs.next();
				f_case1 += rs.getString("f_" + forms[i]);
			
				sql = "select * from report_" + forms[i] + " where uni_id='" + uni_id2 + "'";
				rs = dbb.query(sql);
				rs.next();
				f_case2 += rs.getString("f_" + forms[i]);
			}
		}
		
		rs.close();
		
		return getVS(f_case1, f_case2);
		**/
	}
	
	public static double getVS(String f_case1, String f_case2) {
		Vector<Integer> v1 = new Vector<Integer>();	
		for(int i=0; i<f_case1.length(); i++)
			v1.add(Integer.valueOf(f_case1.substring(i, i + 1)));
		
		Vector<Integer> v2 = new Vector<Integer>();	
		for(int i=0; i<f_case2.length(); i++)
			v2.add(Integer.valueOf(f_case2.substring(i, i + 1)));
		
		return CalculateInside_vs.mulVector(v1, v2) / CalculateInside_vs.absMulVector(v1, v2);
	}
	
	public double getSignificantFigure(double data, int SignificantNum) {
		double size = Math.pow(10, SignificantNum);		
		long l1 = Math.round(data * size);
		
		return l1 / size;
	}
}
