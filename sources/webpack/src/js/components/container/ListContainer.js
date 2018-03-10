import React, { Component } from "react";
import List from "../presentational/List";

class ListContainer extends Component{
	constructor(props){
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}
	
	handleDelete(deletedSymbol){
		this.props.onDelete(deletedSymbol);
	}
	
	render(){
		return(
			<List
				setClass={this.props.setClass}
				getSymbols = {this.props.getSymbols}
				handleDelete = {this.handleDelete}
			/>
		);
	}
}

export default ListContainer;