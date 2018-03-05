import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';
//import presentational elements;
import Graph from "../presentational/Graph";

function getData(){
	var data1 = [
		{"date":"2018-1-1", "MSFT":302,"AAPL":20, "GOOGL":900},
		{"date":"2018-1-2", "MSFT":202,"AAPL":50, "GOOGL":990},
		{"date":"2018-1-3", "MSFT":502,"AAPL":70, "GOOGL":1000},
		{"date":"2018-1-4", "MSFT":346,"AAPL":90, "GOOGL":1100},
		{"date":"2018-1-5", "MSFT":403,"AAPL":100, "GOOGL":1200},
		{"date":"2018-1-6", "MSFT":482,"AAPL":80, "GOOGL": 1090},
		{"date":"2018-1-7", "MSFT":590,"AAPL":120, "GOOGL": 1200}
	];
	
	var data2 = [
		{"date":"2018-1-1", "MSFT":302,"AAPL":20, "GOOGL":900},
		{"date":"2018-1-2", "MSFT":202,"AAPL":50,"GOOGL":990},
		{"date":"2018-1-3", "MSFT":502,"AAPL":70},
		{"date":"2018-1-4", "MSFT":346,"AAPL":90,"GOOGL":1100},
		{"date":"2018-1-5", "MSFT":403,"AAPL":100,"GOOGL":1200},
		{"date":"2018-1-6", "MSFT":482,"AAPL":80, "GOOGL": 1090},
		{"date":"2018-1-7", "MSFT":590,"AAPL":120, "GOOGL": 1200}
	];
	
	var data3 = [
		{"date":"2018-1-1", "MSFT":302,"AAPL":20},
		{"date":"2018-1-2", "MSFT":202,"AAPL":50, "GOOGL":990},
		{"date":"2018-1-3", "MSFT":502,"AAPL":70, "GOOGL":1000},
		{"date":"2018-1-4", "MSFT":346,"AAPL":90, "GOOGL":1100},
		{"date":"2018-1-5", "MSFT":403,"AAPL":100, "GOOGL":1200},
		{"date":"2018-1-6", "MSFT":482,"AAPL":80, "GOOGL": 1090},
		{"date":"2018-1-7", "MSFT":590,"AAPL":120, "GOOGL": 1200}
	];
	return data3;
}


class TestPurpose extends Component {
	constructor(props) {
		super(props);
		this.state = {
				money:'1',
				start: moment().utc().subtract(366,"days").format('YYYY-MM-DD'),
				end: moment().utc().subtract(1,"days").format('YYYY-MM-DD'),
				symbols: ["MSFT,GOOGL"],
				data:[]
		};
	}
	
	componentDidMount() {
		this.setState( ()=> { return{data:getData()}; });
	}

	render() {

		return (
			<table className="testcontainer">
			<tbody>
				<tr>
				<td> <Graph setClass="graphcontainer" data={this.state.data} /> </td>
				</tr>
			</tbody>
			</table>
		);

	}
}

const wrapper = document.getElementById("testpurpose");
wrapper ? ReactDOM.render(<TestPurpose />, wrapper) : false;

export default TestPurpose;
