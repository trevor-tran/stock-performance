import React, { Component } from "react";
import ReactDOM from "react-dom";

/**
 * @param num The number to round
 * @param precision The number of decimal places to preserve
 * @link https://stackoverflow.com/questions/5191088/how-to-round-up-a-number-in-javascript
 */
function roundUp(num, precision) {
  precision = Math.pow(10, precision)
  return Math.ceil(num * precision) / precision
}

class Table extends Component{
	constructor(props){
		super(props);
	}
	render(){
		//properties
		const name = this.props.setClassName;
		return(
			<table className={name} border="1" style={{cellpadding:"5"}}>
				<tbody>
					<tr>
						<th rowSpan="2">Symbol</th>
						<th colSpan="2">From 1/1/2013</th>
						<th colSpan="3">To 1/1/2016</th>
						<th rowSpan="2">Gain/Loss</th>
						<th rowSpan="2">Difference</th>
					</tr>
					<tr>
						<th>Price</th>
						<th>Quantity</th>
						<th>Price</th>
						<th>Quantity</th>
						<th>Balance</th>
					</tr>
				</tbody>
			</table>
		);
	}
}

export default Table;