package database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import user.DBBean;

public class Test {
	public static void main(String[] args) throws Exception {
		updateReporter();
	}
	

	public static void updateReporter() throws Exception {
		DBBean dbb = new DBBean();		
		Connection con= dbb.getConn();
		PreparedStatement ps;		
		String sql = "update report_general set reporter='Missouri Center for Patient Safety' where reporter='University of Missouri'";
		
		ps = con.prepareStatement(sql);
		ps.executeUpdate();
		ps.close();
		con.close();
	}
}
