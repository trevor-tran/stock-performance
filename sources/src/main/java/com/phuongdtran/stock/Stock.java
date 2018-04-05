package com.phuongdtran.stock;

import lombok.*;

/**
 * Three fields: <b>ticker, price, split</b> <br>
 * <b>ticker</b> is stock symbol such as <i>MSFT,GOOGL</i> <br>
 * <b>price</b> is the price of specific stock at a particular date <br>
 * <b>split</b> is stock split ratio.
 * @author PhuongTran
 *
 */
public class Stock {
	@Getter private String ticker;
	@Getter private double price;
	@Getter private double split;
	
	public Stock(String ticker, double price, double split){
		this.ticker = ticker;
		this.price = price;
		this.split = split;
	}

}
