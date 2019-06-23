import React, { Component } from "react";

class List extends Component {
	constructor(props){
		super(props);
		this.removeHandler = this.removeHandler.bind(this);
	}

	removeHandler(e){
		this.props.handleDelete(e.target.id);
	}

	render() {
		const crossStyle = {color:"red",cursor:"pointer"};
		return (
				<table className={this.props.setClassName}>
				<tbody>
				{this.props.getSymbols.map( (symbol)=>(
						<tr key={symbol}>
							<td>{symbol}</td>
							<td id={symbol} style={crossStyle} title="delete" onClick={this.removeHandler}>&#10006;</td>
						</tr>	
				))}
				</tbody>
				</table>
				);
	}
}

export default List;