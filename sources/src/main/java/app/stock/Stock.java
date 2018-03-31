package app.stock;

import lombok.*;

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
