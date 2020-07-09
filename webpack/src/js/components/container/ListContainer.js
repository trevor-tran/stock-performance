import React, { Component } from "react";
import List from "../presentational/List";

class ListContainer extends Component{
	constructor(props){
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}
	
	handleDelete(deletedSymbol){
		this.props.deleteSymbol(deletedSymbol);
	}
	
	render(){
		return(
			<List
				setClassName={this.props.setClassName}
				getSymbols = {this.props.getSymbols}
				handleDelete = {this.handleDelete}
			/>
		);
	}
}

export default ListContainer;