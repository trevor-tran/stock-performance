import React, { Component } from "react";
import update from 'react-addons-update';
import moment from 'moment';
import async from 'async';
import PubSub from 'pubsub-js';
import $ from 'jquery';

import Graph from "../presentational/Graph";

//set new state and save data a Session Storage
function setStateAndSave(_self,data){
	_self.setState({data},() => {
		sessionStorage.setItem('graph_data', JSON.stringify(_self.state.data));
	});
}

//return last symbol in symbols list
function getLastSymbol(symbols){
	return symbols[symbols.length-1];
}

//return e.g.  http://localhost:4567/home/?budget=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function buildUrl (budget, start, end, symbol) {
	var param="?budget=" + budget + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return '/home/' + param;
}

//merge current and new data. Return merged data
function mergeData( currentData, newData){
	//if newData contains multiple symbols data, return newData, no merging
	if(Object.keys(newData[0]).length > 2 ){//1st key must be "date", 2nd key is a symbol, so greater than 2 means more than one symbol
		return newData;
	}else{
		//why am I doing this? stock(s) data from Quandl missing point(s)
		//e.g AAPL,COST data from Quandl misses one data point on 2017-08-07
		var data = [];
		var shorterLength = currentData;
		var greaterLength = newData;
		if(currentData.length > newData.length){
			[shorterLength,greaterLength] = [newData,currentData];
		}
		
		shorterLength.forEach( function(sObj){
			var found = greaterLength.find( gObj => gObj.date === sObj.date);
			if(found){
				data.push( update(sObj, {$merge:found}) );
			}
		});
		return data;
	}
}

//manipulate data into right format
//e.g [{date:"Jan 04 1993", GOOGL:123, MSFT:456}, {date:"Jan 04 1993", GOOGL:124, MSFT:457}]
function manipulateData(json) {
	var dateMap = new Map();
	//each key is a date, each value is {GOOGL:123, MSFT:456}
	Object.keys(json).forEach(key => {
		dateMap.set(key, json[key]);
	});
	var data = [];
	dateMap.forEach(function(price,date) {
		var entry = new Object();
		entry["date"] = moment(date, "YYYY-MM-DD", true).format("DD MMM. YYYY");
		Object.keys(price).forEach(symbol => {
			entry[symbol] = price[symbol];
		});
		data.push(entry);
	});
	return data;
}

//get data from server and return formated data
function fetchData(budget, startDate, endDate, ticker) {
	return new Promise(function(resolve, reject) {
		const url = buildUrl(budget, startDate, endDate, ticker);
		console.log(url);
		//https://developers.google.com/web/updates/2015/03/introduction-to-fetch#sending_credentials_with_a_fetch_request
		fetch(url,{
			credentials: 'include',//crucial to have this to send session attributes, cookies,.... 
			headers: { 'Content-Type': 'application/json', 'Accept': 'application/json'  } 
		})
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(json) {
			if(!json){
				reject(new Error("not_found"));
			}else{
				//send notification data updated
				PubSub.publish("data_updated");
				resolve( manipulateData(json));
			}
		});
	});
}

//request and return new data from server for every stock in the stock symbols list
//nextInput is equivalent to nextProps.getState 
function fetchAllStocks(currentState){
	/*return new Promise(function(resolve, reject){
		var symbols = nextInput.symbols; 
		var fetchTasks= [];
		var data; 
		symbols.forEach(function(symbol){
			//a recent symbol added at the end of "symbols" list
			// must fetch the recent added symbol first to update mutual IPO and Delisting date at server side
			fetchTasks.unshift( function(callback) {
				fetchData( nextInput.budget, nextInput.start, nextInput.end, symbol)
				.then(function(newData){
					callback(null,newData);
				});
			});
		})
		async.parallel(fetchTasks, function (err,results) { 
			results.forEach( function(result) {
				if(data){
					data = mergeData(data,result);
				}else {
					data = result;
				}
			});
			resolve(data);
		});
	}, function(err){
		console.log(err);
	});*/
	
	return new Promise(function(resolve, reject) {
		const url = buildUrl(budget, startDate, endDate, ticker);
		console.log(url);
		var budget = currentState.budget;
		var startDate = currentState.start;
		var endDate = currentState.end;
		var symbols = currentState.symbols;
		//https://developers.google.com/web/updates/2015/03/introduction-to-fetch#sending_credentials_with_a_fetch_request
		fetch(url,{
			method: 'POST'
			credentials: 'include',//crucial to have this to send session attributes, cookies,.... 
			headers: { 'Content-Type': 'application/json', 'Accept': 'application/json'  },
			body: JSON.stringify({"budget": budget, "startdate": startDate, "enddate": endDate, "symbols": symbols})
		})
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(json) {
			if(!json){
				reject(new Error("not_found"));
			}else{
				//send notification data updated
				PubSub.publish("data_updated");
				resolve( manipulateData(json));
			}
		});
	});
}

class GraphContainer extends Component{
	constructor(props){
		super(props);
		this.state = {
			data: JSON.parse(sessionStorage.getItem('graph_data')) || []
		}
	}

	componentWillReceiveProps(nextProps){
		$(".spinner").show();
		var _self = this;
		var current = this.props.getState;
		var next = nextProps.getState;
		if((current.start!== next.start) || (current.end !== next.end) || (current.budget != next.budget)) {
			//at least one symbol in the list to fetch
			if(next.symbols.length){
				fetchAllStocks(next).then( function(newData){
					setStateAndSave(_self,newData);
					$(".spinner").hide();
				});
			}else{
				$(".spinner").hide();
				alert("Should have at least one symbol to display.")
			}
		//must compare length to avoid running into this "if" when a symbol removed
		}else if((current.symbols.length < next.symbols.length)) {
			fetchData(current.budget, current.start, current.end, getLastSymbol(next.symbols) )
			.then( function(newData) {
				var mergedData = mergeData(_self.state.data , newData);
				setStateAndSave(_self,mergedData);
				$(".spinner").hide();
			}).catch(function(err){
				$(".spinner").hide();
				if(err.message==="not_found"){
					var unfound = getLastSymbol(next.symbols);
					_self.props.deleteSymbol(getLastSymbol(next.symbols));
					alert("could not find the symbol: " + unfound);
				}
				console.log(err);
			});
		//when a symbol removed
		}else if (current.symbols.length > next.symbols.length){
			var deletedSymbol= next.deletedSymbol;
			//send notification data removed
			PubSub.publish("data_removed", deletedSymbol);
			var clonedData = _self.state.data.slice();
			clonedData.forEach( function(obj){
				delete obj[deletedSymbol];
			});
			setStateAndSave(_self, clonedData);
			$(".spinner").hide();
		}
	}

	componentWillMount() {
		var _self = this;
		if(this.props.getState.symbols.length != 0 && this.state.data.length == 0){
			fetchAllStocks(this.props.getState)
			.then( function(newData) {
				setStateAndSave(_self, newData);
			});
		}
	}
	
	render(){
		return(
			<Graph setClassName={this.props.setClassName}
				getSymbols={this.props.getState.symbols}
				getData={this.state.data}
			/>
		);
	}
}

export default GraphContainer;