import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';
//import presentational elements;
import Input from "../presentational/Input";
import Graph from "../presentational/Graph";
import List from "../presentational/List";
import Spinner from "../presentational/Spinner";


function saveLocal(_self){
	sessionStorage.setItem('data', JSON.stringify(_self.state.data));	
}


//return e.g.  http://localhost:4567/home/?money=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function setUrl (money, start, end, symbol) {
	var param="?money=" + money + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return 'http://localhost:4567/home/' + param;
}

//DOES NOT WORK AS EXPECTED
function mergeData( currentData, newData){
	var data = [];
	if(currentData.length < newData.length){
		var temp = currentData;
		currentData = newData;
		newData = temp;
	}
	for( var i=0; i<currentData.length;i++){
		for(var j=0;j<newData.length;j++){
			if(currentData[i]["date"] === newData[j]["date"]){
				data.push( update( currentData[j], {$merge : newData[j]} ));
				break;
			}
		}
		//data.push(currentObj);
	}
	return data;
}

//manipulate data into right format
//[{date:"Jan 04 1993", GOOGL:123, MSFT:456},{date:"Jan 04 1993", GOOGL:124, MSFT:457}]
function manipulateReceivedData(json) {
	var _map = new Map();
	Object.keys(json).forEach(key => {
		_map.set(key, json[key]);
	});
	var data = [];
	_map.forEach(function(price,date) {
		var entry = new Object();
		var d = new Date(date).toDateString().substring(4);//reformat date
		entry["date"]= d;
		Object.keys(price).forEach(symbol => {
			entry[symbol] = price[symbol];
		});
		data.push(entry);
	});
	return data;
}

function fetchData(money, startDate, endDate, ticker) {
	return new Promise(function(resolve, reject) {
		const url = setUrl(money, startDate, endDate, ticker);
		console.log("url=",url);
		//request json from server
		fetch(url, { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(json) {
			return manipulateReceivedData(json);
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

class HomeContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				money:'1',
				start: moment().utc().subtract(366,"days").format('YYYY-MM-DD'),
				end: moment().utc().subtract(1,"days").format('YYYY-MM-DD'),
				symbols: ["MSFT"],
				data: JSON.parse(sessionStorage.getItem('data')) || [],
				getLast: () => {
					return this.state.symbols[this.state.symbols.length-1];
				},
				isExist: (newSymbol) => {
					return this.state.symbols.includes(newSymbol); // case sensitive
				}
		};
		this.updateHandler = this.updateHandler.bind(this);
		this.deleteHandler = this.deleteHandler.bind(this);
	}
	
	deleteHandler(deletedSymbol){
		// make a clone to modify
		var updatedData = this.state.data.slice();
		//remove the deleted symbol from every obj
		updatedData.forEach( function(obj){
			delete obj[deletedSymbol];
		});
		//remove the symbol from the list
		var updatedSymbols = this.state.symbols.filter(symbol => symbol !== deletedSymbol);
		//set State and session storage
		this.setState({data:updatedData,symbols:updatedSymbols}, () => saveLocal(this));
	}
	
	updateHandler(start,end,symbol) {
		if ( this.state.isExist(symbol) ) {
			alert(symbol + " is already added.")
		}
		else if (symbol != "" && symbol != null ) {
			var updatedSymbols = update(this.state.symbols, {$push:[symbol] });
			this.setState(() => {
				return {
					start:start,
					end:end,
					symbols: updatedSymbols };
			});
		}
		else {
			this.setState(() => {
				return {start,end};
			});
		}
		//document.getElementById("symbolInput").value ="";
	}

	componentDidUpdate(prevProps, prevState) {
		//alert ("in didUpdate");
		var _self = this;
		var money = this.state.money;
		var startDate = this.state.start;
		var endDate = this.state.end;
		//when new stock symbol entered
		//must compare length to avoid running into it when a symbol removed
		if(this.state.symbols.length > prevState.symbols.length) {
			$(".spinner").show();
			fetchData( _self.state.money, _self.state.start, _self.state.end, _self.state.getLast())
			.then( function(newData) {
				if (_self.state.data.length !== 0) {
					var data = data = mergeData(_self.state.data , newData);
					/*
					var currentData = _self.state.data;
					for(var i = 0; i<currentData.length; i++){
						data.push( update( currentData[i] , {$merge : newData[i]} ));
					}*/
					_self.setState(() => {return{data}});
				} else {
					_self.setState(() => { return{data:newData}; });
				}
				saveLocal(_self);
				$(".spinner").hide();
			});
		}
		//when either start date or end date changed
		if (this.state.start !== prevState.start || this.state.end !== prevState.end) {
			$(".spinner").show();
			var symbols = _self.state.symbols; 
			var fetchTasks= [];
			var data; 
			symbols.forEach(function(symbol){
				fetchTasks.push( function(callback) {
					fetchData( _self.state.money, _self.state.start, _self.state.end, symbol)
					.then(function(newData){
						callback(null,newData);
					});
				});
			})
			async.parallel(fetchTasks, function (err,results) { 
				results.forEach( function(result) {
					if(data){
						data = mergeData(data,result);
						/*for( var i = 0; i<data.length; i++) {
							console.log("index i = ", i);
							data[i] = update(data[i], {$merge : result[i]});
						}*/
					}
					else {
						data = result;
					}
				});
				_self.setState(() => { return{data}; });
				saveLocal(_self);
				$(".spinner").hide();
			});

		}
	}

	componentDidMount() {
		$(".spinner").show();
		var _self = this;

		//fetchAndProcessData(_self);

		fetchData( _self.state.money, _self.state.start, _self.state.end, _self.state.getLast())
		.then( function(newData) {
			_self.setState(() => { 
				return{data: newData }; 
			});
			saveLocal(_self);
			$(".spinner").hide();
		});

	}

	render() {

		return (
			<div id="parent">
				<Input setClass="inputcontainer" setSelf={this} onClickHandler={this.updateHandler}/>
				<List setClass="symbolscontainer" symbols={this.state.symbols} handleDelete={this.deleteHandler}/>
				<Graph setClass="graphcontainer" data={this.state.data} />
				<Spinner setClass="spinner"/>
			</div>
		);

	}
}

const wrapper = document.getElementById("homecontainer");
wrapper ? ReactDOM.render(<HomeContainer />, wrapper) : false;

export default HomeContainer;
