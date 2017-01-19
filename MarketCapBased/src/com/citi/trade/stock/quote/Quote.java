package com.citi.trade.stock.quote;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Quote {
	
	@JsonProperty("Symbol")
	private String symbol;
	
	@JsonProperty("Date")
	private String date;
	
	@JsonProperty("Open")
	private String open;

	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

}
