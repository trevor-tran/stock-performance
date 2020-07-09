package com.phuongdtran.stock;

import com.google.gson.Gson;
import com.phuongdtran.executor.IExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.neo4j.helpers.collection.MapUtil.map;

public class StockDao implements IStockDao {

	private IExecutor executor;
	private boolean isOpen;
	private Gson gson;

	public StockDao(IExecutor executor) {
		this.executor = executor;
		isOpen = false;
		gson = new Gson();
	}

	@Override
	public void open() throws SQLException {
		if (!isOpen) {
			executor.open();
			isOpen = true;
		}
	}

	@Override
	public void close() {
		if (isOpen) {
			executor.close();
			isOpen = false;
		}
	}

	@Override
	public boolean add(Stock stock) throws SQLException {
		connectionOpened();
		if (!existsSymbol(stock.getSymbol())) {
			addSymbol(stock.getSymbol());
		}
		addDate(stock.getSymbol(), stock.getDate(), stock.getPrice(), stock.getDividend(), stock.getSplit());

		return true;
	}

	private void setSymbolInfo(String symbol, String date) throws SQLException {
		connectionOpened();
		String query = " MATCH (s:Symbol {name: $symbol}) " +
						" SET " +
						" (CASE WHEN date($d) < s.ipo THEN s END).ipo = date($d), " +
						" (CASE WHEN date($d) > s.delisting THEN s END).delisting = date($d)";
		executor.create(query, map("symbol", symbol, "d", date));
	}

	/**
	 * return null if there is no info for the symbol
	 * @param symbol
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Ticker getSymbolInfo(String symbol) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		if (!existsSymbol(symbol)) {
			return null;
		}
		String query = "MATCH (s:Symbol) WHERE toUpper(s.name) = $symbol RETURN s.ipo AS ipo, s.delisting AS delisting";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("symbol", symbol));
		if (iterator.hasNext()) {
			Map<String, Object> tickerInfo = map(iterator.next());
			return new Ticker(symbol, tickerInfo.get("ipo").toString(), tickerInfo.get("delisting").toString());
		}
		return null;
	}

	@Override
	public List<Stock> get(String symbol, String startDate, String endDate) throws SQLException{
		connectionOpened();
		symbol = symbol.toUpperCase();
		String query = "MATCH (s:Symbol {name: $symbol})-[:HAS]->(d:Date) " +
				"WHERE date($startdate) <= d.date AND d.date <= date($enddate) " +
				"RETURN d.symbol AS symbol, d.date AS date, d.price AS price, d.dividend AS dividend, d.split AS split";
		Iterator<Map<String,Object>> iterator = executor.query(query,
				map("symbol", symbol, "startdate", startDate, "enddate", endDate));
		List<Stock> result = new ArrayList<>();
		while (iterator.hasNext()) {
			Map<String, Object> singleDay = map(iterator.next());
			Stock stock = new Stock(symbol,
					singleDay.get("date").toString(),
					Double.parseDouble(singleDay.get("price").toString()),
					Double.parseDouble(singleDay.get("split").toString()),
					Double.parseDouble(singleDay.get("dividend").toString()));
			result.add(stock);
		}
		return result;
	}

	private boolean addDate(String symbol, String date, double price, double dividend, double split) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		setSymbolInfo(symbol ,date);
		String query = "MATCH (s:Symbol) WHERE s.name = $symbol " +
					" MERGE (d:Date {date: date($date), symbol:$symbol, price: $price, dividend: $dividend, split: $split}) <-[:HAS]- (s)";
		return executor.create(query, map("symbol", symbol, "date", date, "price", price, "dividend", dividend, "split", split));
	}

	private boolean addSymbol(String symbol) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		String query = "CREATE (s:Symbol {name:$symbol, ipo: date(), delisting: date('1792-05-17')} )";
		return executor.create(query, map("symbol", symbol));
	}

	private boolean existsSymbol(String symbol) throws SQLException {
		connectionOpened();
		symbol = symbol.toUpperCase();
		String query = "MATCH (s:Symbol) WHERE s.name=$symbol RETURN s";
		Iterator<Map<String, Object>> iterator = executor.query(query, map("symbol", symbol));
		return iterator.hasNext();
	}



	/**
	 * Check whether open() has been called. If not, throw an exception.
	 * @throws SQLException
	 */
	private void connectionOpened() throws SQLException {
		if (!isOpen) {
			throw new SQLException("Not yet open a connection. Please call open()");
		}
	}
}
