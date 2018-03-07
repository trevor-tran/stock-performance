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

//save data a Session Storage
function saveLocal(_self){
	sessionStorage.setItem('data', JSON.stringify(_self.state.data));	
}

//need two properties of limits obj: minInvest, maxInvest
function isInvestValidated(invest,limits){
	var min = limits["minInvest"]
	var max = limits["maxInvest"];
	if(invest>=min && invest<=max){
		return true;
	}else{
		alert("Investment must be at least $"+min+" and no more than $"+max);
		return false;
	}
}

//need two properties of limits obj: minDate, maxDate,dayInterrval
function isDateValidated(start,end,limits){
	if( !moment(start).isValid() || !moment(end).isValid() ){
		alert("Date is invalid.");
		return false;
	}else if( moment(end).diff(start,"days") < limits["dayInterval"]){
		alert("Start date must be at least "+ limits["dayInterval"] +" days prior to end date.");
		return false;
	}else if( moment(end).isAfter(limits["maxDate"]) ){
		alert("End date must be before today's date.");
		return false;
	}else if( moment(start).isBefore(limits["minDate"])) {	
		alert("Start date must be after 8 Mar. 1817");
		return false;
	}
	return true;
}

//return e.g.  http://localhost:4567/home/?invest=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function setUrl (invest, start, end, symbol) {
	var param="?invest=" + invest + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return 'http://localhost:4567/home/' + param;
}

function mergeData( currentData, newData){
	var data = [];
	//swap reference
	if(currentData.length < newData.length){
		[currentData, newData] = [newData,currentData];
	}
	//if found current obj in newData, merge and push to data arr
	//else, push current obj to data arr
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

class HomeContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				invest:1,
				start: moment().subtract(366,"days").format('YYYY-MM-DD'),
				end: moment().subtract(1,"days").format('YYYY-MM-DD'),
				symbols: ["MSFT"],
				data: JSON.parse(sessionStorage.getItem('data')) || [],
				limits: {
					maxDate: moment().subtract(1,"days").format('YYYY-MM-DD'),
					minDate: moment("1817-3-8","YYYY-MM-DD"),
					dayInterval:30,
					minInvest:1,
					maxInvest:10000000
				},
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
	
	updateHandler(invest,start,end,symbol) {
		var limits = this.state.limits
		if(isInvestValidated(invest,limits) && isDateValidated(start,end,limits)){
			if ( this.state.isExist(symbol) ) {
				alert(symbol + " is already added.");
				//when a new symbol added
			}else if (symbol != "" && symbol != null ) {
				var updatedSymbols = update(this.state.symbols, {$push:[symbol] });
				this.setState(() => {
					return {
						start:start,
						end:end,
						symbols: updatedSymbols };
				});
				//when dates changed
			}else {
				this.setState(() => { return {start,end}; });
			}
		}
	}

	componentDidUpdate(prevProps, prevState) {
		//alert ("in didUpdate");
		var _self = this;
		var invest = this.state.invest;
		var startDate = this.state.start;
		var endDate = this.state.end;
		//must compare length to avoid running into this "if" when a symbol removed
		if(this.state.symbols.length > prevState.symbols.length) {
			$(".spinner").show();
			fetchData( _self.state.invest, _self.state.start, _self.state.end, _self.state.getLast())
			.then( function(newData) {
				if (_self.state.data.length !== 0) {
					var data = mergeData(_self.state.data , newData);
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
					fetchData( _self.state.invest, _self.state.start, _self.state.end, symbol)
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
				_self.setState(() => { return{data}; });
				saveLocal(_self);
				$(".spinner").hide();
			});
		}
	}
	
	componentDidMount() {
		$(".spinner").show();
		var _self = this;
		fetchData( _self.state.invest, _self.state.start, _self.state.end, _self.state.getLast())
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
			<table className="homecontainer">
			<tbody>
				<tr>
					<td> <Input 
						setClass="inputcontainer" 
						getThis={this} 
						onClickHandler={this.updateHandler}
					/> </td>
				</tr>
				<tr>
					<td><table><tbody>
						<tr>
							<td> <Graph 
								setClass="graphcontainer" 
								symbols={this.state.symbols} 
								data={this.state.data} 
							/> </td>
							<td style={{verticalAlign:"top"}}> <List 
								setClass="symbolscontainer" 
								symbols={this.state.symbols} 
								handleDelete={this.deleteHandler}
							/> </td>
						</tr>
					</tbody></table></td>
				</tr>
				<tr>
					<td><Spinner setClass="spinner"/></td>
				</tr>
			</tbody>
			</table>
		);
	}
}

const wrapper = document.getElementById("homecontainer");
wrapper ? ReactDOM.render(<HomeContainer />, wrapper) : false;

export default HomeContainer;
