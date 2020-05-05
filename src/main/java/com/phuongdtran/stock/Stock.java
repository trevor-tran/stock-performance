package com.phuongdtran.stock;


import lombok.Getter;

/**
 * Three fields: <b>ticker, price, split</b> <br>
 * <b>ticker</b> is stock symbol such as <i>MSFT,GOOGL</i> <br>
 * <b>price</b> is the price of specific stock at a particular date <br>
 * <b>split</b> is stock split ratio.
 * @author PhuongTran
 *
 */
public class Stock {
	@Getter
	private String symbol;
	@Getter
	private String date;
	@Getter
	private double price;
	@Getter
	private double split;
	@Getter
	private double dividend;
	
	public Stock(String symbol, String date, double price, double split, double dividend){
		this.symbol = symbol;
		this.date = date;
		this.price = price;
		this.split = split;
		this.dividend = dividend;
	}
}
