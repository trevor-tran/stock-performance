import React, { Component } from "react";
import $ from 'jquery';
import moment from 'moment';
import PropTypes from 'react';
import update from 'react-addons-update';

import Input from "../presentational/Input";

//need two properties of limits obj: minInvest, maxInvest
function isInvestmentValid(investment,limits){
	var min = limits["minInvestment"]
	var max = limits["maxInvestment"];
	if(investment>=min && investment<=max){
		return true;
	}else{
		alert("Investment must be at least $"+min+" and no more than $"+max);
		return false;
	}
}

//need two members of limits obj: minDate, maxDate,dayInterrval
function isDateValid(start, end, limits){
	if( !moment(start).isValid() || !moment(end).isValid() ){
		alert("Date is invalid.");
		return false;
	}else if( moment(end).diff(start,"days") < limits["dayInterval"]){
		alert("Start date must be at least "+ limits["dayInterval"] +" days prior to end date.");
		return false;
	}else if( moment(end).isAfter(limits["maxDate"]) ){
		alert("End date must be before today's date.");
		return false;
	}else if( moment(start).isBefore(limits["minDate"])) {	
		alert("Start date must be after 8 Mar. 1817");
		return false;
	}
	return true;
}

//get last symbol of array of symbols
function isSymbolExist(symbols, newSymbol){
	return symbols.includes(newSymbol);// case sensitive
}

class InputContainer extends Component{
	constructor(props){
		super(props);
		this.homeContainerState = this.props.getState;
		this.limits = {
			maxDate: moment().subtract(1,"days").format('YYYY-MM-DD'),
			minDate: moment("1817-3-8","YYYY-MM-DD"),
			dayInterval:30,
			minInvestment:1,
			maxInvestment:1000000
		};
		this.updateButtonHandler = this.updateButtonHandler.bind(this);
	}

	updateButtonHandler(investment,start,end,enteredSymbol) {
		var limits = this.limits;
		if(isInvestmentValid(investment,limits) && isDateValid(start,end,limits)){
			if ( isSymbolExist(enteredSymbol) ) {
				alert(symbol + " is already added.");
				//when a new symbol added
			}else if (enteredSymbol != "" && enteredSymbol != null ) {
				var updatedSymbols = update(this.homeContainerState.symbols, {$push:[enteredSymbol] });
				//setState in HomeContainer
				this.props.onUpdate(investment, start, end, updatedSymbols);
				//when dates changed
			}else {
				//setState in HomeContainer
				this.props.onUpdate(investment, start, end);
			}
		}
	}
	
	render(){
		const name = this.props.setClass;
		return(
			<Input
				setClass= {name}
				getState = {this.homeContainerState}
				getLimits = {this.limits}
				onClickHandler={this.updateButtonHandler}
			/>
		);
	}
}

export default InputContainer;