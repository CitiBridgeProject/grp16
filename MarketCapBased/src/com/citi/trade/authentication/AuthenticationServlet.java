package com.citi.trade.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.citi.trade.dao.AutheticationDAO;

public class AuthenticationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String lineContent;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();
		while ((lineContent = br.readLine()) != null) {
			sb.append(lineContent);
			System.out.println("sb:" + sb);
		}

		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(sb.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		String userName = (String) jsonObject.get("user");
		String password = (String) jsonObject.get("pass");

		AutheticationDAO autheticationDAO = new AutheticationDAO();
		JSONObject userData = autheticationDAO.authenticateUser(userName, password);
		System.out.println(userData);

		if (null != userData) {
			out.println(userData);
		} else {
			response.setStatus(400);
		}

	}
}