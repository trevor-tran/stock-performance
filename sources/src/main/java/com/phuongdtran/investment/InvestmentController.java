package com.phuongdtran.investment;

import java.lang.invoke.MethodHandles;
import static com.phuongdtran.util.RequestUtil.*;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Route;

public class InvestmentController {

	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static Investment getInvestment(int userId){
		InvestmentDao investment = null;
		try{
			investment = new InvestmentDao();
			return investment.getInvestment(userId); 
		}catch(SQLException ex){
			logger.error("getInvestment() failed." + ex.getStackTrace());
		}finally{
			investment.close();
		}
		return null;
	}
	
	public static Route updateInvestment = (Request request, Response response) ->{
		String budget = getQueryBudget(request);
		String startDate = getQueryStartDate(request);
		String endDate = getQueryEndDate(request);
		String symbol = getQuerySymbol(request);
		return null;
	};
}
