package com.phuongdtran.investment;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * four fields: <b>investment, startDate, endDate, symbols</b> <br>
 * <b>investment</b> is the amount of money invested or going to invest
 * from <b>startDate</b> to <b>endDate</b> <br>
 * <b>symbols</b> is a <i>Set</i> type
 * @author PhuongTran
 *
 */
public class Investment {
	@Getter @Setter private long budget;
	@Getter @Setter private String startDate;
	@Getter @Setter private String endDate;
	@Getter @Setter private Set<String> symbols;
	//@Getter @Setter double numberOfShares;
	
	public Investment(long budget, String startDate, String endDate, Set<String> symbols) {
		this.budget = budget;
		this.startDate = startDate;
		this.endDate = endDate;
		this.symbols = symbols;
	}
	
	/**
	 * add symbol to existing "symbols" Set
	 * @param symbol
	 */
	public void addSymbol(Set<String> symbol){
		this.symbols.addAll(symbol);
	}
	
	/**
	 * remove symbol from existing "symbols" Set
	 * @param symbol
	 */
	public void removeSymbol(String symbol){
		if (this.symbols.contains(symbol)){
			this.symbols.remove(symbol);
		}
	}
}
