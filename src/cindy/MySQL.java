package cindy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class MySQL{
	private Connection conn;
	private String myName = "root";
	private String myPWD = "root";
	private String dbaddress = "localhost";
	private String DBDriver = "org.gjt.mm.mysql.Driver";
	private String dbname = "webmm";
	
	public MySQL() {
		conn = null;
	}
	
	public Connection getConn() throws Exception {
		if(conn != null) {
			return conn;
		}
		try {
			Class.forName(DBDriver).newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://" + dbaddress + ":3306/" + dbname + "?useUnicode=true&characterEncoding=UTF-8", myName, myPWD);
		}catch (ClassNotFoundException classnotfoundexception) {
			throw new Exception(classnotfoundexception.toString());
		}
		return conn;
	}
	
	
	//returns a List of entries of the HashMap 
	public List<Entry<String, Double>> sortHashMapByValue(HashMap<String, Double> scores){
		Set<Entry<String, Double>> set = scores.entrySet();
        List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> entry1,Entry<String, Double> entry2 )
            {
        		//System.out.println("object 1 is currently...." +o1.toString());
        		//System.out.println("object 2 is currently...." +o2.toString());
            	Double value1 = entry1.getValue();
            	Double value2 = entry2.getValue();
                return value2.compareTo(value1);
            }
        } );
/*    //list is now sorted, printouts to double check
        for(Entry<String, Double> entry:list)
        	System.out.println(entry.getKey()+ " --- " + entry.getValue());*/
        return list;
	}
	
	//find the top n most similar cases in a list of entries
	public String findTopNCases(int n, List<Entry<String, Double>> list){
		//System.out.println(list);
		String topCases = "";
		for(int i = 0; i<n-1; i++)
		{
			Entry<String,Double> object = list.get(i);
			topCases += object.getKey()+", ";
		}
		topCases+=list.get(n-1).getKey()+".";
		return topCases;
		
	}
	
	
	//---used by similarity_vs
	//get certain caseID from table "casefinger"
	public ResultSet getCaseByID(String caseID) throws Exception {
		String sql = "select * from casefinger where caseID= " + caseID;
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 

	/*//get the desired caseID entered by the user from table "matrix_vs_1"
	public ResultSet getCaseByIDScanned() throws Exception {
		System.out.println("Enter the caseID here: ");
		Scanner scan = new Scanner(System.in);
		String caseID = scan.nextLine();
		scan.close();
		Connection con = this.getConn();
		Statement caseIDValue = con.createStatement();

		String sql = "select * from matrix_vs_1 where caseID = " + caseID;
		//System.out.println("command is " + sql);
		ResultSet rs = caseIDValue.executeQuery(sql);
		return rs;
	}*/
	
	//browse the candidate in "casefinger"
	public ResultSet browse() throws Exception {
		String sql = "select * from casefinger";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 	
	//-----------
	

	
	//get certain db_id from table treeinfo
	public ResultSet getTreeBydb_id(String db_id) throws Exception {
		String sql = "select * from treeinfo where db_id=\"" + db_id + "\"";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 
	//---used by similarity_ic
	
	//get certain cate from table treeinfo
	public ResultSet getTreeByCate(String cate) throws Exception {
		String sql = "select * from treeinfo where cate=\"" + cate + "\"";
		Connection con = this.getConn();
		System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 
	//-------------
	
	
	
	
	//---used by SearchWebMM
	
	//browse treeinfo
	public ResultSet browseAnnotation() throws Exception {
		String sql = "select * from treeinfo";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 
	
	//get cases with keyword annotated with 1
	public ResultSet getResultWithTheAnnotation(String anno) throws Exception {
		String sql = "select * from casefinger where C" + anno + "=\"1\"";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		}catch(Exception e) {
			return rs;
		}
		return rs;
	}
	
	//get annotation with particular name
	public ResultSet getAnnotationWithName(String name) throws Exception {
		String sql = "select * from treeinfo where name= " + name;
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	//get caseInfo with caseID
	public ResultSet getCaseInfoWithID(String ID) throws Exception {
		String sql = "select * from caseinfo where caseID=\"" + ID + "\"";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	//get perspectiveInfo with perspectiveID
	public ResultSet getPerspectiveInfoWithID(String ID) throws Exception {
		String sql = "select * from persinfo where persID=\"" + ID + "\"";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	
	//--------------
	
	
	
	//---used by test
	
	//get certain caseID from table "matrix_xx_x"	
	public ResultSet getCaseSimilarity(String caseID, String method, String type) throws Exception {
		String sql = "select * from matrix_" + method + "_" + type + " where caseID=\"" + caseID + "\"";
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 
	
	public ResultSet getCaseNum(String method, String type) throws Exception {
		String sql = "select * from matrix_" + method + "_" + type;
		Connection con = this.getConn();
		//System.out.println(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	} 
	
}