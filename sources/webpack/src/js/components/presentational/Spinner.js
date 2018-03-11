import React, { Component } from "react";
import $ from 'jquery';
import PropTypes from 'react';
import {GridLoader} from 'halogenium';

class Spinner extends Component {
	constructor(props){
		super(props);
	}
	render(){
		const name = this.props.setClassName;
		const overlay = {
			    background: "#e9e9e9",
				display: "none",
				position: "absolute",
				top: 0,
	    		right: 0,
	    		bottom: 0,
	    		left: 0,
			    opacity: 0.5,
		};
		
		const img = {
				position: "fixed",
				zIndex:20,
				top: "50%",
				left: "50%",
				transform: "translate(-50%, -50%)"
		};
		return(
			<div className={name} style={overlay}>
				<GridLoader style={img} color="#000080" size="20px" margin="10px"/>
			</div>
		);
	}
}
export default Spinner;