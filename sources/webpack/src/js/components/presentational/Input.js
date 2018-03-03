import React, { Component } from "react";
import ReactDOM from "react-dom";
import PropTypes from "prop-types";
import $ from 'jquery';

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
		var start = document.getElementById("startDate").value;
		var end = document.getElementById("endDate").value;
		var symbol = document.getElementById("symbolInput").value.toUpperCase().replace(/\s+/g, '');
		this.props.onClickHandler(start,end,symbol);
		document.getElementById("symbolInput").value ="";
	}
	
	catchEnter(e){
		if(e.keyCode === 13){
			this.buttonClick(e);
		}
	}
	render() {
		const name = this.props.setClass;
		const _self = this.props.setSelf;
		return (
			<div className={name} >
				<label>Invest($):</label>
				<input id="money" type="text" defaultValue={_self.state.money} />
				<label>From:</label>
				<input id="startDate" type="date" defaultValue={_self.state.start}/>
				<label>To:</label>
				<input id="endDate" type="date" defaultValue={_self.state.end}/>
				<label>Symbol:</label>
				<input type="text" id="symbolInput" onKeyUp={this.catchEnter} placeholder="e.g. AAPL,MSFT" />
				<button type="button" id="updatebutton" onClick={this.buttonClick}>Update</button>	
			</div>
		);
	}
}

export default Input;