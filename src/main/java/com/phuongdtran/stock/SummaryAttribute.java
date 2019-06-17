package com.phuongdtran.stock;

public class SummaryAttribute {
	//@Getter @Setter private String startDate;
	//@Getter @Setter private String endDate;
	private double startPrice;
	private double startQuantity;
	private double startBalance;
	private double endPrice;
	private double endQuantity;
	private double endBalance;
	public double getStartPrice() {
		return startPrice;
	}
	public void setStartPrice(double startPrice) {
		this.startPrice = startPrice;
	}
	public double getStartQuantity() {
		return startQuantity;
	}
	public void setStartQuantity(double startQuantity) {
		this.startQuantity = startQuantity;
	}
	public double getStartBalance() {
		return startBalance;
	}
	public void setStartBalance(double startBalance) {
		this.startBalance = startBalance;
	}
	public double getEndPrice() {
		return endPrice;
	}
	public void setEndPrice(double endPrice) {
		this.endPrice = endPrice;
	}
	public double getEndQuantity() {
		return endQuantity;
	}
	public void setEndQuantity(double endQuantity) {
		this.endQuantity = endQuantity;
	}
	public double getEndBalance() {
		return endBalance;
	}
	public void setEndBalance(double endBalance) {
		this.endBalance = endBalance;
	}

}
