import React, { Component } from "react";
import update from 'react-addons-update';
import moment from 'moment';
import PubSub from 'pubsub-js';

import Summary from "../presentational/Summary";

//merge current data and new obj
function mergeData( currentData, newObj){
	var clone = currentData.slice();
	var foundIndex = clone.findIndex( obj => obj.symbol === newObj.symbol);
	if(foundIndex !== -1){
		clone[foundIndex] = newObj;
	}else{
		clone.push(newObj);
	}
return clone;
}

//set new state and save data in session storage
function setStateAndSave(_self, data){
	_self.setState({data},() => {
		sessionStorage.setItem('summary_data', JSON.stringify(_self.state.data));
	});
}

//fetch summary from server
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
				data: JSON.parse(sessionStorage.getItem('summary_data')) || []
		}
	}
	
	componentWillMount(){
		var _self = this;
		//get notified from GraphContainer when data updated 
		PubSub.subscribe('data_updated', function(){
			fetchData().then(function(newObj){
				var data = mergeData(_self.state.data, newObj);
				setStateAndSave(_self,data);
			});
		});
		//get notified from GraphContainer when data removed
		PubSub.subscribe('data_removed',function(msg,deletedSymbol){
			var data = _self.state.data.filter(obj => obj.symbol !== deletedSymbol);
			setStateAndSave(_self,data);
		});

	}

	render(){
		return(
			<Summary 
				setClassName={this.props.setClassName}
				getData={this.state.data}
			/>
		);
	}
}

export default SummaryContainer;