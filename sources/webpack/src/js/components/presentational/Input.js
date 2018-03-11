import React, { Component } from "react";

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
		var invest = document.getElementById("investment").value;
		var start = document.getElementById("startDate").value;
		var end = document.getElementById("endDate").value;
		var symbol = document.getElementById("symbolInput").value.toUpperCase().replace(/\s+/g, '');
		this.props.onClickHandler(invest,start,end,symbol);//prop event
		document.getElementById("symbolInput").value ="";
	}
	
	catchEnter(e){
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
				<label>Investment($):</label>
				<input 
					id="investment" 
					type="number" 
					defaultValue={inputContainerState.investment} 
					min={limits.minInvestment} 
					max={limits.maxInvestment} 
					onKeyUp={this.catchEnter}/>
				<label>From:</label>
				<input 
					id="startDate" 
					type="date" 
					defaultValue={inputContainerState.start}
					min={limits.minDate}
					onKeyUp={this.catchEnter} required/>
				<label>To:</label>
				<input 
					id="endDate" 
					type="date" 
					defaultValue={inputContainerState.end} 
					max={limits.maxDate} 
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