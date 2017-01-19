package com.citi.trade.stock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.citi.trade.constant.TradeApplicationConstants;
import com.citi.trade.dao.StockDAO;

public class FetchSavedStockServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("In FetchSavedStockServlet ");
		response.setContentType(TradeApplicationConstants.JSON_CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		String userId = request.getParameter("userId");
		System.out.println("userId:" + userId);
		StockDAO stockDAO = new StockDAO();
		JSONObject stockListObject = stockDAO.getSavedStocks(userId);
		if (null != stockListObject) {
			out.println(stockListObject);
		}
		//System.out.println("stockListObject in servlet:" + stockListObject);
	}
}
