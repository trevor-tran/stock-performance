import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';

//import presentational elements;
import Input from "../presentational/Input";
import Button from "../presentational/Button";



class HomeContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				data: [],
				symbol:'',
				url : window.location.href
		};
		this.buttonClickHandler = this.buttonClickHandler.bind(this);
		this.buttonClickHandler2 = this.buttonClickHandler2.bind(this);
		this.inputHandler = this.inputHandler.bind(this);
		this.fetchUrlAndProcessStockData = this.fetchUrlAndProcessStockData.bind(this);
	}
	
	
	fetchUrlAndProcessStockData(url,_self) {
		fetch(url, { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(json) {
			var _map = new Map();
			Object.keys(json).forEach(key => {
				_map.set(key, json[key]);
			});
			//array of objects. ex {date:"1993-1-1", GOOGL:123, MSFT:456}
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
			_self.setState({data:_input});

		})
		.catch(function(error) {
			console.log(error);
		});

	}

	buttonClickHandler() {
		var _self = this;
		var param="";
		if (this.state.symbol != "" && this.state.symbol != null){
			param = '?symbol='+ this.state.symbol;
		}
		var url = 'http://localhost:4567/home/' + param;
		console.log("url=", url);
		this.fetchUrlAndProcessStockData(url,_self);
		//alert("button clicked!");
		//this.forceUpdate();
	}
	
	
	inputHandler(e) {
		this.setState({symbol:e.target.value});
	}
	
	buttonClickHandler2() {
		alert("show!" + this.state.symbol);
		event.preventDefault();
	}
	
	componentDidMount() {
		var _self = this;
		//$('#symbolbutton').click(buttonClickHandler(_self));
		//following url does not work as having params
		var url = window.location.href;
		this.fetchUrlAndProcessStockData(url,_self);
		

	}

	render() {
		var lines = [];
		if (this.state.data != null){
			var colors = ['#603a93', '#02edc6', '#ffc875', '#bdf2ff', '#ffac97','#f07b50','#413e42','#77b82c','#5c8cb0','#e57cf9' ];
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
					<div className="symbolcontainer">
						<label>"Enter Stock Symbol:"</label>
						<Input 
							id="symbolinput" 
							type="text" 
							value={this.state.symbol}
							placeholder="e.g. AAPL,MSFT"
							handleChange={ this.inputHandler } 
						/>
						<Button type="button" id="symbolbutton" handleClick={this.buttonClickHandler} text="Go"></Button>	
					</div>
					<div id ="graphcontainer">
						<LineChart width={900} height={400} data={this.state.data} margin={{top: 5, right: 10, left: 10, bottom: 5}}>
							<XAxis dataKey="date" angle={-30} textAnchor="end" height={70} />
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
