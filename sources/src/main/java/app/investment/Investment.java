package app.investment;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class Investment {
	@Getter @Setter private long investment;
	@Getter @Setter private String startDate;
	@Getter @Setter private String endDate;
	@Getter @Setter private Set<String> symbols;
	//@Getter @Setter double numberOfShares;
	
	public Investment(long investment, String startDate, String endDate, Set<String> symbols) {
		this.investment = investment;
		this.startDate = startDate;
		this.endDate = endDate;
		this.symbols = symbols;
	}
}
