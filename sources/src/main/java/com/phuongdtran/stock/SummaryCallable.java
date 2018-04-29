package com.phuongdtran.stock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class SummaryCallable implements Callable<Map<String,String>>{
	
	private Map<String,List<Stock>> data;
	
	@Override
	public Map<String, String> call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
