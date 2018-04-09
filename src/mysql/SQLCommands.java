package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLCommands {

	public Connection conn;
	private Statement stmt;
	private String myName = "root";
	private String myPWD = "root";
	private String dbaddress = "localhost";
	private String DBDriver = "org.gjt.mm.mysql.Driver";
	private String dbname = "webmm";
	
	public SQLCommands() {
		conn = null;
		stmt = null;
	}
	
	public void disconnect() throws SQLException {
		try {
			if(this.stmt != null)
				this.stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(this.conn != null)
				this.conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void getConn() throws Exception {		
		try {
			Class.forName(DBDriver).newInstance();
			this.conn = DriverManager.getConnection("jdbc:mysql://" + dbaddress + ":3306/" + dbname + "?useUnicode=true&characterEncoding=UTF-8", myName, myPWD);
		}catch (ClassNotFoundException classnotfoundexception) {
			throw new Exception(classnotfoundexception.toString());
		}
		try {
			this.stmt = this.conn.createStatement();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//---used by similarity_vs
	
	//get certain caseID from table "casefinger"
	public ResultSet getCaseByID(String caseID) throws Exception {		
		String sql = "select * from casefinger where caseID=\"" + caseID + "\"";			
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 
	
	//browse the candidate in "casefinger"
	public ResultSet browse() throws Exception {		
		String sql = "select * from casefinger";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 	
	//-----------
	
	
	//---used by similarity_to
	
	//get certain db_id from table treeinfo
	public ResultSet getTreeBydb_id(String db_id) throws Exception {
		String sql = "select * from treeinfo where db_id=" + db_id;		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 
	
	//get certain cate from table treeinfo
	public ResultSet getTreeByCate(String cate) throws Exception {
		String sql = "select * from treeinfo where cate=\"" + cate + "\"";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 
	//-------------
	
	
	//---used by SearchWebMM
	
	//browse treeinfo
	public ResultSet browseAnnotation() throws Exception {
		String sql = "select * from treeinfo";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 
	
	//get cases with keyword annotated with 1
	public ResultSet getResultWithTheAnnotation(String anno) throws Exception {
		String sql = "select * from casefinger where C" + anno + "=\"1\"";		
		ResultSet rs = null;
		try {
			rs = this.stmt.executeQuery(sql);
		}catch(Exception e) {
			return rs;
		}
		return rs;
	}
	
	//get annotation with particular name
	public ResultSet getAnnotationWithName(String name) throws Exception {
		String sql = "select * from treeinfo where name=\"" + name + "\"";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	}
	
	//get caseInfo with caseID
	public ResultSet getCaseInfoWithID(String ID) throws Exception {
		String sql = "select * from caseinfo where caseID=\"" + ID + "\"";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	}
	
	//get perspectiveInfo with perspectiveID
	public ResultSet getPerspectiveInfoWithID(String ID) throws Exception {
		String sql = "select * from persinfo where persID=\"" + ID + "\"";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	}
	
	
	//--------------
	
	
	
	//---used by test
	
	//get certain caseID from table "matrix_xx_x"	
	public ResultSet getCaseSimilarity(String caseID, String method, String type) throws Exception {
		String sql = "select * from matrix_" + method + "_" + type + " where caseID=\"" + caseID + "\"";		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 
	
	public ResultSet getCaseNum(String method, String type) throws Exception {
		String sql = "select * from matrix_" + method + "_" + type;		
		ResultSet rs = this.stmt.executeQuery(sql);
		return rs;
	} 
	
}