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
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
	
	
	public static String generateSQL(JsonObject inputJson) {

		String returnVal = "";
		String columns = inputJson.get("fields").getAsString();
		String tableName = inputJson.get("tableName").getAsString();
		JsonElement whereConditionsObj = inputJson.get("wherecondition");
		returnVal = String.format("select %s from %s ",  columns, tableName);
		if (whereConditionsObj != null) {
			returnVal = returnVal + " where " + whereConditionsObj.getAsString();
		}
		return returnVal;

	}
	
	public String getNextOrdeID() throws Exception  {
		String order_id = "";
		Connection conn = null;
		
		Statement statement = null;
		
		ResultSet rs = null;
		try {
			conn = getDBConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery("select max(order_id) ORDER_ID from order_details ");
			if (rs.next()) {
				order_id = rs.getString("ORDER_ID");
				if (order_id == null || order_id.equals("")) {
					order_id = "100000";
				}
				else {
					order_id = String.valueOf(Integer.parseInt(order_id) + 1);
				}
			}
			else {
				return "100000";
			}
		}
		catch(Exception ex) {
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
		return order_id;
	}
	
	public String getNextOrderStatus(String orderId) throws Exception  {
		String orderStatus = "";
		Connection conn = null;
		
		Statement statement = null;
		
		ResultSet rs = null;
		try {
			conn = getDBConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery("select order_status  from order_details where order_id='" + orderId + "'");
			if (rs.next()) {
				orderStatus = rs.getString("ORDER_STATUS");
			}
		}
		catch(Exception ex) {
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
				e.printStackTrace();
			}
		}
		return orderStatus;
	}
	
	public void insertOrderData(Map<String, String> orderData) throws Exception  {
		
		String sql = "insert into order_details(%s) values (%s)";
		
		List<String> valuesList = new ArrayList<>();
		for (String value:orderData.values()) {
			valuesList.add("'"+value+"'" );
		}
		String values = String.join(",",  valuesList);
		
		String columnNames = String.join(",", orderData.keySet());
		
		sql = String.format(sql, columnNames, values);
		
		executeUpdateOrInsert(sql);
		
	}
	
	public void updateOrderData(Map<String, String> orderData) throws Exception  {
		
		String sql = "update order_details set ";
		boolean isFirst = true;
		for (String key:orderData.keySet()) {
			if (key.equals("order_id")) {
				continue;
			}
			if(!isFirst) {
				sql = sql + ",";
			}
			isFirst = false;
			sql = sql + key + " = '"+orderData.get(key)+"'";
			
		}
		sql = sql + " where order_id = '"+orderData.get("order_id") +"' "; 
		executeUpdateOrInsert(sql);
		
	}


	private void executeUpdateOrInsert(String sql) throws Exception  {
		Connection conn = null;
		
		
		
		Statement statement = null;
		
		try {
			conn = getDBConnection();
			statement = conn.createStatement();
			
		
			System.out.println(" sql ***************"+ sql);
			statement.execute(sql);
		}catch(Exception ex) {
			throw ex;
		}
		finally {
			try {
				
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
		
	}
	
	
	

}
