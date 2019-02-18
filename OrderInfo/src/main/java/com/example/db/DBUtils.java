package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
	
	public static final String CONNECTION_STRING = "jdbc:db2://dashdb-txn-sbox-yp-dal09-03.services.dal.bluemix.net:50000/BLUDB";
	public static Connection getDBConnection() throws Exception{
		
		 Class.forName("com.ibm.db2.jcc.DB2Driver");

		 DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
		 Connection con = DriverManager.getConnection(CONNECTION_STRING, "zxv30288", "h9hqhb9hvtc7nf+0");

		 return con;

	}
	
	
	public static List<Map<String, String>> executeQuery (String sql) throws Exception{
		
		Connection conn = null;
		
		Statement statement = null;
		
		ResultSet rs = null;
		List<Map<String, String>> returnData = new ArrayList<>();
		try {
			conn = getDBConnection();
			statement = conn.createStatement();
			System.out.println(" sql ***************"+ sql);
			rs = statement.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			Integer columnCount = metaData.getColumnCount();
			
			while (rs.next()) {
				Map<String, String> dataRow = new HashMap<>();
				for (int i=1; i<=columnCount; i++) {
					dataRow.put(metaData.getColumnName(i), rs.getString(i));
				}
				returnData.add(dataRow);
			}
			
		}catch(Exception ex) {
			
			throw ex;
		}
		finally {
			try {
				if(rs != null ) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnData;
	}
	
	

}
