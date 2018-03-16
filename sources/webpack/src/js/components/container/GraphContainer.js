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

//return e.g.  http://localhost:4567/home/?investment=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function buildUrl (invest, start, end, symbol) {
	var param="?investment=" + invest + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return '/home/' + param;
}

//merge current and new data. Return merged data
function mergeData( currentData, newData){
	var data = [];
	if(currentData.length < newData.length){
		[currentData, newData] = [newData,currentData];
	}
	currentData.forEach( function(currentObj){
		var found = newData.find( newObj => newObj.date === currentObj.date);
		if(found){
			data.push( update(currentObj, {$merge:found}) );
		}else{
			data.push(currentObj);
		}
	});
	return data;
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
function fetchData(invest, startDate, endDate, ticker) {
	return new Promise(function(resolve, reject) {
		const url = buildUrl(invest, startDate, endDate, ticker);
		//request json from server
		fetch(url, { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			//send notifation data updated
			PubSub.publish("data_updated");
			// convert to JSON
			return response.json();
		})
		.then(function(json) {
			resolve( manipulateData(json));
		})
		.catch(function(error) {
			console.log(error);
		});
	});
}

//request and return new data from server for every stock in the stock symbols list
//nextInput is equivalent to nextProps.getState 
function fetchAllStock(nextInput){
	return new Promise(function(resolve, reject){
		var symbols = nextInput.symbols; 
		var fetchTasks= [];
		var data; 
		symbols.forEach(function(symbol){
			fetchTasks.push( function(callback) {
				fetchData( nextInput.investment, nextInput.start, nextInput.end, symbol)
				.then(function(newData){
					callback(null,newData);//callback(error,result)
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
		if((current.start!== next.start) || (current.end !== next.end) || (current.investment != next.investment)) {
			fetchAllStock(next).then( function(newData){
				setStateAndSave(_self,newData);
				$(".spinner").hide();
			});
			//must compare length to avoid running into this "if" when a symbol removed
		}else if((current.symbols.length < next.symbols.length)) {
			fetchData(current.investment, current.start, current.end, getLastSymbol(next.symbols) )
			.then( function(newData) {
				if (_self.state.data.length !== 0) {
					var mergedData = mergeData(_self.state.data , newData);
					setStateAndSave(_self,mergedData);
				} else {
					setStateAndSave(_self,newData);
				}
				$(".spinner").hide();
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
		var current = this.props.getState;
		if(_self.state.data.length === 0){
			fetchData(current.investment, current.start, current.end, getLastSymbol(current.symbols) )
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