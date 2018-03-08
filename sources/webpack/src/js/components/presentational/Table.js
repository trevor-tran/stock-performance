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
		const _self = this.props.getThis;
		const name = this.props.setClass;
		if(_self.state.data.length != 0){
			const invest = _self.state.invest;
			const firstObj = _self.state.data[0];
			const lastObj = _self.state.data[_self.state.data.length-1];
			var rows=[];
			return(
				<table className={name} border="1"><tbody>
					<tr>
						<th rowSpan="2">Stock Symbol</th>
						<th>Balance</th>
						<th>Balance</th>
						<th rowSpan="2">Gain/Loss</th>
						<th rowSpan="2">Difference</th>
					</tr>
					<tr>
						<th>As {firstObj.date}</th>
						<th>As {lastObj.date}</th>
					</tr>
					{ _self.state.symbols.map((symbol)=>(
						<tr key={symbol}>
							<td>{symbol}</td>
							<td>${invest}</td>
							<td>${lastObj[symbol]}</td>
							<td>${roundUp(lastObj[symbol]-invest, 2)}</td>
							<td></td>
						</tr>
						
					))}
				</tbody></table>
			);
		}else{
			return false;
		}
	}
}

export default Table;