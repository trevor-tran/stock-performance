import React, { Component } from "react";

class Input extends Component {
	constructor(props) {
		super(props);
		this.buttonClick = this.buttonClick.bind(this);
		this.keyUpHandler = this.keyUpHandler.bind(this);
	}
	
	buttonClick(e){
		e.preventDefault();
		e.stopPropagation();
		e.nativeEvent.stopImmediatePropagation();
		var budget = document.getElementById("budget").value;
		var start = document.getElementById("startDate").value;
		var end = document.getElementById("endDate").value;
		var symbol = document.getElementById("symbolInput").value.toUpperCase().replace(/\s+/g, '');
		this.props.onClickHandler(budget,start,end,symbol);//prop event
		document.getElementById("symbolInput").value ="";
	}
	
	keyUpHandler(e){
		//13 is the code enter key on keyboard.
		if(e.keyCode === 13){
			this.buttonClick(e);
		}
	}
	render() {
		const name = this.props.setClassName;
		const inputContainerState = this.props.getState;
		const limits = this.props.getLimits;
		return (
			<div className={name} >
				<label>Budget($):</label>
				<input 
					id="budget" 
					type="number" 
					defaultValue={inputContainerState.budget} 
					min={limits.minBudget} 
					max={limits.maxBudget} 
					onKeyUp={this.keyUpHandler}/>
				<label>From:</label>
				<input 
					id="startDate" 
					type="date" 
					defaultValue={inputContainerState.start}
					min={limits.minDate}
					onKeyUp={this.keyUpHandler} required/>
				<label>To:</label>
				<input 
					id="endDate" 
					type="date" 
					defaultValue={inputContainerState.end} 
					max={limits.maxDate} 
					onKeyUp={this.keyUpHandler} required/>
				<label>Symbol:</label>
				<input 
					type="text" 
					id="symbolInput" 
					onKeyUp={this.keyUpHandler} 
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