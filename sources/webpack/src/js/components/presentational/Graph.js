import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, ResponsiveContainer, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import PropTypes from 'react';

class Graph extends Component {
	constructor(props){
		super(props);
	}
	render(){
		//properties
		const data = this.props.data;
		const name = this.props.setClass;
		let lines = [];
		if (data != null){
			var colors = ['#8884d8', '#82ca9d', '#595163', '#8b2412','#f83581','#f07b50','#0c5e59','#0011ff','#e57cf9'];
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
						<div className = {name}>
							<ResponsiveContainer  width='100%' height='100%'>
							<LineChart data={data}>
							<XAxis dataKey="date" angle={-20} textAnchor="end" height={50} />
							<YAxis label={{ value: 'U.S. dollars ($)', angle: -90, position: 'insideLeft' }} />
							<CartesianGrid strokeDasharray="3 3"/>
								<Tooltip/>
							<Legend />
							{lines}	
							</LineChart>
							</ResponsiveContainer>
						</div>
					);
		}
		else {
			return false;
		}
	}

}

export default Graph;