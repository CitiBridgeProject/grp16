package com.citi.trade.stock;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.citi.trade.stock.quote.Quote;
import com.citi.trade.stock.quote.TradeHistoricalData;
import com.citi.trade.util.StockUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StockRecommendation {
	
	private static final String LARGE_CAP_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22HSBC%22,%22HCA%22,%22HAL%22,%22HPQ%22,%22IBM%22,%22MSFT%22,%22AAPL%22,%22GOOGL%22,%22INFY%22,%22HES%22)%20and%20startDate%20=%20%22{0}%22%20and%20endDate%20=%20%22{1}%22&format=json&env=store://datatables.org/alltableswithkeys";
	private static final String MID_CAP_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22VNTV%22,%22TSS%22,%22FFIN%22,%22HP%22,%22CVI%22,%22DPM%22,%22XL%22,%22KW%22,%22STL%22,%22VAC%22)%20and%20startDate%20=%20%22{0}%22%20and%20endDate%20=%20%22{1}%22&format=json&env=store://datatables.org/alltableswithkeys";
	private static final String SMALL_CAP_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22CASS%22,%22MNKD%22,%22SJW%22,%22PLAB%22,%22RDCM%22,%22SHLD%22,%22VDSI%22,%22TCS%22,%22IIN%22,%22RAFI%22)%20and%20startDate%20=%20%22{0}%22%20and%20endDate%20=%20%22{1}%22&format=json&env=store://datatables.org/alltableswithkeys";

	private static final int NUMBER_OF_DAYS_DIFFERENCE = -14;

	private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	public JSONObject getStockRecommendations(String marketCap) throws JSONException, IOException {

		Calendar cal = Calendar.getInstance();
		String todaysDate = dateFormatter.format(cal.getTime());
		System.out.println("Todays Date:" + todaysDate);

		cal.add(Calendar.DATE, NUMBER_OF_DAYS_DIFFERENCE);
		String historicDate = dateFormatter.format(cal.getTime());
		System.out.println("Start Date:" + historicDate);
		JSONObject jsonResponse = null;
		switch (marketCap) {
		case "SmallCap":
			jsonResponse = StockUtils.readJsonFromUrl(MessageFormat.format(SMALL_CAP_URL, historicDate, todaysDate));
			break;
		case "MiddleCap":
			jsonResponse = StockUtils.readJsonFromUrl(MessageFormat.format(MID_CAP_URL, historicDate, todaysDate));
			break;
		case "LargeCap":
			jsonResponse = StockUtils.readJsonFromUrl(MessageFormat.format(LARGE_CAP_URL, historicDate, todaysDate));
			break;
		}

		//System.out.println("json Response for all stocks in cap :" + jsonResponse);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = jsonResponse.toString();
		TradeHistoricalData tradeData = mapper.readValue(jsonInString, TradeHistoricalData.class);

		List<Quote> quoteList = tradeData.getQuery().getResults().getQuote();
		Map<String, List> tickerQuoteListMap = new HashMap<>();

		for (Quote quoteObj : quoteList) {
			System.out.println(quoteObj.getSymbol());
			List quoteListForTicker = tickerQuoteListMap.get(quoteObj.getSymbol());
			if (null == quoteListForTicker) {
				quoteListForTicker = new ArrayList<>();
			}
			quoteListForTicker.add(quoteObj);
			tickerQuoteListMap.put(quoteObj.getSymbol(), quoteListForTicker);
		}

		@SuppressWarnings("unchecked")
		Map quoteTickersMap = new TreeMap(new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				Double d1 = (Double) o1;
				Double d2 = (Double) o2;
				return d2.compareTo(d1);
			}

		});
		for (Map.Entry<String, List> entry : tickerQuoteListMap.entrySet()) {
			System.out.println();
			String ticker = entry.getKey();
			//System.out.println("ticker:" + ticker);
			List quotes = entry.getValue();
			Collections.reverse(quotes);
			double arrayForSlope[][] = new double[quotes.size()][2];
			int xAxisIndex = 0;

			for (Iterator iterator = quotes.iterator(); iterator.hasNext();) {

				Quote quoteObj = (Quote) iterator.next();
				double openValue = Double.parseDouble(quoteObj.getOpen());

				Date quotedDate = null;
				try {
					quotedDate = (Date) dateFormatter.parse(quoteObj.getDate());
				} catch (ParseException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(quotedDate.getTime());
				arrayForSlope[xAxisIndex][0] = Double.parseDouble(quotedDate.getTime() + "");
				arrayForSlope[xAxisIndex][1] = openValue;
				xAxisIndex++;

			}
			SimpleRegression simpleRegression = new SimpleRegression(true);

			simpleRegression.addData(arrayForSlope);
			double slopeOfTicker = simpleRegression.getSlope();
			//System.out.println("slope = " + slopeOfTicker);
			quoteTickersMap.put(slopeOfTicker, ticker);
			System.out.println(ticker + ":" + slopeOfTicker);
			//System.out.println("arrayForSlope::");
			for (int i = 0; i < arrayForSlope.length; i++) {
				System.out.println(arrayForSlope[i][0] + " , " + arrayForSlope[i][1]);
			}
		}
		
		System.out.println("Printing top 5 slopes:");
		JSONArray list = new JSONArray();
		JSONObject stockData = null;
		JSONObject stockListObject = new JSONObject();
		
		int counter = 0;
		
		for (Object key : quoteTickersMap.keySet()) {
			counter++;
			List quoteListForTicker = tickerQuoteListMap.get(quoteTickersMap.get(key));
			String stocksymbol = (String)quoteTickersMap.get(key);
			String stockprice = ((Quote) quoteListForTicker.get(quoteListForTicker.size() - 1)).getOpen();
			System.out.println((Double) key + "  ,   " + quoteTickersMap.get(key) + "  ,   "
					+ stockprice);
			
			stockData = new JSONObject();
			stockData.put("stocksymbol", stocksymbol);
			stockData.put("stockprice", stockprice);

			list.add(stockData);
			if (counter == 5) {
				break;
			}
		}
		stockListObject.put("stocks", list);
		
		return stockListObject;
	}
}
