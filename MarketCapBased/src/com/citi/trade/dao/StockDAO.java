package com.citi.trade.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class StockDAO {

	private static final String SELECT_SAVED_STOCK_QUERY = "Select * from  user_saved_stock where user_id=?";
	private static final String INSERT_STOCK_QUERY = "insert into user_saved_stock values(?,?,?,?)";

	public boolean saveStocks(String userId, JSONArray stockArray) {
		Connection con = null;
		for (Object o : stockArray) {
			System.out.println("Object from Array:" + o);
			JSONObject stockData = (JSONObject) o;
			String stockSymbol = (String) stockData.get("stockSymbol");
			System.out.println("stockSymbol:" + stockSymbol);
			String stockPrice = (String) stockData.get("stockPrice");
			System.out.println("stockPrice:" + stockPrice);
			int stockQuantity = Integer.parseInt((String) stockData.get("stockQuantity"));
			System.out.println("stockQuantity:" + stockQuantity);

			try {
				con = DBConnection.createConnection();
				PreparedStatement pst = con.prepareStatement(INSERT_STOCK_QUERY);
				pst.setString(1, userId);
				pst.setString(2, stockSymbol);
				pst.setString(3, stockPrice);
				pst.setInt(4, stockQuantity);
				pst.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;

	}

	public JSONObject getSavedStocks(String userId) {
		Connection con = null;
		JSONArray list = new JSONArray();
		JSONObject stockData = null;
		JSONObject stockListObject = new JSONObject();
		try {
			con = DBConnection.createConnection();
			PreparedStatement pst = con.prepareStatement(SELECT_SAVED_STOCK_QUERY);
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();

			if (null != rs) {
				while (rs.next()) {
					String stocksymbol = rs.getString("stocksymbol");
					String stockprice = rs.getString("stockprice");
					String stockquantity = rs.getString("stockquantity");
					System.out.println("stocksymbol:" + stocksymbol);
					System.out.println("stockprice:" + stockprice);
					System.out.println("stockquantity:" + stockquantity);
					
					stockData = new JSONObject();
					stockData.put("stocksymbol", stocksymbol);
					stockData.put("stockprice", stockprice);
					stockData.put("stockquantity", stockquantity);

					list.add(stockData);
					stockListObject.put("stocks", list);
				}
			}
			System.out.println("stockListObject in the DAO:" + stockListObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockListObject;

	}

}
