import React, { Component } from "react";
import ReactDOM from "react-dom";
//import Input from "../presentational/Input";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';


function fetchUrlAndProcessStockData(url, _self) {
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

function buttonClickHandler(_self) {
	var param="";
	if (document.getElementById("symbolinput").value!= "" && document.getElementById("symbolinput").value!=null){
		var symbol = document.getElementById("symbolinput").value;
		param = '?symbol='+ symbol;
	}
	var url = 'http://localhost:4567/home/' + param;
	console.log("url=", url);
	fetchUrlAndProcessStockData(url, _self);
}


class GraphContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				data: []
		};
	}

	


	/*
	processStockJson(json) {
		//map where key is date, and value is objects
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

	}
	 */

	componentDidMount() {
		var _self = this;
		$('#symbolbutton').click(alert("clicked!");
				//buttonClickHandler(_self));
		//following url does not work as having params
		//var url = 'http://localhost:4567/home/';
		//var url = window.location.href;
		//fetchUrlAndProcessStockData(url, _self);

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
							<LineChart width={900} height={400} data={this.state.data} margin={{top: 5, right: 10, left: 10, bottom: 5}}>
							<XAxis dataKey="date" angle={-30} textAnchor="end" height={70} />
							<YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
							<CartesianGrid strokeDasharray="3 3"/>
								<Tooltip/>
							<Legend />
							{lines}	
							</LineChart>
					);
		}
		else {
			return false;
		}
	}
}

const wrapper = document.getElementById("graph");
wrapper ? ReactDOM.render(<GraphContainer />, wrapper) : false;

export default GraphContainer;
