import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';

//import presentational elements;
import Input from "../presentational/Input";
import Button from "../presentational/Button";

class HomeContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				money:'1',
				start: moment().local().subtract(31,"days").format('YYYY-MM-DD'),
				end: moment().local().subtract(1,"days").format('YYYY-MM-DD'),
				symbol:"",
				data: JSON.parse(sessionStorage.getItem('data')) || []
		};
		this.buttonClickHandler = this.buttonClickHandler.bind(this);
		this.buttonClickHandler2 = this.buttonClickHandler2.bind(this);
		this.moneyHandler = this.moneyHandler.bind(this);
		this.startDateHandler = this.startDateHandler.bind(this);
		this.endDateHandler = this.endDateHandler.bind(this);
		this.symbolHandler = this.symbolHandler.bind(this);
		this.resetInput = this.resetInput.bind(this);
		this.fetchUrlAndProcessStockData = this.fetchUrlAndProcessStockData.bind(this);
	}
	
	fetchUrlAndProcessStockData(_self) {
		//set URL
		var param="?money=" + _self.state.money + "&start=" + _self.state.start + "&end=" + _self.state.end;
		if (_self.state.symbol != "" && _self.state.symbol != null){
			param += '&symbol='+ this.state.symbol;
		}
		var url = 'http://localhost:4567/home/' + param;
		
		//request json from server
		fetch(url, { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		// manipulate data into right format
		//[{date:"Jan 04 1993", GOOGL:123, MSFT:456},{date:"Jan 04 1993", GOOGL:124, MSFT:457}]
		.then(function(json) {
			var _map = new Map();
			Object.keys(json).forEach(key => {
				_map.set(key, json[key]);
			});
			var _input = [];
			_map.forEach(function(price,date) {
				var entry = new Object();
				var d = new Date(date).toDateString().substring(4);//reformat date
				entry["date"]= d;
				Object.keys(price).forEach(symbol => {
					entry[symbol] = price[symbol];
				});
				_input.push(entry);
			});
			return _input;
		})
		//merge previous and new data 
		.then( function(_input) {
			
			if (_self.state.data.length != 0) {
				var temp = _self.state.data;
				var newData = [];
				for(var i = 0;i<temp.length; i++){
					newData.push( update( temp[i] , {$merge : _input[i]} ));
				}
				_self.setState({data:newData}); 
			}
			else {
				_self.setState({data:_input});
			}
		})
		//store data in session storage
		.then( function () {
			sessionStorage.setItem('data', JSON.stringify(_self.state.data));
		})
		.catch(function(error) {
			console.log(error);
		});
	}
	// Input Handers: money, dates, symbol
	moneyHandler(e) {
		this.setState({money:e.target.value});
	}
	startDateHandler(e) {
		this.setState({start:e.target.value});
	}
	endDateHandler(e) {
		this.setState({end:e.target.value});
		//alert(e.target.value);
	}
	symbolHandler(e) {
		this.setState({symbol:e.target.value});
	}
	resetInput(){
		this.setState({symbol:""});
	}
	
	buttonClickHandler() {
		var _self = this;
		//window.location.href = url;
		this.fetchUrlAndProcessStockData(_self);
		this.resetInput();
	}
	
	buttonClickHandler2() {
		alert("show!" + this.state.symbol);
		event.preventDefault();
	}
	
	componentDidMount() {
		var _self = this;
		//var url = window.location.href;
		this.fetchUrlAndProcessStockData(_self);
	}

	render() {
		var lines = [];
		if (this.state.data != null){
			var colors = ['#603a93', '#02edc6', '#ffc875', '#bdf2ff','#77b82c','#f07b50','#413e42','#5c8cb0','#e57cf9','#ffac97'];
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
						<Input 
							id="money" 
							type="text" 
							value={this.state.money}
							handleChange={ this.moneyHandler } 
						/>
						<label>From:</label>
						<Input 
							id="start" 
							type="date" 
							value= {this.state.start}
							handleChange={ this.startDateHandler } 
						/>
						<label>To:</label>
						<Input 
							id="end" 
							type="date" 
							value={this.state.end}
							handleChange={ this.endDateHandler } 
						/>
						<label>Symbol:</label>
						<Input 
							id="symbolinput" 
							type="text" 
							value={this.state.symbol}
							placeholder="e.g. AAPL,MSFT"
							handleChange={ this.symbolHandler } 
						/>
						<Button type="button" id="symbolbutton" handleClick={this.buttonClickHandler} text="Update"></Button>	
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
