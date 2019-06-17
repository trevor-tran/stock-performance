package com.phuongdtran.stock;


/**
 * Three fields: <b>ticker, price, split</b> <br>
 * <b>ticker</b> is stock symbol such as <i>MSFT,GOOGL</i> <br>
 * <b>price</b> is the price of specific stock at a particular date <br>
 * <b>split</b> is stock split ratio.
 * @author PhuongTran
 *
 */
public class Stock {
	private String ticker;
	private double price;
	private double split;
	
	public Stock(String ticker, double price, double split){
		this.ticker = ticker;
		this.price = price;
		this.split = split;
	}

	public String getTicker() {
		return ticker;
	}

	public double getPrice() {
		return price;
	}

	public double getSplit() {
		return split;
	}
	
	

}
