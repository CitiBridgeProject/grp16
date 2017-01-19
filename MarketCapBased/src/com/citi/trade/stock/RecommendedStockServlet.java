package com.citi.trade.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.citi.trade.constant.TradeApplicationConstants;

public class RecommendedStockServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("In RecommendedStockServlet");
		response.setContentType(TradeApplicationConstants.JSON_CONTENT_TYPE);

		String marketCap = (String) request.getParameter("selectedCap");
		System.out.println("marketCap:" + marketCap);
		JSONObject json = new StockRecommendation().getStockRecommendations(marketCap);
		//System.out.println("json response in RecommendedStockServlet:" + json);

		PrintWriter out = response.getWriter();
		out.println(json);
	}

}