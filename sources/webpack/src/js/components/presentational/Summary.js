import React, { Component } from "react";
import ReactDOM from "react-dom";

/**
 * @param num The number to round
 * @param precision The number of decimal places to preserve
 * @link https://stackoverflow.com/questions/5191088/how-to-round-up-a-number-in-javascript
 */
function round(num, precision) {
  precision = Math.pow(10, precision)
  return Math.round(num * precision) / precision
}

class Summary extends Component{
	constructor(props){
		super(props);
	}
	render(){
		//properties
		const name = this.props.setClassName;
		const data = this.props.getData;
		if(data.length!==0){
			return(
				<table className={name} border="1" cellPadding="5">
				<tbody>
					<tr>
						<th rowSpan="2">Symbol</th>
						<th colSpan="3">Start Date: {data[0].startDate}</th>
						<th colSpan="3">End Date: {data[0].endDate}</th>
						<th rowSpan="2">Gain/Loss($)</th>
						</tr>
					<tr>
						<th>Price($)</th>
						<th>Quantity</th>
						<th>Balance($)</th>
						<th>Price($)</th>
						<th>Quantity</th>
						<th>Balance($)</th>
					</tr>
					{
						data.map( (obj)=>(
						<tr key={obj.symbol}>
							<td> {obj.symbol} </td>
							<td> {obj.startPrice} </td>
							<td> {round(obj.startQuantity, 4)} </td>
							<td> {obj.startBalance} </td>
							<td> {obj.endPrice} </td>
							<td> {round(obj.endQuantity, 4)} </td>
							<td> {obj.endBalance} </td>
							<td> {round(obj.endBalance-obj.startBalance, 2)} </td>
						</tr>
						))
					}
				</tbody>
				</table>
		);
	}else{return false;}
	}
}

export default Summary;