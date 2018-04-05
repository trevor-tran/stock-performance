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
				budget: undefined,
				start: undefined,
				end: undefined,
				symbols: undefined,
				deletedSymbol: undefined
		};
		this.updateState = this.updateState.bind(this);
		this.removeSymbol = this.removeSymbol.bind(this);
		this.updateOnBackend = this.updateOnBackend.bind(this);
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
	//enteredSymbol can be either a symbol or a array of symbols
	updateState(budget,start,end,enteredSymbol){
		if(typeof enteredSymbol == "undefined"){
			this.setState(()=>{
				return{budget,start,end};
			});
		}else{
			var symbols = enteredSymbol;
			//enteredSymbol is not Array type
			if(!Array.isArray(enteredSymbol)){	
				//this.state.symbols is undefined
				if(!this.state.symbols){
					symbols = [enteredSymbol];
				}else{
					symbols = update(this.state.symbols, {$push:[enteredSymbol] });
				}
			}
			this.setState(()=>{
				return{budget,start,end, symbols};
			});
		}
		
	}
	
	updateOnBackend(budget,start,end,symbol) {
		var params = 'budget=' + budget + "&startdate=" + start + "&enddate=" + end;
		if(typeof enteredSymbol != "undefined"){
			param += "?symbol=" + symbol;
		}
		fetch(window.location.origin + '/update/', {
			method: 'POST',
			credentials:'include',
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			body: params
		}).then( function(response){
			//https://developers.google.com/web/updates/2015/03/introduction-to-fetch#response_types
			if (response.status > 300){
				alert("error");
			}
		});
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
			_self.updateState(json.budget, json.startDate, json.endDate, json.symbols);
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
