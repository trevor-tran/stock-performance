import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';

//import presentational elements;
import List from "../presentational/List";
import Spinner from "../presentational/Spinner";
import Table from "../presentational/Table";

import GraphContainer from "../container/GraphContainer";
import InputContainer from "../container/InputContainer";

class HomeContainer extends Component {
	constructor(props) {
		super(props);
		this.state = {
				investment:1,
				start: moment().subtract(366,"days").format('YYYY-MM-DD'),
				end: moment().subtract(1,"days").format('YYYY-MM-DD'),
				symbols: ["MSFT"]
		};
		this.deleteHandler = this.deleteHandler.bind(this);
		this.updateState = this.updateState.bind(this);
	}
	
	deleteHandler(deletedSymbol){
		// make a clone to modify
		var updatedData = this.state.data.slice();
		//remove the deleted symbol from every obj
		updatedData.forEach( function(obj){
			delete obj[deletedSymbol];
		});
		//remove the symbol from the list
		var updatedSymbols = this.state.symbols.filter(symbol => symbol !== deletedSymbol);
		//set State and session storage
		this.setState({data:updatedData,symbols:updatedSymbols}, () => saveLocal(this));
	}
/*
	componentDidUpdate(prevProps, prevState) {
		//alert ("in didUpdate");
		var _self = this;
		//must compare length to avoid running into this "if" when a symbol removed
		if((this.state.symbols.length > prevState.symbols.length)) {
			$(".spinner").show();
			fetchData( _self.state.investment, _self.state.start, _self.state.end, _self.state.getLast())
			.then( function(newData) {
				if (_self.state.data.length !== 0) {
					var data = mergeData(_self.state.data , newData);
					_self.setState(() => {return{data}});
				} else {
					_self.setState(() => { return{data:newData}; });
				}
				saveLocal(_self);
				$(".spinner").hide();
			});
		}
		//when either start date, end date, or investment changed
		if ( (this.state.start!== prevState.start) 
				|| (this.state.end !== prevState.end)
				|| (this.state.investment != prevState.investment)) {
			$(".spinner").show();
			var symbols = _self.state.symbols; 
			var fetchTasks= [];
			var data; 
			symbols.forEach(function(symbol){
				fetchTasks.push( function(callback) {
					fetchData( _self.state.investment, _self.state.start, _self.state.end, symbol)
					.then(function(newData){
						callback(null,newData);
					});
				});
			})
			async.parallel(fetchTasks, function (err,results) { 
				results.forEach( function(result) {
					if(data){
						data = mergeData(data,result);
					}else {
						data = result;
					}
				});
				_self.setState(() => { return{data}; });
				saveLocal(_self);
				$(".spinner").hide();
			});
		}
	}
*/	
	updateState(investment,start,end,symbols){
		if(typeof symbols !== "undefined"){
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
						<InputContainer 
							setClass="inputcontainer" 
							getState={this.state}
							onUpdate={this.updateState}
						/>
					</td>
				</tr>
				<tr>
					<td>
						<GraphContainer 
							setClass="graphcontainer"
							getInvestment={this.state.investment}
							getStartDate={this.state.start}
							getEndDate={this.state.end}
							getSymbols={this.state.symbols} 
						/>
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
