package app.investment;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvestmentController {
	
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static Investment getInvestment(int userId){
		try(InvestmentDao investment = new InvestmentDao()){
			return investment.getInvestment(userId); 
		}catch(SQLException ex){
			logger.error(ex.getMessage());
		}catch(Exception ex){
			logger.error("getInvestment() Exception:" + ex.getMessage());
		}
		return null;
	}
}
