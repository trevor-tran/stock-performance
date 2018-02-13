import React, { Component } from "react";
import ReactDOM from "react-dom";
//import Input from "../presentational/Input";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

const data1 = [
	{date: '10/20/1017', AAPL: 400, TSLA: 240, GOOGL:150},
	{date: '10/10/2018', AAPL: 200, TSLA: 390, GOOGL:560},
	{date: '10/20/2018', AAPL: 300, TSLA: 139, GOOGL:160},
	];

class GraphContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				data: false
		};
	}

	componentDidMount() {
		var _self = this;
		fetch('http://localhost:4567/stockdata/?user=phuong', { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then(function(data) {
			// save JSON stock data to render later
			console.log(data);
			_self.setState({data});
		})
		.catch(function(error) {
			console.log(error);
		});
	}
	render() {
		return (
				<LineChart width={600} height={300} data={data1}
				margin={{top: 5, right: 30, left: 20, bottom: 5}}>
				<XAxis dataKey="date"/>
				<YAxis/>
				<CartesianGrid strokeDasharray="3 3"/>
					<Tooltip/>
				<Legend />
				<Line type="monotone" dataKey="AAPL" stroke="#8884d8" />
					<Line type="monotone" dataKey="TSLA" stroke="#82ca9d" />
					<Line type="monotone" dataKey="GOOGL" stroke="#82449d" />
						</LineChart>
		);
	}
}

const wrapper = document.getElementById("graph");
wrapper ? ReactDOM.render(<GraphContainer />, wrapper) : false;

export default GraphContainer;
