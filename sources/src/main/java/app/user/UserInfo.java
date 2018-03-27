package app.user;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
@Value // All fields are private and final. Getters (but not setters) are generated (https://projectlombok.org/features/Value.html)
public class UserInfo {
	BigDecimal investment;
	Date startDate;
	Date endDate;
	Set<String> stockSymbol;
	double numberOfShares;
}
