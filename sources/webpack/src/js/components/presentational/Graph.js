import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import PropTypes from 'react';

class Graph extends Component {
	constructor(props){
		super(props);
	}
	render(){
		const data = this.props.data;
		let lines = [];
		if (data != null){
			var colors = ['#8884d8', '#82ca9d', '#1c110a', '#8b2412','#f83581','#f07b50','#0c5e59','#0011ff','#e57cf9'];
			var colorIndex = 0;
			var obj1 = data[0]; // extract a object.
			// loop over obj1, get keys,but "date".Add <Line> for each key, which is stock symbol.
			$.each(obj1, function(key, value) {
				if(key!="date") {
					var color = colors [ colorIndex++ % colors.length]; // rotate colors
					lines.push(<Line type="monotone" key={key} dataKey={key} dot={false} unit=" USD" stroke={color}/>);
				}

			});
					return(
							<LineChart width={900} height={400} data={data} margin={{top: 5, right: 10, left: 10, bottom: 5}}>
							<XAxis dataKey="date" angle={-20} textAnchor="end" height={50} />
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

export default Graph;