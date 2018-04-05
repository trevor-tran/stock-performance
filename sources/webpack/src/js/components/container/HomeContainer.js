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
				investment: undefined,
				start: undefined,
				end: undefined,
				symbols: undefined,
				deletedSymbol: undefined
		};
		this.updateState = this.updateState.bind(this);
		this.removeSymbol = this.removeSymbol.bind(this);
	}
	
	//remove the symbol from this.state.symbol
	removeSymbol(deletedSymbol){
		var updatedSymbols = this.state.symbols.filter(symbol => symbol !== deletedSymbol);
		this.setState(() => {
			return{
				deletedSymbol,
				symbols:updatedSymbols
				}; 
		});
	}
	
	//set new state when new values received
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
	
	componentWillMount(){
		var _self = this;
		fetch("/signin/", {
			method: 'GET',
			credentials:'include',
			headers: {'Accept': 'application/json'},
		})
		.then( function(response){
			return response.json();
		})
		.then( function(json){
			console.log("loaded from server:",json);
			//NOTE: json.symbols is either empty or non-empty array
			//it means after calling updatState, this.state.symbols is not "undefined" anymore.
			_self.updateState(json.investment, json.startDate, json.endDate, json.symbols);
		});
	}
	
	
	render() {
		if (!this.state.symbols){			
			return false;
		}else{
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
							<GraphContainer	setClassName="graphcontainer" getState={this.state} deleteSymbol={this.removeSymbol} />
						</td>
						<td style={{verticalAlign:"top"}}>
							<ListContainer setClassName="listcontainer"	getSymbols={this.state.symbols} deleteSymbol={this.removeSymbol} />
						</td>
					</tr>
					<tr>
						<td colSpan="2"><h2>Summary Table</h2></td>
					</tr>
					{/*<tr>
						<td colSpan="2">
							<SummaryContainer setClassName="summarycontainer" />
						</td>
					</tr> */}
					<tr>
						<td><Spinner setClassName="spinner" /></td>
					</tr>
				</tbody>
				</table>
			);
		}
	}
}

const wrapper = document.getElementById("homecontainer");
wrapper ? ReactDOM.render(<HomeContainer />, wrapper) : false;

export default HomeContainer;
