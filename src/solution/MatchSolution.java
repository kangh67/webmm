package solution;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import report.ContinueAndEdit;
import report.NewCase;
import user.DBBean;

public class MatchSolution {
	public ArrayList<String> orderedCFs = new ArrayList<String>();
	public HashMap<String, String> cf_name = new HashMap<String, String>();
	public HashMap<String, String> sid_name = new HashMap<String, String>();
	
	public static void main(String[] args) throws Exception {
		MatchSolution ms = new MatchSolution();
		HashMap<String, ArrayList<String>> res = ms.getCFID_sids(new DBBean(), "UM75569", "fall");
		System.out.println(res);
		System.out.println(ms.cf_name);
		System.out.println(ms.sid_name);
		
		ms.copyAnswerSession(new DBBean(), "MCPS16_2919");
	}
	
	public HashMap<String, ArrayList<String>> getCFID_sids(DBBean dbb, String uni_id, String type) throws SQLException {
		HashMap<String, ArrayList<String>> cfid_sids = new HashMap<String, ArrayList<String>>();
		
		String[] cfs = null;
		ResultSet rs = dbb.query("select * from report_" + type + " where uni_id='" + uni_id + "'");
		rs.next();
		
		if(rs.getString("cf_unstr").equals("NA"))
			cfs = rs.getString("cf_str").split("\\|\\|");
		else
			cfs = rs.getString("cf_unstr").split("\\|\\|");
		
		for(int i=0; i<cfs.length; i++) {
			rs = dbb.query("select * from contributing_factors_list where CFID='" + cfs[i] + "'");
			rs.next();			
			if(!rs.getString("Solutions_" + type).equals("NA")) {
				String[] sids = rs.getString("Solutions_" + type).split(";");
				ArrayList<String> sidArray = new ArrayList<String>();
				for(int j=0; j<sids.length; j++) {
					sidArray.add(sids[j]);
					ResultSet rs_sid = dbb.query("select * from solutions_" + type + " where SID='" + sids[j] + "'");
					rs_sid.next();
					sid_name.put(sids[j], rs_sid.getString("Solution"));
					rs_sid.close();
				}
				cfid_sids.put(cfs[i], sidArray);
				cf_name.put(cfs[i], rs.getString("Term"));
				orderedCFs.add(cfs[i]);
			}
		}
		
		rs.close();
		
		return cfid_sids;
	}
	
	public ArrayList<Solution_entry> getEligibleSolutions(DBBean dbb, NewCase caseSession, String type) throws SQLException {
		ArrayList<Solution_entry> eligibleSolutions = new ArrayList<Solution_entry>();		
		ResultSet rs = dbb.query("select * from solutions_" + type);
		
		while(rs.next()) {
			if(judgeOneSolutionEligibleOrNot(rs.getString("Criteria"), rs.getString("logic"), caseSession)) {
				Solution_entry newSolution = new Solution_entry();
				
				newSolution.SID = rs.getString("SID");
				newSolution.Solution = rs.getString("Solution");
				newSolution.Category = rs.getString("Contributing_factor");
				newSolution.General_or_specific = rs.getString("General_or_specific");
				newSolution.Criteria = rs.getString("Criteria");
				newSolution.Logic = rs.getString("Logic");
				newSolution.Resource = rs.getString("Resource");
				newSolution.Solution_type = rs.getString("Solution_type");
				
				eligibleSolutions.add(newSolution);
			}				
		}
		
		rs.close();		
		
		return eligibleSolutions;
	}
	
	public ArrayList<Solution_entry> getEligibleSolutions_cf(DBBean dbb, Set<String> sids, String type) throws SQLException {
		ArrayList<Solution_entry> eligibleSolutions = new ArrayList<Solution_entry>();		
		ResultSet rs = dbb.query("select * from solutions_" + type);
		
		while(rs.next()) {
			if(sids.contains(rs.getString("SID"))) {
				Solution_entry newSolution = new Solution_entry();
				
				newSolution.SID = rs.getString("SID");
				newSolution.Solution = rs.getString("Solution");
				newSolution.Category = rs.getString("Contributing_factor");
				newSolution.General_or_specific = rs.getString("General_or_specific");
				newSolution.Criteria = rs.getString("Criteria");
				newSolution.Logic = rs.getString("Logic");
				newSolution.Resource = rs.getString("Resource");
				newSolution.Solution_type = rs.getString("Solution_type");
				newSolution.Contributing_factors = rs.getString("CFs");
				
				eligibleSolutions.add(newSolution);
			}				
		}
		
		rs.close();		
		
		return eligibleSolutions;
	}
	
	public HashMap<String, Integer> ContributingFactorStat(ArrayList<Solution_entry> solutions) {
		HashMap<String, Integer> cf = new HashMap<String, Integer>();
		
		for(int i=0; i<solutions.size(); i++) {
			if(cf.containsKey(solutions.get(i).Category))
				cf.put(solutions.get(i).Category, cf.get(solutions.get(i).Category) + 1);
			else
				cf.put(solutions.get(i).Category, 1);
		}
		
		return cf;
	}
	
	public HashMap<String, Integer> SolutionTypeStat(ArrayList<Solution_entry> solutions) {
		HashMap<String, Integer> st = new HashMap<String, Integer>();
		
		for(int i=0; i<solutions.size(); i++) {
			if(st.containsKey(solutions.get(i).Solution_type))
				st.put(solutions.get(i).Solution_type, st.get(solutions.get(i).Solution_type) + 1);
			else
				st.put(solutions.get(i).Solution_type, 1);
		}
		
		return st;
	}
	
	public NewCase copyAnswerSession(DBBean dbb, String uni_id) throws Exception {
		NewCase answers = new NewCase();
		ContinueAndEdit cae = new ContinueAndEdit();
		String sql = "select * from report_general where uni_id='" + uni_id + "'";
		ResultSet rs_report = dbb.query(sql);
		
		rs_report.next();
		
		//----basic info
		answers.uni_id = uni_id;
		answers.reporter = rs_report.getString("reporter");
		answers.caseName = rs_report.getString("rname");
		answers.initime = rs_report.getString("initime");
		answers.uptime = rs_report.getString("uptime");
		answers.description = rs_report.getString("des");
		answers.link = rs_report.getString("link");
		//------				
				
		//------general
		ArrayList<String> general = cae.getGeneralQuestions(dbb);
		//be sure general form has been finished
		if(!rs_report.getString("rstatus").equals("0"))	{
			answers.createRouteAndInfo("general");			
			for(int i=0; i<general.size(); i++) {
				if(rs_report.getString(general.get(i)) != null) {					
					answers.putRouteAndInfo("general", general.get(i), rs_report.getString(general.get(i)));
				}
			}	
			if(answers.route_gen.size() >= 1) {
				answers.general = true;
			}
			String ans = rs_report.getString("herf_7");
			answers.setForms(ans.split("\\|\\|"));
		}		
				
		//System.out.println(answers.route_gen);
		//System.out.println(answers.info_gen);
		//System.out.println(answers.general);		
		//--------				
				
		//-----other info
		String forms = rs_report.getString("formn");
		if(forms != null) {
			String[] eachform = forms.split("\\|\\|");
			for(int i=0; i<eachform.length; i++) {
				sql = "select * from report_" + eachform[i] + " where uni_id='" + rs_report.getString("uni_id") + "'";
				ResultSet rs_detail = dbb.query(sql);
						
				if(rs_detail.next()) {
					ArrayList<String> detail = cae.getDetailQuestions(dbb, eachform[i]);
					answers.createRouteAndInfo(eachform[i]);	
					for(int j=0; j<detail.size(); j++) {
						if(rs_detail.getString(detail.get(j)) != null) {
							answers.putRouteAndInfo(eachform[i], detail.get(j), rs_detail.getString(detail.get(j)));
						}
					}												
				}
						
				rs_detail.close();
			}
		}	
		//------
		rs_report.close();
		
		return answers;
	}	
	
	public String generateSolutionFileForDownload(DBBean dbb, ArrayList<Solution_entry> solutions, String absPath, String queryID) throws IOException, SQLException {
		String fileName = "solution_" + queryID + ".csv";
		String wholeName = absPath + "\\solutionFiles\\" + fileName;
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(wholeName)));		
		
		dos.writeBytes("Solution ID,Category,Solution Type,Resource,Link,Solution Content,Contributing Factors" + "\r\n");
		
		for(int i=0; i<solutions.size(); i++) {
			String[] multiResource = solutions.get(i).Resource.split("\\|\\|");
			String link = "NA";
			
			for(int j=0; j<multiResource.length; j++) {			
				String sql = "select * from resources where resource='" + multiResource[j] + "'";
				ResultSet rs = dbb.query(sql);
				if(rs.next()) {
					if(j==0)
						link = rs.getString("link");
					else
						link = link + "; " + rs.getString("link");
				}else {
					System.out.println("Cannot find \"" + multiResource[j] + "\"");
				}
				rs.close();
			}			
			
			String solutionType = solutions.get(i).Solution_type.replaceAll("0", "Principle").replaceAll("1", "Direct Action").replaceAll("2", "Patients").replaceAll("NA", "Others");
			dos.writeBytes(solutions.get(i).SID + "," + solutions.get(i).Category + "," + solutionType + ",\"" + solutions.get(i).Resource + "\"," + link + ",\"" + solutions.get(i).Solution + "\"" + solutions.get(i).Contributing_factors + "\"" + "\r\n");
		}
		
		dos.close();		
		
		return fileName;
	}
	
	/*
	 * Judge whether certain solution entry should be provided or not
	 */
	public static boolean judgeOneSolutionEligibleOrNot(String criteria, String logic, NewCase caseSession) throws SQLException {
		ArrayList<String> answerArray = translateAnswersToCriteriaFormat(caseSession);
		
		//General solutions
		if(logic.equals("NA"))
			return true;
		
		//Single specific solutions
		if(logic.equals("S")) 			
			return judgeSingle(answerArray, criteria);		
			
		//AND specific solutions
		if(logic.equals("A")) {
			String[] thisCriteria = criteria.split(" ");
			
			for(int i=0; i<thisCriteria.length; i++) 
				if(!judgeSingle(answerArray, thisCriteria[i]))
					return false;
			
			return true;
		}
		
		//OR specific solutions
		if(logic.equals("O")) {
			String[] thisCriteria = criteria.split(" ");
			
			for(int i=0; i<thisCriteria.length; i++) {
				if(judgeSingle(answerArray, thisCriteria[i]))
					return true;
			}
			
			return false;
		}
		//Complex situation a&b c d e means a&(b|c|d|e)
		if(logic.equals("C")) {
			String[] thisAND = criteria.split("&");		
			
			return judgeOneSolutionEligibleOrNot(thisAND[0], "S", caseSession) & judgeOneSolutionEligibleOrNot(thisAND[1], "O", caseSession);			
		}
		
		return false;
	}
	
	/*
	 * Given a case reporting session, return answer array
	 * e.g., [1_a, 2_c, 4_a, 5_f, 6_l, 7_b, 9_a, 9_f, 10_d, 10_f, 10_h, 10_m, 10_r, 11_a, 12_b, 13_c]
	 */
	public static ArrayList<String> translateAnswersToCriteriaFormat(NewCase caseSession) throws SQLException {
		ArrayList<String> criteriaFormat = new ArrayList<String>();
		
		ArrayList<String> avaForm = caseSession.showAvaForms();
		ArrayList<String> route = caseSession.getRoute(avaForm.get(0));
		HashMap<String, String> info = caseSession.getInfo(avaForm.get(0));			
		
		for(int i=0; i<route.size(); i++) {
			String[] thisInfo = info.get(route.get(i)).split("\\|\\|");
			for(int j=0; j<thisInfo.length; j++) {
				String[] thisAns = thisInfo[j].split("\\$\\$");
				String option = String.valueOf((char)(Integer.valueOf(thisAns[0]) + 97));
				criteriaFormat.add(route.get(i).replaceAll(avaForm.get(0) + "_", "") + "_" + option);
			}
		}	
		
		return criteriaFormat;
	}
	
	public static boolean judgeSingle(ArrayList<String> answers, String criteria) {
		if(criteria.contains("^")) {
			if(answers.contains(criteria.replaceAll("^", "")))
				return false;
			else
				return true;
		}else {
			if(answers.contains(criteria))
				return true;
			else
				return false;
		}	
	}
}
