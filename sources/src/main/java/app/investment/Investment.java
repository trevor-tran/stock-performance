package app.investment;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class Investment {
	@Getter @Setter static long investment;
	@Getter @Setter static String startDate;
	@Getter @Setter static String endDate;
	@Getter @Setter static Set<String> symbolSet;
	//@Getter @Setter double numberOfShares;
}
