package com.phuongdtran.investment;

import java.lang.invoke.MethodHandles;
import static com.phuongdtran.util.RequestUtil.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.phuongdtran.stock.StockDao;

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

	public static void updateInvestment (Request request) {
		Gson gson = new GsonBuilder().create();
		JsonObject json = gson.fromJson(request.body(), JsonObject.class);
		long budget = json.get("budget").getAsLong();
		String startDate = json.get("startdate").getAsString();
		String endDate = json.get("enddate").getAsString();
		Set<String> symbol = jsonArrayToSet( json.get("symbols").getAsJsonArray());
		
		int userId = Integer.parseInt(getSessionUserId(request));
		Investment prev = getSessionInvestment(request);
		
		InvestmentDao investmentDao = null;
		try{
			investmentDao = new InvestmentDao();
			if(isDiffFromPrev(request)) {
				prev.setBudget(budget);
				prev.setStartDate(startDate);
				prev.setEndDate(endDate);
				investmentDao.update(userId, budget, startDate, endDate);
			}
			if( symbol.size()==1 && !prev.getSymbols().containsAll(symbol)){
				prev.addSymbol(symbol);
				investmentDao.addSymbol(userId, symbol.iterator().next());
			}
		}catch(SQLException ex){
			logger.error("updateInvestment() failed." + ex.getMessage());
		}finally{
			investmentDao.close();
		}
	}
	
	/**
	 * compare some of fields between request body and Investment store as a session attribute
	 * @param request
	 * @return <i>true</i> if either budget,start date, end date differs<br>
	 * <i>false</i> if the same.
	 */
	public static boolean isDiffFromPrev(Request request){
		Investment prev = getSessionInvestment(request);
		
		Gson gson = new GsonBuilder().create();
		JsonObject json = gson.fromJson(request.body(), JsonObject.class);
		long budget = json.get("budget").getAsLong();
		String startDate = json.get("startdate").getAsString();
		String endDate = json.get("enddate").getAsString();
		
		if(!Objects.equals(prev.getStartDate(), startDate) || !Objects.equals(prev.getEndDate(), endDate) || prev.getBudget()!= budget) {
			return true;
		}
		return false;
	}

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
//			StockDao.remove(symbol);
			response.status(200);
		}catch(SQLException ex){
			logger.error("removeSymbol() failed." + ex.getMessage());
			response.status(301);
		}finally{
			investmentDao.close();
		}
		return response.status();
	};
	
	private static Set<String> jsonArrayToSet(JsonArray jsonArray){
		Set<String> symbols = new HashSet<String>();
		for(int i=0; i<jsonArray.size(); i++){
			symbols.add(jsonArray.get(i).getAsString());
		}
		return symbols;
	}
}
