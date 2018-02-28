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
import Button from "../presentational/Button";

//return e.g.  http://localhost:4567/home/?money=1&start=1993-1-1&end=1994-1-2&symbol=AAPL
function setUrl (money, start, end, symbol) {
	var param="?money=" + money + "&start=" + start + "&end=" + end;
	if (symbol != "" && symbol!= null){
		param += '&symbol='+ symbol;
	}
	return 'http://localhost:4567/home/' + param;
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
				start: moment().utc().subtract(61,"days").format('YYYY-MM-DD'),
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
		this.enterKey = this.enterKey.bind(this);
		this.buttonHandler = this.buttonHandler.bind(this);
	}
	enterKey(e) {
		if(e.keyCode === 13){
			this.buttonHandler(e);
		}
	}
	//button event
	buttonHandler(e) {
		e.preventDefault();
		e.stopPropagation();
		e.nativeEvent.stopImmediatePropagation();
		var start = document.getElementById("startDate").value;
		var end = document.getElementById("endDate").value;
		//capitalize and trim spaces
		var symbol = document.getElementById("symbolInput").value.toUpperCase().replace(/\s+/g, '');
		if ( this.state.isExist(symbol) ) {
			alert(symbol + " is already added.")
		}
		else if (symbol != "" && symbol != null ) {
			var newSymbols = update(this.state.symbols, {$push:[symbol] });
			this.setState(() => {
				return {
					start:start,
					end:end,
					symbols: newSymbols };
			});
		}
		else {
			this.setState(() => {
				return {start,end};
			});
		}
		document.getElementById("symbolInput").value ="";
	}
	
	componentDidUpdate(nextProps, prevState) {
		//alert ("inside didUpdate");
		var _self = this;
		var money = this.state.money;
		var startDate = this.state.start;
		var endDate = this.state.end;		
		if(this.state.symbols !== prevState.symbols) {
			fetchData( _self.state.money, _self.state.start, _self.state.end, _self.state.getLast())
			.then( function(newData) {
				if (_self.state.data.length !== 0) {
					var data = [];
					var currentData = _self.state.data;
					for(var i = 0; i<currentData.length; i++){
							data.push( update( currentData[i] , {$merge : newData[i]} ));
						}
					_self.setState(() => {return{data}});
				} else {
					_self.setState(() => { return{data:newData}; });
				}
				sessionStorage.setItem('data', JSON.stringify(_self.state.data));
			});
		}
		if (this.state.start !== prevState.start || this.state.end !== prevState.end) {
			  var symbols = _self.state.symbols; 
			  var data = [];
			  async.each(symbols, function (symbol,callback) { 
				  fetchData( _self.state.money, _self.state.start, _self.state.end, symbol)
				  .then( function(newData) {
					  if(data.length != 0){
						  for( var i = 0; i<data.length; i++) {
							  data[i] = update(data[i], {$merge : newData[i]});
						  }
					  }
					  else {
						  data = newData;
					  }
					  callback();
				  });
				  
			  }, 
			  function() {
			  _self.setState(() => { return{data}; });
			  sessionStorage.setItem('data', JSON.stringify(_self.state.data));
			  });
		  }
	}
	
	componentDidMount() {
		var _self = this;
		
		//fetchAndProcessData(_self);
		
		fetchData( _self.state.money, _self.state.start, _self.state.end, _self.state.getLast())
		.then( function(newData) {
			_self.setState(() => { 
				return{data: newData }; 
			});
			sessionStorage.setItem('data', JSON.stringify(_self.state.data));
		});
		
	}
	
	render() {
		var lines = [];
		if (this.state.data != null){
			var colors = ['#8884d8', '#82ca9d', '#1c110a', '#8b2412','#f83581','#f07b50','#0c5e59','#0011ff','#e57cf9'];
			var colorIndex = 0;
			var obj1 = this.state.data[0]; // extract a object.
			// loop over obj1, get keys,but "date".Add <Line> for each key, which is stock symbol.
			$.each(obj1, function(key, value) {
				if(key!="date") {
					var color = colors [ colorIndex++ % colors.length]; // rotate colors
					lines.push(<Line type="monotone" key={key} dataKey={key} dot={false} unit=" USD" stroke={color}/>);
				}

			});
			return (
				<div id="parent">
					<div className="inputcontainer">
						<label>Invest($):</label>
						<input id="money" type="text" defaultValue={this.state.money} />
						<label>From:</label>
						<input id="startDate" type="date" defaultValue={this.state.start}/>
						<label>To:</label>
						<input id="endDate" type="date" defaultValue={this.state.end}/>
						<label>Symbol:</label>
						<input type="text" id="symbolInput" onKeyUp={this.enterKey} placeholder="e.g. AAPL,MSFT" />
						<Button type="button" id="symbolbutton" handleClick={this.buttonHandler} text="Update"></Button>	
					</div>
					<div id ="graphcontainer">
						<LineChart width={900} height={400} data={this.state.data} margin={{top: 5, right: 10, left: 10, bottom: 5}}>
							<XAxis dataKey="date" angle={-20} textAnchor="end" height={50} />
							<YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
							<CartesianGrid strokeDasharray="3 3"/>
							<Tooltip/>
							<Legend />
							{lines}	
						</LineChart>
					</div>
				</div>
			);
		}
		else {
			return false;
		}
	}
}

const wrapper = document.getElementById("homecontainer");
wrapper ? ReactDOM.render(<HomeContainer />, wrapper) : false;

export default HomeContainer;
