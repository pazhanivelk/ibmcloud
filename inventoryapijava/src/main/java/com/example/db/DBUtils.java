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
			rs = statement.executeQuery("select max(order_id) ORDER_ID from ORDERDETAILS ");
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
	
	public String getNextOrderStatus(Integer orderId) throws Exception  {
		String str="";
				
		Connection conn = null;
		
		Statement statement = null;
		
		ResultSet rs = null;
		try {
			conn = getDBConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery("select ORDER_ID,PRODUCT_ID,PRODUCT_NAME,QUANTITY,ADDRESS,DELIVERY_DATE,PHONENUMBER from ORDERDETAILS where order_id='" + orderId+"'" );
			if (rs.next()) {
				str = "Order ID : "  +  rs.getString("ORDER_ID") + "\n";
				str =  str +  "Product ID: " + rs.getString("product_id") + "\n";
				str =  str +  "Product Name: " +  rs.getString("PRODUCT_NAME")+ "\n";
				str =  str +  "QUANTITY :" + rs.getString("QUANTITY") + "\n";
				str =  str +  "Address :" +  rs.getString("ADDRESS") + "\n";
				str =  str +  "DELIVERY_DATE: " + rs.getString("DELIVERY_DATE")+"\n";
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
		return str;
	}
	
	public void insertOrderData(Map<String, String> orderData) throws Exception  {
		
		String sql = "insert into ORDERDETAILS(%s) values (%s)";
		
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
		
		String sql = "update ORDERDETAILS set ";
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
	
	public void cancelOrderData(Map<String, Object> orderData) throws Exception  {
		
		Integer orderId = ((Double)orderData.get("order_id")).intValue();
		String sql = "update ORDERDETAILS set ORDER_STATUS = 'Cancelled'";
		sql = sql + " where order_id = '"+orderId +"' "; 
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
