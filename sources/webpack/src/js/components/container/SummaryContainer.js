import React, { Component } from "react";
import ReactDOM from "react-dom";
import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';
import $ from 'jquery';
import update from 'react-addons-update';
import moment from 'moment';
import PropTypes from 'react';
import async from 'async';

import Summary from "../presentational/Summary";

function fetchData() {
	return new Promise(function(resolve, reject) {
		//request json from server
		fetch("/summary/", { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } })
		.then(function(response) {
			// convert to JSON
			return response.json();
		})
		.then( function(newData){
			resolve(newData);
		})
		.catch(function(error) {
			console.log(error);
			reject(error);
		});
	});
}

class SummaryContainer extends Component{
	constructor(props){
		super(props);
		this.state = {
				data: []
		}
	}
	
	componentWillMount(){
		var _self = this;
		fetchData().then(function(data){
			console.log("type of summary:", typeof data);
			console.log(data);
			_self.setState((prevState) => {
				return{data: prevState.data.push(data) };
			});
			console.log(this.state.data);
		});
	}
	
	render(){
		return(
			<Summary 
				setClassName={this.props.setClassName}
			/>
		);
	}
}

export default SummaryContainer;