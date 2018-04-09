package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBBean {
	Connection conn = null;
	Statement stat = null;
	ResultSet rs = null;
	private String myName = "root";
	private String myPWD = "root";
	private String dbaddress = "localhost";
	private String DBDriver = "com.mysql.jdbc.Driver";
	private String dbname = "common_formats";
	
	public DBBean() {
		try {
			conn = this.getConn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disConnect() throws SQLException {
		try {
			if(this.rs != null)
				this.rs.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(this.stat != null)
				this.stat.close();
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
	
	public Connection getConn() throws Exception {
		if(conn != null) {
			return conn;
		}
		try {
			Class.forName(DBDriver).newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://" + dbaddress + ":3306/" + dbname + "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false", myName, myPWD);
		}catch (ClassNotFoundException classnotfoundexception) {
			throw new Exception(classnotfoundexception.toString());
		}
		return conn;
	}
	
	public ResultSet query(String sql) {
		try {
			//conn = getConn();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public void delete(String sql) {
		try {
			conn = getConn();
			stat = conn.createStatement();
			stat.executeUpdate(sql);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if(rs != null)
				rs.close();
			if(stat != null)
				stat.close();
			if(conn != null)
				conn.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
