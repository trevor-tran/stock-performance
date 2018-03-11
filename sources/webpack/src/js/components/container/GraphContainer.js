import React, { Component } from "react";
import update from 'react-addons-update';
import moment from 'moment';
import async from 'async';

import Graph from "../presentational/Graph";

//save data a Session Storage
function saveLocal(_self){
	sessionStorage.setItem('data', JSON.stringify(_self.state.data));	
}

//return last symbol in symbols list
function getLastSymbol(symbols){
	return symbols[symbols.length-1];
}

//return e.g.  http://localhost:4567/home/?investment=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function setUrl (invest, start, end, symbol) {
	var param="?investment=" + invest + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return '/home/' + param;
}

//merge current and new data
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
	var _map = new Map();
	Object.keys(json).forEach(key => {
		_map.set(key, json[key]);
	});
	var data = [];
	_map.forEach(function(price,date) {
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
		const url = setUrl(invest, startDate, endDate, ticker);
		console.log("url=",url);
		//request json from server
		fetch(url, { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(json) {
			return manipulateData(json);
		})
		.then( function(newData){
			resolve(newData);
		})
		.catch(function(error) {
			console.log(error);
			reject(error);
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
			data: JSON.parse(sessionStorage.getItem('data')) || []
		}
	}
	
	componentWillReceiveProps(nextProps){
		var _self = this;
		var current = this.props.getState;
		var next = nextProps.getState;
		new Promise(function(resolve, reject){ 	
			if((current.start!== next.start) || (current.end !== next.end) || (current.investment != next.investment)) {
				fetchAllStock(next).then( function(newData){
					resolve(newData);
				});
				//must compare length to avoid running into this "if" when a symbol removed
			}else if((current.symbols.length < next.symbols.length)) {
				fetchData(current.investment, current.start, current.end, getLastSymbol(next.symbols) )
				.then( function(newData) {
					if (_self.state.data.length !== 0) {
						resolve(mergeData(_self.state.data , newData));
					} else {
						resolve(newData);
					}
				});
				//when a symbol removed
			}else if (current.symbols.length > next.symbols.length){
				var deletedSymbol= next.deletedSymbol;
				var updatedData = _self.state.data.slice();
				updatedData.forEach( function(obj){
					delete obj[deletedSymbol];
				});
				resolve(updatedData);
			}
		})
		.then(function(newData){
			_self.setState({data:newData}, function(){
				saveLocal(_self);
			});
		}).catch(function(err){
			console.log(err);
		});
	}
	
	componentWillMount() {
		var _self = this;
		var current = this.props.getState;
		fetchData(current.investment, current.start, current.end, getLastSymbol(current.symbols) )
		.then( function(newData) {
			_self.setState(() => { 
				return{data: newData }; 
			});
			saveLocal(_self);
		});
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