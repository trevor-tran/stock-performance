import React, { Component } from "react";
import ReactDOM from "react-dom";
import PropTypes from "prop-types";
import $ from 'jquery';
import moment from 'moment';

class Input extends Component {
	constructor(props) {
		super(props);
		this.buttonClick = this.buttonClick.bind(this);
		this.catchEnter = this.catchEnter.bind(this);
	}
	
	buttonClick(e){
		e.preventDefault();
		e.stopPropagation();
		e.nativeEvent.stopImmediatePropagation();
		var invest = document.getElementById("invest").value;
		var start = document.getElementById("startDate").value;
		var end = document.getElementById("endDate").value;
		var symbol = document.getElementById("symbolInput").value.toUpperCase().replace(/\s+/g, '');
		this.props.onClickHandler(invest,start,end,symbol);
		document.getElementById("symbolInput").value ="";
	}
	
	catchEnter(e){
		if(e.keyCode === 13){
			this.buttonClick(e);
		}
	}
	render() {
		const name = this.props.setClass;
		const _self = this.props.getThis;
		return (
			<div className={name} >
				<label>Invest($):</label>
				<input 
					id="invest" 
					type="number" 
					defaultValue={_self.state.invest} 
					min={_self.state.limits["minInvest"]} 
					max={_self.state.limits["maxInvest"]} 
					onKeyUp={this.catchEnter}/>
				<label>From:</label>
				<input 
					id="startDate" 
					type="date" 
					defaultValue={_self.state.start}
					min={_self.state.limits["minDate"]}
					onKeyUp={this.catchEnter} required/>
				<label>To:</label>
				<input 
					id="endDate" 
					type="date" 
					defaultValue={_self.state.end} 
					max={_self.state.limits["maxDate"]} 
					onKeyUp={this.catchEnter} required/>
				<label>Symbol:</label>
				<input 
					type="text" 
					id="symbolInput" 
					onKeyUp={this.catchEnter} 
					placeholder="e.g. MSFT" />
				<button 
					type="button" 
					id="updatebutton" 
					onClick={this.buttonClick}>Update</button>	
			</div>
		);
	}
}

export default Input;