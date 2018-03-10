import React, { Component } from "react";
import ReactDOM from "react-dom";
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';

import Graph from "../presentational/Graph";


//save data a Session Storage
function saveLocal(_self){
	sessionStorage.setItem('data', JSON.stringify(_self.state.data));	
}

//return e.g.  http://localhost:4567/home/?investment=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function setUrl (invest, start, end, symbol) {
	var param="?investment=" + invest + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return '/home/' + param;
}

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

function getLastSymbol(symbols){
	return symbols[symbols.length-1];
}

class GraphContainer extends Component{
	constructor(props){
		super(props);
		this.state = {
			data: JSON.parse(sessionStorage.getItem('data')) || []
		}
		this.symbols = this.props.getSymbols;
		this.investment = this.props.getInvestment;
		this.startDate = this.props.getStartDate;
		this.endDate = this.props.getEndDate;
	}
	
	componentWillReceiveProps(nextProps){
		console.log("ComponentWillReceiveProps:",nextProps);
		var _self = this;
		if((this.symbols.length < nextProps.getSymbols.length)) {
			fetchData(this.investment, this.startDate, this.endDate, getLastSymbol(nextProps.getSymbols) )
			.then( function(newData) {
				if (_self.state.data.length !== 0) {
					var data = mergeData(_self.state.data , newData);
					_self.setState(() => {return{data}});
				} else {
					_self.setState(() => { return{data:newData}; });
				}
				saveLocal(_self);
			});
			this.symbols = nextProps.getSymbols; //update
		}
		
	}
	
	componentWillMount() {
		var _self = this;
		fetchData(this.investment, this.startDate, this.endDate, getLastSymbol(this.symbols) )
		.then( function(newData) {
			_self.setState(() => { 
				return{data: newData }; 
			});
			saveLocal(_self);
		});
	}
	
	render(){
		return(
			<Graph setClass={this.props.setClass}
				getSymbols={this.props.getSymbols}//should not use this.symbols
				getData={this.state.data}
			/>
		);
	}
}

export default GraphContainer;