package com.citi.trade.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

public class AutheticationDAO {

	private static final String SELECT_USER_QUERY = "SELECT USER_ID,USERNAME,PASSWORD FROM LOGIN WHERE USERNAME=? AND PASSWORD=?";

	public JSONObject authenticateUser(String userName, String password) {

		Connection con = null;
		JSONObject userData = null;
		try {
			con = DBConnection.createConnection();
			PreparedStatement pst = con.prepareStatement(SELECT_USER_QUERY);
			pst.setString(1, userName);
			pst.setString(2, password);
			ResultSet rs = pst.executeQuery();

			String userId;
			if (null != rs) {
				while (rs.next()) {
					userId = rs.getString("user_id");
					System.out.println(userId);
					userData = new JSONObject();
					userData.put("userId", userId);
					
				}
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userData;
	}

}
