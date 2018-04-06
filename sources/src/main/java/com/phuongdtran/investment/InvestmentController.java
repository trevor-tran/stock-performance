package com.phuongdtran.investment;

import java.lang.invoke.MethodHandles;
import static com.phuongdtran.util.RequestUtil.*;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Route;

public class InvestmentController {

	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static Investment getInvestment(int userId){
		InvestmentDao investmentDao = null;
		try{
			investmentDao = new InvestmentDao();
			return investmentDao.getInvestment(userId); 
		}catch(SQLException ex){
			logger.error("getInvestment() failed." + ex.getMessage());
		}finally{
			investmentDao.close();
		}
		return null;
	}

	public static Route updateInvestment = (Request request, Response response) ->{
		Investment inv = getSessionInvestment(request);
		int userId = Integer.parseInt( getSessionUserId(request));

		long budget = Long.parseLong(getQueryBudget(request));
		String startDate = getQueryStartDate(request);
		String endDate = getQueryEndDate(request);
		String symbol = getQuerySymbol(request);

		InvestmentDao investmentDao = null;
		try{
			investmentDao = new InvestmentDao();
			if(!Objects.equals(inv.getStartDate(), startDate) || !Objects.equals(inv.getEndDate(), endDate) || inv.getBudget()!= budget) {
				inv.setBudget(budget);
				inv.setStartDate(startDate);
				inv.setEndDate(endDate);
				investmentDao.update(userId, budget, startDate, endDate);
			}
			if( symbol!=null && !inv.getSymbols().contains(symbol)){
				inv.addSymbol(symbol);
				investmentDao.addSymbol(userId, symbol);
			}
			request.session().attribute("investment", inv);
			response.status(200);
		}catch(SQLException ex){
			logger.error("updateInvestment() failed." + ex.getMessage());
			response.status(301);
		}finally{
			investmentDao.close();
		}
		return response.status();
	};

	public static Route removeSymbol = (Request request, Response response) -> {
		int userId = Integer.parseInt(getSessionUserId(request));
		String symbol = getQuerySymbol(request);
		InvestmentDao investmentDao = null;
		try{
			investmentDao = new InvestmentDao();
			//remove symbol in database
			investmentDao.removeSymbol(userId,symbol);
			//remove symbol in session attribute
			getSessionInvestment(request).removeSymbol(symbol);
			response.status(200);
		}catch(SQLException ex){
			logger.error("removeSymbol() failed." + ex.getMessage());
			response.status(301);
		}finally{
			investmentDao.close();
		}
		return response.status();
	};
}
