import React, { Component } from "react";
import {LineChart, ResponsiveContainer, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

class Graph extends Component {
	constructor(props){
		super(props);
	}
	render(){
		//properties
		const data = this.props.getData;
		const name = this.props.setClassName;
		const symbols = this.props.getSymbols;
		let lines = [];
		if (data != null){
			var colors = ['#8884d8', '#82ca9d','#e57cf9', '#8b2412','#f83581','#f07b50','#0c5e59','#0011ff','#595163'];
			var colorIndex = 0;
			symbols.forEach( function(symbol) {
				var color = colors [ colorIndex++ % colors.length]; // rotate colors
				lines.push(<Line type="monotone" key={symbol} dataKey={symbol} dot={false} unit=" USD" stroke={color}/>);
			});
					return(
						<div className = {name}>
							<ResponsiveContainer  width='100%' height='100%'>
							<LineChart data={data}>
							<XAxis dataKey="date" angle={-20} textAnchor="end" height={55} />
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