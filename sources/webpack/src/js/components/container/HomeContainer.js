import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';

import Spinner from "../presentational/Spinner";
import InputContainer from "../container/InputContainer";
import GraphContainer from "../container/GraphContainer";
import ListContainer from "../container/ListContainer";
import SummaryContainer from "../container/SummaryContainer";

class HomeContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				investment:1,
				start: moment().subtract(366,"days").format('YYYY-MM-DD'),
				end: moment().subtract(1,"days").format('YYYY-MM-DD'),
				symbols: ["MSFT"],
				deletedSymbol:"undefined"
		};
		this.updateState = this.updateState.bind(this);
		this.removeSymbol = this.removeSymbol.bind(this);
	}
	
	removeSymbol(deletedSymbol){
		//remove the symbol from the list
		var updatedSymbols = this.state.symbols.filter(symbol => symbol !== deletedSymbol);
		this.setState(() => {
			return{
				deletedSymbol,
				symbols:updatedSymbols
				}; 
		});
	}
	
	updateState(investment,start,end,symbols){
		if(typeof symbols == "undefined"){
			this.setState(()=>{
				return{investment,start,end};
			});
		}else{
			this.setState(()=>{
				return{investment,start,end,symbols};
			});
		}
	}
	
	render() {
		return (
			<table className="homecontainer">
			<tbody>
				<tr>
					<td colSpan="2">
						<InputContainer setClassName="inputcontainer" getState={this.state}	onUpdate={this.updateState} />
					</td>
				</tr>
				<tr>
					<td>
						<GraphContainer	setClassName="graphcontainer" getState={this.state}	/>
					</td>
					<td style={{verticalAlign:"top"}}>
						<ListContainer setClassName="listcontainer"	getSymbols={this.state.symbols} onDelete={this.removeSymbol} />
					</td>
				</tr>
				<tr>
					<td colSpan="2"><h3 style={{textAlign:"center"}}>Summary Table</h3></td>
				</tr>
				<tr>
					<td colSpan="2">
						<SummaryContainer setClassName="summarycontainer" />
					</td>
				</tr>
			</tbody>
			</table>
		);
	}
}

const wrapper = document.getElementById("homecontainer");
wrapper ? ReactDOM.render(<HomeContainer />, wrapper) : false;

export default HomeContainer;
